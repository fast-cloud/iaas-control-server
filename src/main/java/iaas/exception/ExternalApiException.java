package iaas.exception;

import iaas.dto.response.ErrorCode;
import lombok.Getter;

/**
 * 외부 API 호출 예외의 기본 클래스
 */
@Getter
public abstract class ExternalApiException extends BaseException {
    protected ExternalApiException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    protected ExternalApiException(ErrorCode errorCode, String message, Throwable cause) {
        super(errorCode, message, cause);
    }
}

