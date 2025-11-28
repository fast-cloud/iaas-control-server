package iaas.exception;

import iaas.dto.response.ErrorCode;

/**
 * Swift API 호출 중 오류가 발생했을 때 발생하는 예외
 */
public class SwiftApiException extends ExternalApiException {
    public SwiftApiException(String message) {
        super(ErrorCode.SWIFT_API_ERROR, message);
    }

    public SwiftApiException(String message, Throwable cause) {
        super(ErrorCode.SWIFT_API_ERROR, message, cause);
    }

    public SwiftApiException(String operation, String containerName, Throwable cause) {
        super(ErrorCode.SWIFT_API_ERROR, 
            String.format("SwiftAPI Server %s 실패: containerName=%s", operation, containerName), 
            cause);
    }
}

