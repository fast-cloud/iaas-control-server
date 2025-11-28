package iaas.dto.request;

import lombok.Data;

// Request JSON 본문
@Data
public class InstanceCreateRequest {
    private String instanceName;
    private Integer templateId;
}