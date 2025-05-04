package com.ssafy.eatnote.model.dto;

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
@Schema(description = "이메일 인증 코드 검증 요청 DTO")
public class VerifyCodeRequest {

    @Schema(description = "이메일", example = "test@example.com")
    private String email;

    @Schema(description = "인증 코드", example = "123456")
    private String code;
}
