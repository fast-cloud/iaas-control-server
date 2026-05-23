package iaas.controller;

import iaas.dto.request.BucketCreateRequestDto;
import iaas.dto.response.ApiResponseDto;
import iaas.dto.response.BucketCreateResponseDto;
import iaas.dto.response.BucketListResponseDto;
import iaas.dto.response.BucketStatusResponseDto;
import iaas.dto.response.SuccessCode;
import iaas.service.BucketService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bucket")
@RequiredArgsConstructor
public class BucketController {

	private final BucketService bucketService;

	// 테스트용 하드코딩된 사용자 ID (인증 구현 시 JWT/세션으로 교체)
	private static final String TEST_USER_ID = "test-user-id";

	/**
	 * POST /iaas/bucket — 버킷 생성
	 */
	@PostMapping
	public ApiResponseDto<BucketCreateResponseDto> createBucket(
			@Valid @RequestBody BucketCreateRequestDto requestDto) {
		BucketCreateResponseDto response = bucketService.createBucket(requestDto, TEST_USER_ID);
		return ApiResponseDto.success(SuccessCode.BUCKET_CREATE_SUCCESS, response);
	}

	/**
	 * GET /iaas/bucket — 자신의 버킷 목록 조회
	 */
	@GetMapping
	public ApiResponseDto<List<BucketListResponseDto>> listBuckets() {
		List<BucketListResponseDto> response = bucketService.listBuckets(TEST_USER_ID);
		return ApiResponseDto.success(SuccessCode.BUCKET_LIST_SUCCESS, response);
	}

	/**
	 * GET /iaas/bucket?bucket={name} — 버킷 내 파일 목록 조회
	 */
	@GetMapping(params = "bucket")
	public ApiResponseDto<BucketStatusResponseDto> getBucketStatus(
			@RequestParam String bucket) {
		BucketStatusResponseDto response = bucketService.getBucketStatus(bucket, TEST_USER_ID);
		return ApiResponseDto.success(SuccessCode.BUCKET_SEARCH_SUCCESS, response);
	}
}
