package iaas.dto.response;

import lombok.Builder;
import lombok.Data;

/**
 * Template 정보 DTO (인스턴스 조회 시 포함)
 */
@Data
@Builder
public class TemplateDto {
    private String name;
    private String desc;
}

