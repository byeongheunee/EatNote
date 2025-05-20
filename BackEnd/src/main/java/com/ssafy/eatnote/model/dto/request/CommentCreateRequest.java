package com.ssafy.eatnote.model.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "댓글 생성 요청 DTO")
public class CommentCreateRequest {

    @Schema(description = "댓글 내용", example = "좋은 글 감사합니다!")
    private String content;

    @Schema(description = "부모 댓글 ID (최상위 댓글이면 null)", example = "1005", nullable = true)
    private Integer parentCommentId;

    @Schema(description = "댓글 대상 타입", example = "ARTICLE") // 예: ARTICLE, MEAL 등
    private String targetType;

    @Schema(description = "댓글 대상 ID", example = "5")
    private Long targetId;
}