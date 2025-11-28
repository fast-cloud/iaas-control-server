package iaas.scheduler;

import iaas.entity.Instance;
import iaas.service.InstanceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 시퀀스 다이어그램 2: 인스턴스 상태 동기화 스케줄러
 * - 매 30초~1분마다 실행
 * - OpenStack의 실제 서버 상태와 DB 상태를 비교하여 동기화
 */
@Slf4j
@Component
@EnableScheduling
@RequiredArgsConstructor
public class InstanceStatusSyncScheduler {

    private final InstanceService instanceService;

    /**
     * 매 1분마다 실행 (fixedDelay = 60000ms = 60초)
     * - fixedDelay: 이전 작업이 완료된 후 지정된 시간이 지나면 다음 작업 시작
     * - fixedRate를 사용하면 이전 작업 완료와 무관하게 주기적으로 실행
     * 
     * 시간 조정:
     * - 30초마다: fixedDelay = 30000
     * - 1분마다: fixedDelay = 60000
     * - 5분마다: fixedDelay = 300000
     */
    @Scheduled(fixedDelay = 60000, initialDelay = 10000)
    public void syncInstanceStatus() {
        try {
            log.debug("Starting instance status sync...");
            
            // 1. DB에서 모든 인스턴스 조회
            List<Instance> instances = instanceService.getAllInstances();
            
            if (instances.isEmpty()) {
                log.debug("No instances to sync");
                return;
            }
            
            log.info("Syncing {} instances with OpenStack", instances.size());
            
            // 2. 각 인스턴스의 상태를 OpenStack과 동기화
            for (Instance instance : instances) {
                // OpenStack UUID가 없는 경우 스킵 (아직 생성 중)
                if (instance.getOpenstackUuid() == null || instance.getOpenstackUuid().isEmpty()) {
                    log.debug("Skipping instance {} - no OpenStack UUID", instance.getInstanceId());
                    continue;
                }
                
                // 상태 동기화
                instanceService.syncInstanceStatus(instance);
            }
            
            log.info("Instance status sync completed");
            
        } catch (Exception e) {
            log.error("Error during instance status sync", e);
        }
    }
    
    /**
     * 선택적: PROVISIONING 상태인 인스턴스만 동기화 (더 자주 실행)
     * - 생성 중인 인스턴스는 더 자주 체크
     */
    /*
    @Scheduled(fixedDelay = 30000, initialDelay = 5000)
    public void syncProvisioningInstances() {
        try {
            log.debug("Syncing PROVISIONING instances...");
            
            List<Instance> instances = instanceService.getAllInstances().stream()
                    .filter(i -> "PROVISIONING".equals(i.getCachedStatus()))
                    .toList();
            
            if (instances.isEmpty()) {
                return;
            }
            
            log.info("Syncing {} PROVISIONING instances", instances.size());
            
            for (Instance instance : instances) {
                if (instance.getOpenstackUuid() != null && !instance.getOpenstackUuid().isEmpty()) {
                    instanceService.syncInstanceStatus(instance);
                }
            }
            
        } catch (Exception e) {
            log.error("Error during PROVISIONING instance sync", e);
        }
    }
    */
}

