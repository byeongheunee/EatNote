package com.ssafy.eatnote.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "공통 응답 객체")
public class MyApiResponse<T> {

	@Schema(description = "요청 성공 여부", example = "true")
	private boolean success;

	@Schema(description = "응답 코드 (성공/에러 구분용 코드)", example = "USER_LOGIN_SUCCESS")
	private String code;

	@Schema(description = "응답 데이터", nullable = true)
	private T data;

	@Schema(description = "응답 메시지", example = "요청이 정상적으로 처리되었습니다.")
	private String message;

	// 성공 응답 (데이터 + 코드 + 메시지)
	public static <T> MyApiResponse<T> success(T data, String code, String message) {
		return MyApiResponse.<T>builder().success(true).data(data).code(code).message(message).build();
	}

	// 실패 응답 (코드 + 메시지)
	public static <T> MyApiResponse<T> failure(String code, String message) {
		return MyApiResponse.<T>builder().success(false).code(code).message(message).build();
	}
}


/**
 * 
 * 상황      		코드 값 예시					설명 
 * 로그인 성공 		AUTH_LOGIN_SUCCESS 			인증 도메인, 로그인 성공 
 * 로그인 실패 		AUTH_LOGIN_FAILED 			인증 도메인, 로그인 실패 
 * 회원가입 성공 	AUTH_REGISTER_SUCCESS 		회원가입 완료 
 * 회원 없음		USER_NOT_FOUND 				사용자 없음 에러 
 * 권한 없음 		AUTH_FORBIDDEN 				인증은 됐지만 권한 없음 
 * 잘못된 입력		VALIDATION_FAILED 			입력값 유효성 실패 
 * 서버 오류 		INTERNAL_SERVER_ERROR 		일반적인 500 에러
 * 
 **/
