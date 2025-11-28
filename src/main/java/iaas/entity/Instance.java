package iaas.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import java.time.OffsetDateTime;

@Data
@Entity
@Table(name = "iaas_instance")
public class Instance {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "instance_id", length = 36)
    private String instanceId; // ERD의 varchar(36) -> UUID 문자열

    @Column(name = "instance_name", nullable = false)
    private String instanceName;

    @Column(name = "openstack_uuid", nullable = false)
    private String openstackUuid;

    @Column(name = "owner_user_id", nullable = false, length = 36)
    private String ownerUserId;

    @Column(name = "cached_status", nullable = false, length = 50)
    private String cachedStatus;
    
    @Column(name = "ip_address", length = 50)
    private String ipAddress; // OpenStack이 할당한 IP 주소

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private OffsetDateTime createdAt; // ERD의 timestamp

    // 시퀀스 다이어그램의 로직을 위해 어떤 템플릿으로 생성되었는지 참조합니다.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "template_id") // DB FK 이름
    private Template template;
}
