package iaas.exception;

import iaas.dto.response.ErrorCode;

/**
 * 버킷을 찾을 수 없을 때 발생하는 예외
 */
public class BucketNotFoundException extends BusinessException {
    public BucketNotFoundException(String message) {
        super(ErrorCode.BUCKET_NOT_FOUND, message);
    }

    public BucketNotFoundException(String bucketName, String ownerUserId) {
        super(ErrorCode.BUCKET_NOT_FOUND, 
            String.format("버킷을 찾을 수 없거나 접근 권한이 없습니다. bucketName=%s, ownerUserId=%s", bucketName, ownerUserId));
    }
}

