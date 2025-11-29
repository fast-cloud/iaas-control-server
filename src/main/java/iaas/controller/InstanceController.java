package iaas.controller;

import iaas.dto.request.InstanceCreateRequest;
import iaas.dto.response.ApiResponseDto;
import iaas.dto.response.InstanceResponseData;
import iaas.dto.response.SuccessCode;
import iaas.service.InstanceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/iaas/compute")
@RequiredArgsConstructor
public class InstanceController {

    private final InstanceService instanceService;
    
    // 테스트용 하드코딩된 사용자 ID
    private static final String TEST_USER_ID = "test-user-id";

    /**
     * 시퀀스 다이어그램 1: GET /iaas/compute - 인스턴스 목록 조회
     * 
     * 테스트용: 하드코딩된 userId 사용
     */
    @GetMapping
    public ResponseEntity<ApiResponseDto<List<InstanceResponseData>>> getInstances() {
        String userId = TEST_USER_ID;
        
        log.info("GET /iaas/compute - userId: {}", userId);
        
        List<InstanceResponseData> instances = instanceService.getInstances(userId);
        
        return ResponseEntity.ok(
                ApiResponseDto.success(SuccessCode.INSTANCE_LIST_SUCCESS, instances)
        );
    }

    /**
     * 시퀀스 다이어그램 3: POST /iaas/compute - 인스턴스 생성 요청
     * 
     * HTTP 202 Accepted를 반환합니다.
     * - 생성 시작은 즉시 반환 (비동기)
     * - 실제 OpenStack VM 생성은 백그라운드에서 진행
     * 
     * 테스트용: 하드코딩된 userId 사용
     */
    @PostMapping
    public ResponseEntity<ApiResponseDto<InstanceResponseData>> createInstance(
            @RequestBody InstanceCreateRequest request) {
        
        String userId = TEST_USER_ID;
        
        log.info("POST /iaas/compute - userId: {}, request: {}", userId, request);
        
        InstanceResponseData instance = instanceService.createInstance(request, userId);
        
        // HTTP 202 Accepted: 요청을 수락했지만 아직 처리 중
        return ResponseEntity.accepted()
                .body(ApiResponseDto.success(SuccessCode.INSTANCE_CREATE_ACCEPTED, instance));
    }
}

