package iaas.service;

import org.openstack4j.api.OSClient.OSClientV3;
import org.openstack4j.model.compute.Server;
import org.openstack4j.model.compute.ServerCreate;
import org.openstack4j.api.Builders;
import org.openstack4j.model.common.Identifier;
import org.openstack4j.model.image.Image;
import org.openstack4j.openstack.OSFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class OpenStackService {

    @Value("${openstack.auth.url}") private String authUrl;
    @Value("${openstack.user.name}") private String username;
    @Value("${openstack.user.password}") private String password;
    @Value("${openstack.project.name}") private String projectName;
    @Value("${openstack.domain.name}") private String domainName;

    /**
     * OpenStack 클라이언트를 생성합니다.
     * @Async 메서드에서 사용하기 위해 매번 새로 생성합니다.
     */
    private OSClientV3 createOpenStackClient() {
        return OSFactory.builderV3()
                .endpoint(authUrl)
                .credentials(username, password, Identifier.byName(domainName))
                .scopeToProject(Identifier.byName(projectName), Identifier.byName(domainName))
                .authenticate();
    }

    /**
     * 이미지 이름으로 이미지 ID를 조회합니다.
     * UUID 형식이 아니면 이름으로 조회하여 ID를 반환합니다.
     */
    private String resolveImageId(OSClientV3 osClient, String imageIdOrName) {
        if (imageIdOrName == null || imageIdOrName.isEmpty()) {
            throw new IllegalArgumentException("Image ID or name cannot be null or empty");
        }
        
        // UUID 형식인지 확인 (36자리 하이픈 포함)
        if (imageIdOrName.matches("^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$")) {
            return imageIdOrName; // 이미 UUID 형식이면 그대로 반환
        }
        
        // 이름으로 이미지 조회
        List<? extends Image> images = osClient.images().list();
        Optional<? extends Image> image = images.stream()
                .filter(img -> imageIdOrName.equals(img.getName()))
                .findFirst();
        
        if (image.isPresent()) {
            return image.get().getId();
        }
        
        throw new IllegalArgumentException("Image not found: " + imageIdOrName);
    }

    /**
     * OpenStack에 VM 생성을 요청합니다.
     */
    public Server createInstance(String serverName, String flavorId, String imageId, String keyPairName, String networkId) {
        // @Async 메서드에서 사용하기 위해 클라이언트를 새로 생성
        OSClientV3 osClient = createOpenStackClient();

        // 이미지 ID 또는 이름을 실제 이미지 ID로 변환
        String resolvedImageId = resolveImageId(osClient, imageId);

        // ⬇️ 여기가 핵심입니다. OSFactory가 아니라 Builders.server()를 사용합니다.
        ServerCreate serverCreate = Builders.server()
                .name(serverName)
                .flavor(flavorId)
                .image(resolvedImageId)  // 변환된 이미지 ID 사용
                .keypairName(keyPairName)
                .networks(List.of(networkId))
                .build();

        // 인증된 클라이언트로 API 호출
        return osClient.compute().servers().boot(serverCreate);
    }

    /**
     * OpenStack에서 특정 VM의 현재 상태를 조회합니다. (폴러가 사용)
     */
    public Server getInstanceStatus(String openstackUuid) {
        // 클라이언트를 새로 생성하여 사용
        OSClientV3 osClient = createOpenStackClient();
        return osClient.compute().servers().get(openstackUuid);
    }
    
    /**
     * OpenStack Server 객체에서 IP 주소를 추출합니다.
     * 여러 네트워크가 있을 경우 첫 번째 IP를 반환합니다.
     */
    public String extractIpAddress(Server server) {
        if (server == null || server.getAddresses() == null) {
            return null;
        }
        
        // Addresses는 Map<NetworkName, List<Address>> 형태
        // 첫 번째 네트워크의 첫 번째 IP 주소를 반환
        return server.getAddresses().getAddresses().values().stream()
                .flatMap(List::stream)
                .filter(addr -> addr.getType() != null && "fixed".equals(addr.getType()))
                .map(addr -> addr.getAddr())
                .findFirst()
                .orElse(null);
    }
}