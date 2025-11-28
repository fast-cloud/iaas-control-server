package iaas.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SuccessCode {


	BUCKET_CREATE_SUCCESS(20001, "버킷이 성공적으로 생성되었습니다."),
	BUCKET_SEARCH_SUCCESS(20002, "파일 목록을 성공적으로 조회했습니다."),
	

	INSTANCE_LIST_SUCCESS(20003, "인스턴스 목록 조회 성공"),
	
	//202 Accepted
	INSTANCE_CREATE_ACCEPTED(20201, "인스턴스 생성을 시작합니다. 완료까지 최대 3분이 소요될 수 있습니다.");


	private final int code;
	private final String message;
}