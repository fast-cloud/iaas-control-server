package iaas.repository;

import iaas.entity.Bucket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BucketRepositoy extends JpaRepository<Bucket, String> {
}
