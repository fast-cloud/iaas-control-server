package iaas.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.time.LocalDateTime;

/**
 * 파일 정보 DTO (명세서의 objects 배열 항목)
 */
@Builder
public record ObjectDto(
		String name,
		Long size,
		
		@JsonProperty("last_modified")
		@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm'Z'")
		LocalDateTime lastModified
) {}

