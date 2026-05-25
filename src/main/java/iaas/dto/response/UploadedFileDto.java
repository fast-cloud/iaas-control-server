package iaas.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record UploadedFileDto(
        String name,
        Long bytes,

        @JsonProperty("content_type")
        String contentType,

        String etag,

        @JsonProperty("last_modified")
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
        LocalDateTime lastModified
) {}
