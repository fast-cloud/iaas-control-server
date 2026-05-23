package iaas.service;

import iaas.dto.request.BucketCreateRequestDto;
import iaas.dto.response.BucketCreateResponseDto;
import iaas.dto.response.BucketListResponseDto;
import iaas.dto.response.BucketStatusResponseDto;
import iaas.dto.response.ObjectDto;
import iaas.entity.Bucket;
import iaas.exception.BucketNotFoundException;
import iaas.exception.DuplicateBucketException;
import iaas.exception.SwiftApiException;
import iaas.repository.BucketRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openstack4j.api.OSClient.OSClientV3;
import org.openstack4j.model.common.Identifier;
import org.openstack4j.openstack.OSFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BucketService {

	private final BucketRepository bucketRepository;

	@Value("${openstack.auth.url}") private String authUrl;
	@Value("${openstack.user.name}") private String username;
	@Value("${openstack.user.password}") private String password;
	@Value("${openstack.project.name}") private String projectName;
	@Value("${openstack.domain.name}") private String domainName;

	// 매 요청마다 새 클라이언트 생성 — 싱글톤은 토큰 만료(1h) 문제 발생
	private OSClientV3 createOpenStackClient() {
		return OSFactory.builderV3()
				.endpoint(authUrl)
				.credentials(username, password, Identifier.byName(domainName))
				.scopeToProject(Identifier.byName(projectName), Identifier.byName(domainName))
				.authenticate();
	}

	private void createContainerInSwift(String containerName) throws SwiftApiException {
		try {
			log.info("Swift 컨테이너 생성 요청: {}", containerName);
			OSClientV3 osClient = createOpenStackClient();
			osClient.objectStorage().containers().create(containerName);
			log.info("Swift 컨테이너 생성 성공: {}", containerName);
		} catch (Exception e) {
			log.error("Swift 컨테이너 생성 실패: {}", containerName, e);
			throw new SwiftApiException("컨테이너 생성", containerName, e);
		}
	}

	public BucketCreateResponseDto createBucket(BucketCreateRequestDto requestDto, String ownerUserId) throws SwiftApiException {
		String bucketName = requestDto.getName();

		// 1. 중복 체크
		Optional<Bucket> existingBucket = bucketRepository.findByBucketNameAndOwnerUserId(bucketName, ownerUserId);
		if (existingBucket.isPresent()) {
			throw new DuplicateBucketException(bucketName, ownerUserId);
		}

		// 2. SwiftAPI Server에 컨테이너 생성 요청
		createContainerInSwift(bucketName);

		// 3. 메타데이터 저장
		Bucket bucket = Bucket.builder()
				.bucketName(bucketName)
				.ownerUserId(ownerUserId)
				.status("PENDING")
				.build();

		Bucket savedBucket = bucketRepository.save(bucket);
		log.info("버킷 메타데이터 저장 완료: bucketId={}, bucketName={}", savedBucket.getBucketId(), savedBucket.getBucketName());

		return BucketCreateResponseDto.builder()
				.name(savedBucket.getBucketName())
				.status(savedBucket.getStatus())
				.createdAt(savedBucket.getCreatedAt())
				.build();
	}

	private List<SwiftObject> searchBucketContents(String containerName) throws SwiftApiException {
		try {
			log.info("Swift 버킷 내용 조회 요청: {}", containerName);
			OSClientV3 osClient = createOpenStackClient();
			List<? extends org.openstack4j.model.storage.object.SwiftObject> swiftObjects =
					osClient.objectStorage().objects().list(containerName);

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
			log.error("Swift 버킷 내용 조회 실패: {}", containerName, e);
			throw new SwiftApiException("버킷 내용 조회", containerName, e);
		}
	}

	public List<BucketListResponseDto> listBuckets(String ownerUserId) {
		return bucketRepository.findByOwnerUserId(ownerUserId).stream()
				.map(bucket -> BucketListResponseDto.builder()
						.bucketId(bucket.getBucketId())
						.name(bucket.getBucketName())
						.status(bucket.getStatus())
						.createdAt(bucket.getCreatedAt())
						.build())
				.collect(Collectors.toList());
	}

	public BucketStatusResponseDto getBucketStatus(String bucketName, String ownerUserId) throws SwiftApiException {
		Optional<Bucket> bucketOpt = bucketRepository.findByBucketNameAndOwnerUserId(bucketName, ownerUserId);
		if (bucketOpt.isEmpty()) {
			throw new BucketNotFoundException(bucketName, ownerUserId);
		}

		Bucket bucket = bucketOpt.get();
		log.info("버킷 조회: bucketName={}, ownerUserId={}", bucketName, ownerUserId);

		List<SwiftObject> swiftObjects = searchBucketContents(bucketName);
		log.info("Swift 파일 개수: {}", swiftObjects.size());

		List<ObjectDto> objects = swiftObjects.stream()
				.map(swiftObj -> ObjectDto.builder()
						.name(swiftObj.getName())
						.size(swiftObj.getSize())
						.lastModified(swiftObj.getLastModified())
						.build())
				.collect(Collectors.toList());

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
