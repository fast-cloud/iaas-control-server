package iaas.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.util.List;

@Builder
public record BucketUploadResponseDto(
        String bucket,

        @JsonProperty("uploadCount")
        int uploadCount,

        List<UploadedFileDto> files
) {}
