package com.ssafy.eatnote.model.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "트레이너 피드백 DTO")
public class TrainerFeedbackResponse {

	@Schema(description = "피드백 ID", example = "301")
	private Long feedbackId;

	@Schema(description = "트레이너 ID", example = "8")
	private Long trainerId;

	@Schema(description = "트레이너 닉네임", example = "trainerKim")
	private String trainerNickname;

	@Schema(description = "트레이너 점수 (10점 만점)", example = "8.5")
	private Float trainerScore;

	@Schema(description = "피드백 내용", example = "단백질 보충이 필요해요.")
	private String comment;

	@Schema(description = "피드백 작성 시각", example = "2025-05-13T20:45:00")
	private LocalDateTime createdAt;

	@Schema(description = "현재 로그인한 사용자가 작성한 피드백인지 여부", example = "true")
	private Boolean isWrittenByMe;
}
