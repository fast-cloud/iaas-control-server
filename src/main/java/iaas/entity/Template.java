package iaas.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "iaas_template")
public class Template {

    @Id
    @Column(name = "template_id")
    private Integer templateId;

    @Column(name = "template_name", nullable = false)
    private String templateName;

    @Column(columnDefinition = "text")
    private String description;

    @Column(name = "openstack_image_id", nullable = false)
    private String openstackImageId;

    @Column(name = "openstack_flavor_id", nullable = false)
    private String openstackFlavorId;

    // [중요] ERD에 없지만, 인스턴스 생성에 필수적이므로 추가했다고 가정한 컬럼들
    @Column(name = "openstack_network_id", nullable = false)
    private String openstackNetworkId;

    @Column(name = "keypair_name", nullable = false)
    private String keypairName;
}