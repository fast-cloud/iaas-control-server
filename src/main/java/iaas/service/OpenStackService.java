package iaas.service;

import lombok.RequiredArgsConstructor;
import org.openstack4j.api.OSClient.OSClientV3;
import org.openstack4j.model.compute.Server;
import org.openstack4j.model.compute.ServerCreate;
import org.openstack4j.api.Builders;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OpenStackService {

    // Config에서 등록한 인증된 클라이언트 빈(Bean)을 주입받음
    private final OSClientV3 osClient;

    /**
     * OpenStack에 VM 생성을 요청합니다.
     */
    public Server createInstance(String serverName, String flavorId, String imageId, String keyPairName, String networkId) {

        // ⬇️ 여기가 핵심입니다. OSFactory가 아니라 Builders.server()를 사용합니다.
        ServerCreate serverCreate = Builders.server()
                .name(serverName)
                .flavor(flavorId)
                .image(imageId)
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