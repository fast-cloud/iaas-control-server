package iaas.dto.response;

import lombok.Builder;
import lombok.Data;
import java.time.OffsetDateTime;

// Response JSON의 "data" 객체
@Data
@Builder
public class InstanceResponseData {
    private String instanceId;
    private String instanceName;
    private String status; // (cached_status)
    private String ipAddress; // OpenStack이 할당한 IP 주소
    private TemplateDto template; // 템플릿 정보 객체
    private OffsetDateTime createdAt;
    
    // 참고: openstackUuid는 내부 관리용이므로 응답에서 제외
    // 명세서에 없는 필드는 노출하지 않음
}