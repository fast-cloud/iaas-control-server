package iaas.config;

import iaas.dto.response.ApiResponseDto;
import iaas.dto.response.ErrorCode;
import iaas.exception.BaseException;
import iaas.exception.OpenStackApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.persistence.EntityNotFoundException;

/**
 * 전역 예외 처리 핸들러
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 커스텀 예외 처리 (BaseException을 상속한 모든 예외)
     */
    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ApiResponseDto<?>> handleBaseException(BaseException e) {
        log.error("Custom Exception 발생: {}", e.getMessage(), e);
        return ResponseEntity
                .status(getHttpStatus(e.getErrorCode()))
                .body(ApiResponseDto.fail(e.getErrorCode()));
    }

    /**
     * JPA EntityNotFoundException 처리
     */
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiResponseDto<?>> handleEntityNotFoundException(EntityNotFoundException e) {
        log.error("Entity Not Found: {}", e.getMessage(), e);
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ApiResponseDto.fail(ErrorCode.ENTITY_NOT_FOUND));
    }

    /**
     * 요청 바디 검증 실패 (@Valid)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponseDto<?>> handleValidationException(MethodArgumentNotValidException e) {
        FieldError fieldError = e.getBindingResult().getFieldError();
        String message = fieldError != null ? fieldError.getDefaultMessage() : "유효하지 않은 요청입니다.";
        log.error("Validation Error: {}", message);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponseDto.fail(ErrorCode.INVALID_PARAMETER));
    }

    /**
     * 필수 헤더 누락
     */
    @ExceptionHandler(MissingRequestHeaderException.class)
    public ResponseEntity<ApiResponseDto<?>> handleMissingRequestHeaderException(MissingRequestHeaderException e) {
        log.error("Missing Required Header: {}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponseDto.fail(ErrorCode.MISSING_REQUIRED_HEADER));
    }

    /**
     * 필수 파라미터 누락
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiResponseDto<?>> handleMissingServletRequestParameterException(
            MissingServletRequestParameterException e) {
        log.error("Missing Required Parameter: {}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponseDto.fail(ErrorCode.MISSING_REQUIRED_PARAMETER));
    }

    /**
     * OpenStack API 호출 실패
     */
    @ExceptionHandler(OpenStackApiException.class)
    public ResponseEntity<ApiResponseDto<?>> handleOpenStackApiException(OpenStackApiException e) {
        log.error("OpenStack API error: {}", e.getMessage(), e);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponseDto.fail(ErrorCode.OPENSTACK_API_ERROR));
    }

    /**
     * 기타 예외 처리 (예상치 못한 예외)
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponseDto<?>> handleException(Exception e) {
        log.error("Unexpected Exception 발생: {}", e.getMessage(), e);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponseDto.fail(ErrorCode.INTERNAL_SERVER_ERROR));
    }

    /**
     * ErrorCode에 따른 HTTP Status 결정
     */
    private HttpStatus getHttpStatus(ErrorCode errorCode) {
        int code = errorCode.getCode();
        
        if (code >= 40000 && code < 40100) {
            return HttpStatus.BAD_REQUEST;
        } else if (code >= 40100 && code < 40300) {
            return HttpStatus.UNAUTHORIZED;
        } else if (code >= 40300 && code < 40400) {
            return HttpStatus.FORBIDDEN;
        } else if (code >= 40400 && code < 40500) {
            return HttpStatus.NOT_FOUND;
        } else if (code >= 40500 && code < 40900) {
            return HttpStatus.METHOD_NOT_ALLOWED;
        } else if (code >= 40900 && code < 42200) {
            return HttpStatus.CONFLICT;
        } else if (code >= 42200 && code < 50000) {
            return HttpStatus.UNPROCESSABLE_ENTITY;
        } else if (code >= 50000 && code < 50300) {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        } else {
            return HttpStatus.SERVICE_UNAVAILABLE;
        }
    }
}