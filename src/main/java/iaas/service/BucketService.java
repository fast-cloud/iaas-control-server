package iaas.service;

import iaas.dto.request.BucketCreateRequestDto;
import iaas.dto.response.BucketCreateResponseDto;
import iaas.dto.response.BucketStatusResponseDto;
import iaas.dto.response.ObjectDto;
import iaas.entity.Bucket;
import iaas.repository.BucketRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openstack4j.api.OSClient.OSClientV3;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class BucketService {
	private BucketRepository bucketRepository;
	private OSClientV3 openstackClient;

	/**
	 * SwiftAPI Server에 컨테이너 생성 요청
	 * @param containerName 컨테이너(버킷) 이름
	 * @return 생성 성공 여부
	 */
	private boolean createContainerInSwift(String containerName) {
		try {
			log.info("SwiftAPI Server에 컨테이너 생성 요청: {}", containerName);
			openstackClient.objectStorage().containers().create(containerName);
			log.info("SwiftAPI Server 컨테이너 생성 성공: {}", containerName);
			return true;
		} catch (Exception e) {
			log.error("SwiftAPI Server 컨테이너 생성 실패: {}", containerName, e);
			return false;
		}
	}

	/**
	 * 버킷 생성
	 * 1. 중복 체크
	 * 2. SwiftAPI Server에 컨테이너 생성 요청
	 * 3. Swift 성공 응답 확인
	 * 4. Bucket Repository에 메타데이터 저장 (IaaS DB)
	 * 5. 저장된 데이터 반환
	 * 
	 * @param requestDto 버킷 생성 요청 DTO
	 * @param ownerUserId 소유자 ID
	 * @return 생성된 버킷 정보
	 */
	public BucketCreateResponseDto createBucket(BucketCreateRequestDto requestDto, String ownerUserId) {
		String bucketName = requestDto.getName();

		// 1. 중복 체크
		Optional<Bucket> existingBucket = bucketRepository.findByBucketNameAndOwnerUserId(bucketName, ownerUserId);
		if (existingBucket.isPresent()) {
			throw new IllegalArgumentException("이미 존재하는 버킷 이름입니다.");
		}

		// 2. SwiftAPI Server에 컨테이너 생성 요청
		boolean swiftSuccess = createContainerInSwift(bucketName);
		if (!swiftSuccess) {
			throw new RuntimeException("Swift 컨테이너 생성에 실패했습니다.");
		}
		log.info("SwiftAPI Server 컨테이너 생성 성공: {}", bucketName);

		// 3. Swift 성공 확인 후, Bucket Repository에 메타데이터 저장 (IaaS DB)
		Bucket bucket = Bucket.builder()
				.bucketName(bucketName)
				.ownerUserId(ownerUserId)
				.status("PENDING")
				.build();

		Bucket savedBucket = bucketRepository.save(bucket);
		log.info("버킷 메타데이터 저장 완료: bucketId={}, bucketName={}", savedBucket.getBucketId(), savedBucket.getBucketName());

		// 4. Response DTO 생성
		return BucketCreateResponseDto.builder()
				.name(savedBucket.getBucketName())
				.status(savedBucket.getStatus())
				.createdAt(savedBucket.getCreatedAt())
				.build();
	}

	/**
	 * SwiftAPI Server에서 버킷 내 파일 목록 조회 (search bucket contents)
	 * @param containerName 컨테이너(버킷) 이름
	 * @return 파일 목록
	 */
	private List<SwiftObject> searchBucketContents(String containerName) {
		try {
			log.info("SwiftAPI Server에 버킷 내용 조회 요청: {}", containerName);
			List<? extends org.openstack4j.model.storage.object.SwiftObject> swiftObjects = 
					openstackClient.objectStorage().objects().list(containerName);
			
			// OpenStack4j의 SwiftObject를 내부 SwiftObject로 변환
			return swiftObjects.stream()
					.map(obj -> {
						LocalDateTime lastModified = obj.getLastModified() != null 
								? obj.getLastModified().toInstant()
										.atZone(ZoneId.systemDefault())
										.toLocalDateTime()
								: LocalDateTime.now();
						
						return new SwiftObject(
								obj.getName(),
								obj.getSizeInBytes(),
								lastModified
						);
					})
					.collect(Collectors.toList());
		} catch (Exception e) {
			log.error("SwiftAPI Server 버킷 내용 조회 실패: {}", containerName, e);
			return new ArrayList<>();
		}
	}

	/**
	 * 버킷 현황 조회 (시퀀스 다이어그램 반영)
	 * 1. Bucket Repository에서 버킷 메타데이터 조회 및 소유권 확인
	 * 2. SwiftAPI Server에 버킷 내용 조회 요청 (search bucket contents)
	 * 3. Swift 응답을 받아서 DTO로 변환하여 반환
	 * 
	 * @param bucketName 버킷 이름
	 * @param ownerUserId 소유자 ID
	 * @return 버킷 현황 정보 (버킷 이름 및 파일 목록)
	 */
	public BucketStatusResponseDto getBucketStatus(String bucketName, String ownerUserId) {
		// 1. Bucket Repository에서 버킷 메타데이터 조회 및 소유권 확인
		Optional<Bucket> bucketOpt = bucketRepository.findByBucketNameAndOwnerUserId(bucketName, ownerUserId);
		if (bucketOpt.isEmpty()) {
			throw new EntityNotFoundException("버킷을 찾을 수 없거나 접근 권한이 없습니다.");
		}

		Bucket bucket = bucketOpt.get();
		log.info("버킷 조회: bucketName={}, ownerUserId={}", bucketName, ownerUserId);

		// 2. SwiftAPI Server에 버킷 내용 조회 요청 (search bucket contents)
		List<SwiftObject> swiftObjects = searchBucketContents(bucketName);
		log.info("SwiftAPI Server에서 조회한 파일 개수: {}", swiftObjects.size());

		// 3. Swift 응답을 ObjectDto로 변환
		List<ObjectDto> objects = swiftObjects.stream()
				.map(swiftObj -> ObjectDto.builder()
						.name(swiftObj.getName())
						.size(swiftObj.getSize())
						.lastModified(swiftObj.getLastModified())
						.build())
				.collect(Collectors.toList());

		// 4. Response DTO 생성
		return BucketStatusResponseDto.builder()
				.bucket(bucket.getBucketName())
				.objects(objects)
				.build();
	}

	/**
	 * Swift 객체 정보를 담는 내부 클래스
	 */
	private static class SwiftObject {
		private String name;
		private Long size;
		private LocalDateTime lastModified;

		public SwiftObject(String name, Long size, LocalDateTime lastModified) {
			this.name = name;
			this.size = size;
			this.lastModified = lastModified;
		}

		public String getName() {
			return name;
		}

		public Long getSize() {
			return size;
		}

		public LocalDateTime getLastModified() {
			return lastModified;
		}
	}
}
