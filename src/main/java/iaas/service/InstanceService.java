package iaas.service;

import iaas.dto.request.InstanceCreateRequest;
import iaas.dto.response.InstanceResponseData;
import iaas.dto.response.TemplateDto;
import iaas.entity.Instance;
import iaas.entity.Template;
import jakarta.persistence.EntityNotFoundException;
import iaas.repository.InstanceRepository;
import iaas.repository.TemplateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openstack4j.model.compute.Server;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class InstanceService {

    private final InstanceRepository instanceRepository;
    private final TemplateRepository templateRepository;
    private final OpenStackService openStackService;

    /**
     * 시퀀스 다이어그램 1: GET /instances - userId로 인스턴스 목록 조회
     */
    @Transactional(readOnly = true)
    public List<InstanceResponseData> getInstances(String userId) {
        List<Instance> instances = instanceRepository.findByOwnerUserId(userId);
        
        return instances.stream()
                .map(this::convertToResponseData)
                .collect(Collectors.toList());
    }

    /**
     * 시퀀스 다이어그램 3: POST /iaas/compute - 인스턴스 생성 요청
     * 1단계: DB에 "BUILD" 상태로 저장
     * 2단계: 비동기로 OpenStack에 VM 생성 요청
     */
    @Transactional
    public InstanceResponseData createInstance(InstanceCreateRequest request, String userId) {
        log.info("Creating instance for user: {}, instanceName: {}", userId, request.getInstanceName());
        
        // Template 조회
        Template template = templateRepository.findById(request.getTemplateId())
                .orElseThrow(() -> new EntityNotFoundException("Template not found: " + request.getTemplateId()));
        
        // 1단계: DB에 BUILD 상태로 저장
        Instance instance = new Instance();
        instance.setInstanceName(request.getInstanceName());
        instance.setOwnerUserId(userId);
        instance.setCachedStatus("BUILD");
        instance.setTemplate(template);
        instance.setOpenstackUuid(""); // 초기값 (OpenStack 생성 후 업데이트)
        
        Instance savedInstance = instanceRepository.save(instance);
        log.info("Instance saved to DB with ID: {}, status: BUILD", savedInstance.getInstanceId());
        
        // 2단계: 비동기로 OpenStack VM 생성 요청
        requestOpenStackVmCreation(savedInstance, template);
        
        return convertToResponseData(savedInstance);
    }

    /**
     * 시퀀스 다이어그램 3 - 2단계: 비동기로 OpenStack에 VM 생성 요청
     * par (병렬 처리) - 비동기로 실행
     */
    @Async
    @Transactional
    public void requestOpenStackVmCreation(Instance instance, Template template) {
        try {
            log.info("Requesting OpenStack VM creation for instance: {}", instance.getInstanceId());
            
            // OpenStack API 호출 (동기 호출이지만 @Async로 인해 별도 스레드에서 실행)
            Server server = openStackService.createInstance(
                    instance.getInstanceName(),
                    template.getOpenstackFlavorId(),
                    template.getOpenstackImageId(),
                    template.getKeypairName(),
                    template.getOpenstackNetworkId()
            );
            
            // 성공: ACTIVE 상태로 업데이트
            log.info("OpenStack VM created successfully. UUID: {}, Status: {}", 
                    server.getId(), server.getStatus());
            
            // IP 주소 추출
            String ipAddress = openStackService.extractIpAddress(server);
            log.info("Instance {} assigned IP: {}", instance.getInstanceId(), ipAddress);
            
            updateInstanceStatus(instance.getInstanceId(), "ACTIVE", server.getId(), ipAddress);
            
        } catch (Exception e) {
            log.error("Failed to create OpenStack VM for instance: {}", instance.getInstanceId(), e);
            
            // 실패: ERROR 상태로 업데이트
            updateInstanceStatus(instance.getInstanceId(), "ERROR", "", null);
        }
    }

    /**
     * 인스턴스 상태 업데이트
     */
    @Transactional
    public void updateInstanceStatus(String instanceId, String status, String openstackUuid, String ipAddress) {
        Instance instance = instanceRepository.findById(instanceId)
                .orElseThrow(() -> new EntityNotFoundException("Instance not found: " + instanceId));
        
        instance.setCachedStatus(status);
        if (openstackUuid != null && !openstackUuid.isEmpty()) {
            instance.setOpenstackUuid(openstackUuid);
        }
        if (ipAddress != null && !ipAddress.isEmpty()) {
            instance.setIpAddress(ipAddress);
        }
        
        instanceRepository.save(instance);
        log.info("Instance {} status updated to: {}, IP: {}", instanceId, status, ipAddress);
    }

    /**
     * 시퀀스 다이어그램 2: 스케줄러가 호출 - 모든 인스턴스 조회
     */
    @Transactional(readOnly = true)
    public List<Instance> getAllInstances() {
        return instanceRepository.findAll();
    }

    /**
     * 시퀀스 다이어그램 2: 상태 동기화 - OpenStack 실제 상태와 DB 상태 비교 후 업데이트
     */
    @Transactional
    public void syncInstanceStatus(Instance instance) {
        try {
            // OpenStack에서 실제 상태 조회
            Server server = openStackService.getInstanceStatus(instance.getOpenstackUuid());
            
            if (server == null) {
                log.warn("Server not found in OpenStack: {}", instance.getOpenstackUuid());
                return;
            }
            
            String openstackStatus = server.getStatus().name();
            String ipAddress = openStackService.extractIpAddress(server);
            
            // DB 상태와 다르면 업데이트
            boolean needsUpdate = false;
            
            if (!openstackStatus.equals(instance.getCachedStatus())) {
                log.info("Syncing instance {} status: {} -> {}", 
                        instance.getInstanceId(), 
                        instance.getCachedStatus(), 
                        openstackStatus);
                instance.setCachedStatus(openstackStatus);
                needsUpdate = true;
            }
            
            // IP 주소가 변경되었거나 새로 할당된 경우
            if (ipAddress != null && !ipAddress.equals(instance.getIpAddress())) {
                log.info("Syncing instance {} IP: {} -> {}", 
                        instance.getInstanceId(), 
                        instance.getIpAddress(), 
                        ipAddress);
                instance.setIpAddress(ipAddress);
                needsUpdate = true;
            }
            
            if (needsUpdate) {
                instanceRepository.save(instance);
            }
            
        } catch (Exception e) {
            log.error("Failed to sync instance status: {}", instance.getInstanceId(), e);
        }
    }

    /**
     * Entity -> ResponseData 변환
     */
    private InstanceResponseData convertToResponseData(Instance instance) {
        // Template 정보 변환
        TemplateDto templateDto = null;
        if (instance.getTemplate() != null) {
            templateDto = TemplateDto.builder()
                    .name(instance.getTemplate().getTemplateName())
                    .desc(instance.getTemplate().getDescription())
                    .build();
        }
        
        return InstanceResponseData.builder()
                .instanceId(instance.getInstanceId())
                .instanceName(instance.getInstanceName())
                .status(instance.getCachedStatus())
                .ipAddress(instance.getIpAddress()) // IP 주소 추가
                .template(templateDto) // Template 정보 추가
                .createdAt(instance.getCreatedAt())
                .build();
    }
}

