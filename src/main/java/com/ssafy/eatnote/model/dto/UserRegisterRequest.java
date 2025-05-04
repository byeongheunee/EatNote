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
@Schema(description = "회원가입 요청 DTO")
public class UserRegisterRequest {

    @Schema(description = "이메일", example = "test@example.com")
    private String email;

    @Schema(description = "비밀번호", example = "1234")
    private String password;

    @Schema(description = "이름", example = "홍길동")
    private String name;

    @Schema(description = "닉네임", example = "gildong123")
    private String nickname;

    @Schema(description = "성별", example = "M")
    private String gender;

    @Schema(description = "회원 유형 (1=회원, 2=트레이너)", example = "2")
    private Integer userType;

    @Schema(description = "트레이너 상세정보", nullable = true)
    private TrainerDetails trainerDetails;

    @Schema(description = "일반회원 상세정보", nullable = true)
    private MemberDetails memberDetails;
}
