package com.ssafy.eatnote.model.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "댓글 응답 DTO (트리 구조 포함)")
public class CommentResponse {

    @Schema(description = "댓글 ID", example = "5001")
    private Integer commentId;

    @Schema(description = "댓글 내용", example = "좋은 글 감사합니다!")
    private String content;

    @Schema(description = "작성자 사용자 ID", example = "3")
    private Long userId;

    @Schema(description = "작성자 닉네임", example = "햄트레이너")
    private String userNickname;

    @Schema(description = "작성일시", example = "2025-05-02T14:45:00")
    private LocalDateTime createdAt;

    @Schema(description = "수정일시", example = "2025-05-02T15:00:00")
    private LocalDateTime updatedAt;

    @Schema(description = "댓글 깊이")
    private Integer depth;

    @Schema(description = "좋아요 수", example = "5")
    private Integer likeCount;

    @Schema(description = "싫어요 수", example = "1")
    private Integer dislikeCount;

    @Schema(description = "내 반응 상태 (LIKE / DISLIKE / null)", example = "LIKE")
    private String myReaction;

    @Schema(description = "대댓글 리스트")
    private List<CommentResponse> replies;
}
