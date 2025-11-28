package iaas.dto.response;

import lombok.Builder;

import java.util.List;

/**
 * 버킷 현황 조회 응답 DTO (명세서의 data 필드)
 */
@Builder
public record BucketStatusResponseDto(
		String bucket,
		List<ObjectDto> objects
) {}

