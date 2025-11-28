package iaas.exception;

import iaas.dto.response.ErrorCode;
import lombok.Getter;

/**
 * 모든 커스텀 예외의 기본 클래스
 */
@Getter
public abstract class BaseException extends RuntimeException {
    private final ErrorCode errorCode;

    protected BaseException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    protected BaseException(ErrorCode errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }
}

