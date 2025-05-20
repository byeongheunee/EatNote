package com.ssafy.eatnote.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "게시판 DTO")
public class Board {

    @Schema(description = "게시판 ID", example = "1")
    private Integer boardId;

    @Schema(description = "게시판 이름", example = "식단 공유 게시판")
    private String name;

    @Schema(description = "게시판 설명", example = "자신의 식단을 공유하고 피드백을 받는 공간입니다.")
    private String description;

    @Schema(description = "접근 권한 코드", example = "ALL")
    private String accessCode;

    @Schema(description = "생성 시각", example = "2025-05-02T12:30:00")
    private LocalDateTime createdAt;

    @Schema(description = "수정 시각", example = "2025-05-02T13:10:00")
    private LocalDateTime updatedAt;
}
