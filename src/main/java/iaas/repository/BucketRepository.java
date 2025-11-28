package iaas.repository;

import iaas.entity.Bucket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BucketRepository extends JpaRepository<Bucket, String> {
	// 사용자별 버킷 목록 조회
	List<Bucket> findByOwnerUserId(String ownerUserId);

	// 버킷을 생성할때 버킷 이름과 소유자로 버킷 중복을 확인함
	Optional<Bucket> findByBucketNameAndOwnerUserId(String bucketName, String ownerUserId);

	// 버킷 ID와 소유자로 버킷 조회할때 소유권 확인함
	Optional<Bucket> findByBucketIdAndOwnerUserId(String bucketId, String ownerUserId);
}
