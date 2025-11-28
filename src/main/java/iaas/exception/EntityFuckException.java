package iaas.exception;

/**
 * 엔티티 조회 실패 등의 비즈니스 로직 예외
 */
public class EntityFuckException extends RuntimeException {
    
    public EntityFuckException(String message) {
        super(message);
    }
    
    public EntityFuckException(String message, Throwable cause) {
        super(message, cause);
    }
}
