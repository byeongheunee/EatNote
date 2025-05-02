package com.ssafy.eatnote.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "댓글 DTO (트리 구조 지원)")
public class Comment {

    @Schema(description = "댓글 ID", example = "5001")
    private Integer commentId;

    @Schema(description = "댓글 내용", example = "좋은 글 감사합니다!")
    private String content;

    @Schema(description = "작성자 사용자 ID", example = "3")
    private Long userId;

    @Schema(description = "게시글 ID", example = "101")
    private Integer articleId;

    @Schema(description = "트리 왼쪽 값", example = "2")
    private Integer lft;

    @Schema(description = "트리 오른쪽 값", example = "3")
    private Integer rgt;

    @Schema(description = "댓글 깊이 (0부터 시작)", example = "1")
    private Integer depth;

    @Schema(description = "작성일", example = "2025-05-02T14:45:00")
    private LocalDateTime createdAt;

    @Schema(description = "수정일", example = "2025-05-02T15:00:00")
    private LocalDateTime updatedAt;
}
