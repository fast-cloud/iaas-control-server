package iaas.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SuccessCode {

	//200 OK
	BUCKET_CREATE_SUCCESS(20001, "버킷이 성공적으로 생성되었습니다."),
	BUCKET_SEARCH_SUCCESS(20002, "파일 목록을 성공적으로 조회했습니다.");
	

	private final int code;
	private final String message;
}