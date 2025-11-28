package iaas.exception;

import iaas.dto.response.ErrorCode;
import lombok.Getter;

/**
 * 비즈니스 로직 예외의 기본 클래스
 */
@Getter
public abstract class BusinessException extends BaseException {
    protected BusinessException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    protected BusinessException(ErrorCode errorCode, String message, Throwable cause) {
        super(errorCode, message, cause);
    }
}

