package iaas.controller;

import iaas.dto.request.BucketCreateRequestDto;
import iaas.dto.response.ApiResponseDto;
import iaas.dto.response.BucketCreateResponseDto;
import iaas.dto.response.BucketStatusResponseDto;
import iaas.dto.response.SuccessCode;
import iaas.service.BucketService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/iaas/bucket")
@AllArgsConstructor
public class BucketController {
	private final BucketService bucketService;

	/**
	 * 버킷 생성
	 * POST /iaas/bucket
	 * 
	 * @param requestDto 버킷 생성 요청 DTO
	 * @param ownerUserId 소유자 ID (Header: X-User-Id)
	 * @return 생성된 버킷 정보
	 */
	@PostMapping
	public ApiResponseDto<BucketCreateResponseDto> createBucket(
			@Valid @RequestBody BucketCreateRequestDto requestDto,
			@RequestHeader("X-User-Id") String ownerUserId) {
		BucketCreateResponseDto response = bucketService.createBucket(requestDto, ownerUserId);
		return ApiResponseDto.success(SuccessCode.BUCKET_CREATE_SUCCESS, response);
	}

	/**
	 * 버킷 현황 조회 (시퀀스 다이어그램 반영)
	 * GET /iaas/bucket?bucket=버킷이름
	 * 
	 * @param bucket 버킷 이름 (Query Parameter)
	 * @param ownerUserId 소유자 ID (Header: X-User-Id)
	 * @return 버킷 현황 정보 (버킷 이름 및 파일 목록)
	 */
	@GetMapping
	public ApiResponseDto<BucketStatusResponseDto> getBucketStatus(
			@RequestParam String bucket,
			@RequestHeader("X-User-Id") String ownerUserId) {
		BucketStatusResponseDto response = bucketService.getBucketStatus(bucket, ownerUserId);
		return ApiResponseDto.success(SuccessCode.BUCKET_SEARCH_SUCCESS, response);
	}


}
