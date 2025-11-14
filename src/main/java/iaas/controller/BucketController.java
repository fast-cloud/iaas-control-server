package iaas.controller;

import iaas.dto.request.BucketCreateRequestDto;
import iaas.dto.response.ApiResponseDto;
import iaas.dto.response.BucketCreateResponseDto;
import iaas.dto.response.ErrorCode;
import iaas.dto.response.SuccessCode;
import iaas.exception.EntityFuckException;
import iaas.service.BucketService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("/bucket")
@AllArgsConstructor
public class BucketController {
	private final BucketService bucketService;

	@GetMapping("/")
	public ApiResponseDto<BucketCreateResponseDto> createBucket(BucketCreateRequestDto requestDto) {
		try {
			bucketService.createBucket(requestDto);
		} catch (EntityFuckException e) {
			return ApiResponseDto.fail(ErrorCode.DATABASE_CONNECTION_ERROR);
		}
		return ApiResponseDto.success(SuccessCode.BUCKET_CREATE_SUCCESS);
	}
}
