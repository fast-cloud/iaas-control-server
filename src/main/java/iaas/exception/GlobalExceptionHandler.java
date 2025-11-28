package iaas.exception;

import iaas.dto.response.ApiResponseDto;
import iaas.dto.response.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 전역 예외 처리 핸들러
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 엔티티 조회 실패 (404 Not Found)
     */
    @ExceptionHandler(EntityFuckException.class)
    public ResponseEntity<ApiResponseDto<Void>> handleEntityNotFoundException(EntityFuckException e) {
        log.error("Entity not found: {}", e.getMessage());
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponseDto.fail(ErrorCode.ENTITY_NOT_FOUND));
    }

    /**
     * OpenStack API 호출 실패 (500 Internal Server Error)
     */
    @ExceptionHandler(OpenStackApiException.class)
    public ResponseEntity<ApiResponseDto<Void>> handleOpenStackApiException(OpenStackApiException e) {
        log.error("OpenStack API error: {}", e.getMessage(), e);
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponseDto.fail(ErrorCode.OPENSTACK_API_ERROR));
    }

    /**
     * 일반적인 예외 처리 (500 Internal Server Error)
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponseDto<Void>> handleGeneralException(Exception e) {
        log.error("Unexpected error: {}", e.getMessage(), e);
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponseDto.fail(ErrorCode.INTERNAL_SERVER_ERROR));
    }
}

