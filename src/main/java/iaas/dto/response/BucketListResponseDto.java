package iaas.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record BucketListResponseDto(
		@JsonProperty("bucket_id") String bucketId,
		String name,
		String status,

		@JsonProperty("created_at")
		@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm'Z'")
		LocalDateTime createdAt
) {}
