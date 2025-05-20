package com.ssafy.eatnote.model.dto;

import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "트레이너 피드백 달력 통계 응답 DTO")
public class FeedbackCalendarStat {

    @Schema(description = "날짜", example = "2025-05-01")
    private LocalDate date;

    @Schema(description = "요청된 식단 수", example = "3")
    private int total;

    @Schema(description = "작성된 피드백 수", example = "2")
    private int written;
} 
