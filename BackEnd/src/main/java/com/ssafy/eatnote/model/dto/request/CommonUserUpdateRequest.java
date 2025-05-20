package com.ssafy.eatnote.model.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter 
@Setter
@NoArgsConstructor 
@AllArgsConstructor 
@Builder
@Schema(description = "공통 사용자 정보 수정 요청 DTO")
public class CommonUserUpdateRequest {
    @Schema(description = "비밀번호", example = "newpassword123!")
    private String password;

    @Schema(description = "닉네임", example = "fitmaster")
    private String nickname;
}