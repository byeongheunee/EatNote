package com.ssafy.eatnote.model.dto.response;

import com.ssafy.eatnote.model.dto.SimpleUserInfo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter 
@Builder
@NoArgsConstructor 
@AllArgsConstructor
@Schema(description = "로그인 응답 DTO")
public class LoginResponse {

    @Schema(description = "JWT 액세스 토큰")
    private String accessToken;

    @Schema(description = "사용자 정보")
    private SimpleUserInfo user;
}
