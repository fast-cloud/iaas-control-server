package iaas.exception;

import iaas.dto.response.ErrorCode;

/**
 * 중복된 버킷 이름으로 생성 시도할 때 발생하는 예외
 */
public class DuplicateBucketException extends BusinessException {
    public DuplicateBucketException(String message) {
        super(ErrorCode.DUPLICATE_BUCKET_NAME, message);
    }

    public DuplicateBucketException(String bucketName, String ownerUserId) {
        super(ErrorCode.DUPLICATE_BUCKET_NAME, 
            String.format("이미 존재하는 버킷 이름입니다. bucketName=%s, ownerUserId=%s", bucketName, ownerUserId));
    }
}

