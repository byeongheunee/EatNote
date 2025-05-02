package com.ssafy.eatnote.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "팔로우 관계 DTO")
public class UserFollow {

    @Schema(description = "팔로우 ID", example = "101")
    private Long followId;

    @Schema(description = "팔로우 요청자 ID", example = "5")
    private Long fromUserId;

    @Schema(description = "팔로우 대상 사용자 ID", example = "12")
    private Long toUserId;

    @Schema(description = "팔로우 상태 (PENDING, ACCEPTED, REJECTED)", example = "PENDING")
    private String status;

    @Schema(description = "팔로우 요청 시간", example = "2025-05-02T15:45:00")
    private LocalDateTime requestedAt;

    @Schema(description = "팔로우 응답 시간", example = "2025-05-02T16:00:00")
    private LocalDateTime respondedAt;
}
