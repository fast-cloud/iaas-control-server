package iaas.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {

	//400 Bad Request
	BAD_REQUEST(40000, "유효한 요청이 아닙니다."),
	MISSING_REQUIRED_HEADER(40001, "필수 헤더가 누락되었습니다."),
	MISSING_REQUIRED_PARAMETER(40002, "필수 파라미터가 누락되었습니다."),
	INVALID_PARAMETER(40003, "유효하지 않은 파라미터입니다."),
	SWIFT_API_ERROR(40004, "SwiftAPI Server 오류가 발생했습니다."),

	//404 Not Found
	NOT_FOUND(40400, "존재하지 않는 API입니다."),
	ENTITY_NOT_FOUND(40405, "요청한 리소스를 찾을 수 없습니다."),
	BUCKET_NOT_FOUND(40410, "버킷을 찾을 수 없습니다."),

	//405 Method Not Allowed
	METHOD_NOT_ALLOWED(40500, "해당 요청은 지원되지 않습니다."),

	//409 Conflict
	DUPLICATE_BUCKET_NAME(40901, "이미 존재하는 버킷 이름입니다."),

	//500 Internal Server Error
	INTERNAL_SERVER_ERROR(50000, "서버 내부 오류입니다."),
	DATABASE_CONNECTION_ERROR(50001, "데이터베이스 연결 오류입니다."),
	OPENSTACK_API_ERROR(50003, "OpenStack API 호출 중 오류가 발생했습니다."),

	//503 Service Unavailable
	SERVICE_UNAVAILABLE(50300, "서비스를 이용할 수 없습니다.");

	private final int code;
	private final String message;
}