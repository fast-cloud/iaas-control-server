package iaas.exception;

/**
 * OpenStack API 호출 실패 예외
 */
public class OpenStackApiException extends RuntimeException {
    
    public OpenStackApiException(String message) {
        super(message);
    }
    
    public OpenStackApiException(String message, Throwable cause) {
        super(message, cause);
    }
}