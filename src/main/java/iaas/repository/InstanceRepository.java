package iaas.repository;

import iaas.entity.Instance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import java.util.List;

public interface InstanceRepository extends JpaRepository<Instance, String> {

    // 상태 폴러(Poller)가 사용할 쿼리 메소드
    List<Instance> findByCachedStatus(String status);
    
    // userId로 인스턴스 목록 조회 (시퀀스 다이어그램 1번)
    // Template 정보를 함께 조회 (N+1 문제 방지)
    @EntityGraph(attributePaths = {"template"})
    List<Instance> findByOwnerUserId(String ownerUserId);
}