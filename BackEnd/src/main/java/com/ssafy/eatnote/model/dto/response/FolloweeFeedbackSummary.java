package com.ssafy.eatnote.model.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "트레이너가 팔로우한 회원의 피드백 요약 정보 DTO")
public class FolloweeFeedbackSummary {

    @Schema(description = "회원 ID", example = "10")
    private Long userId;

    @Schema(description = "회원 닉네임", example = "홍길동")
    private String nickname;

    @Schema(description = "회원 프로필 이미지 URL", example = "/uploads/profiles/10.jpg")
    private String profileImage;

    @Schema(description = "작성되지 않은 피드백 수", example = "3")
    private int pendingCount;

    @Schema(description = "작성된 피드백 수", example = "5")
    private int writtenCount;
}