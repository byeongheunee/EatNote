package com.ssafy.eatnote.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "알림 DTO")
public class Notification {

    @Schema(description = "알림 ID", example = "501")
    private Long notificationId;

    @Schema(description = "알림 받을 사용자 ID", example = "2")
    private Long userId;

    @Schema(description = "알림 유형", example = "NEW_MEAL")
    private String type;

    @Schema(description = "알림 내용", example = "새로운 식단이 등록되었습니다.")
    private String content;

    @Schema(description = "관련 식단 ID (없을 수도 있음)", example = "1001")
    private Long relatedMealId;

    @Schema(description = "읽음 여부", example = "false")
    private Boolean isRead;

    @Schema(description = "생성 시각", example = "2025-05-02T14:30:00")
    private LocalDateTime createdAt;
}
