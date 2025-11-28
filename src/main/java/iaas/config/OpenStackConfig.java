package iaas.config;

import org.openstack4j.api.OSClient.OSClientV3;
import org.openstack4j.model.common.Identifier;
import org.openstack4j.openstack.OSFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenStackConfig {

    @Value("${openstack.auth.url}") private String authUrl;
    @Value("${openstack.user.name}") private String username;
    @Value("${openstack.user.password}") private String password;
    @Value("${openstack.project.name}") private String projectName;
    @Value("${openstack.domain.name}") private String domainName;

    /**
     * 애플리케이션 로드 시 1회 실행되어, 인증된 OSClientV3 객체를
     * Spring Bean (싱글톤)으로 등록합니다.
     * 이 객체는 토큰 만료를 자동으로 관리합니다.
     */
    @Bean
    public OSClientV3 openstackClient() {
        return OSFactory.builderV3()
                .endpoint(authUrl)
                // ⬇️ OSFactory.domainName() -> Identifier.byName()
                .credentials(username, password, Identifier.byName(domainName))
                // ⬇️ OSFactory.projectName() -> Identifier.byName()
                .scopeToProject(Identifier.byName(projectName), Identifier.byName(domainName))
                .authenticate();
    }
}