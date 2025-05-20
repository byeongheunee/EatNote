package com.ssafy.eatnote.model.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * 개인정보 수정 페이지 진입 전 비밀번호 확인 요청 DTO
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "비밀번호 확인 요청 DTO")
public class PasswordCheckRequest {

    @Schema(description = "현재 비밀번호", example = "mypassword123!")
    private String password;
}