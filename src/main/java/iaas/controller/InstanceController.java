package iaas.controller;

import iaas.dto.request.InstanceCreateRequest;
import iaas.dto.response.ApiResponseDto;
import iaas.dto.response.InstanceResponseData;
import iaas.dto.response.SuccessCode;
import iaas.service.InstanceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/iaas/compute")
@RequiredArgsConstructor
public class InstanceController {

    private final InstanceService instanceService;

    /**
     * 시퀀스 다이어그램 1: GET /iaas/compute - 인스턴스 목록 조회
     * 
     * 사용자 인증 방법:
     * Option 1: @AuthenticationPrincipal로 JWT에서 userId 추출 (현재 구현)
     * Option 2: @RequestHeader("X-User-Id")로 API Gateway에서 전달받은 userId 사용
     * Option 3: @RequestParam("userId")로 파라미터로 전달 (비권장)
     */
    @GetMapping
    public ResponseEntity<ApiResponseDto<List<InstanceResponseData>>> getInstances(
            @AuthenticationPrincipal UserDetails userDetails) {
        
        // Option 1: JWT에서 userId 추출 (Spring Security 사용 시)
        String userId = userDetails.getUsername();
        
        log.info("GET /iaas/compute - userId: {}", userId);
        
        List<InstanceResponseData> instances = instanceService.getInstances(userId);
        
        return ResponseEntity.ok(
                ApiResponseDto.success(SuccessCode.INSTANCE_LIST_SUCCESS, instances)
        );
    }
    
    /**
     * Option 2: API Gateway에서 헤더로 userId를 전달받는 경우
     * (위 메소드 대신 이것을 사용할 수도 있습니다)
     */
    /*
    @GetMapping
    public ResponseEntity<ApiResponseDto<List<InstanceResponseData>>> getInstances(
            @RequestHeader("X-User-Id") String userId) {
        
        log.info("GET /iaas/compute - userId: {}", userId);
        
        List<InstanceResponseData> instances = instanceService.getInstances(userId);
        
        return ResponseEntity.ok(
                ApiResponseDto.success(SuccessCode.INSTANCE_LIST_SUCCESS, instances)
        );
    }
    */

    /**
     * 시퀀스 다이어그램 3: POST /iaas/compute - 인스턴스 생성 요청
     * 
     * HTTP 202 Accepted를 반환합니다.
     * - 생성 시작은 즉시 반환 (비동기)
     * - 실제 OpenStack VM 생성은 백그라운드에서 진행
     */
    @PostMapping
    public ResponseEntity<ApiResponseDto<InstanceResponseData>> createInstance(
            @RequestBody InstanceCreateRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        String userId = userDetails.getUsername();
        
        log.info("POST /iaas/compute - userId: {}, request: {}", userId, request);
        
        InstanceResponseData instance = instanceService.createInstance(request, userId);
        
        // HTTP 202 Accepted: 요청을 수락했지만 아직 처리 중
        return ResponseEntity.accepted()
                .body(ApiResponseDto.success(SuccessCode.INSTANCE_CREATE_ACCEPTED, instance));
    }
    
    /**
     * Option 2: API Gateway에서 헤더로 userId를 전달받는 경우
     */
    /*
    @PostMapping
    public ResponseEntity<ApiResponseDto<InstanceResponseData>> createInstance(
            @RequestBody InstanceCreateRequest request,
            @RequestHeader("X-User-Id") String userId) {
        
        log.info("POST /iaas/compute - userId: {}, request: {}", userId, request);
        
        InstanceResponseData instance = instanceService.createInstance(request, userId);
        
        return ResponseEntity.accepted()
                .body(ApiResponseDto.success(SuccessCode.INSTANCE_CREATE_ACCEPTED, instance));
    }
    */
}

