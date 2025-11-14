package iaas.service;

import iaas.dto.request.BucketCreateRequestDto;
import iaas.dto.response.BucketCreateResponseDto;
import iaas.entity.Bucket;
import iaas.exception.EntityFuckException;
import iaas.repository.BucketRepositoy;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class BucketService {
	private BucketRepositoy bucketRepositoy;

	public BucketCreateResponseDto createBucket(BucketCreateRequestDto requestDto) throws EntityFuckException {
		Bucket bucket = new Bucket();
		bucketRepositoy.save(bucket);
		if (bucket.getId().equals("fuck")){
			throw new EntityFuckException();
		}
		return BucketCreateResponseDto.builder().name(bucket.getId()).build();
	}
}
