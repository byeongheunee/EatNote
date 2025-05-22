package com.ssafy.eatnote.model.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "게시글 응답 DTO")
public class ArticleResponse {

    @Schema(description = "게시글 ID", example = "101")
    private Integer articleId;

    @Schema(description = "게시글 제목", example = "오늘의 식단 공유합니다!")
    private String title;

    @Schema(description = "게시글 내용", example = "닭가슴살과 고구마를 먹었어요.")
    private String content;

    @Schema(description = "소속 게시판 ID", example = "1")
    private Integer boardId;

    @Schema(description = "작성자 사용자 ID", example = "5")
    private Long userId;

    @Schema(description = "작성자 닉네임", example = "햄트레이너")
    private String userNickname;

    @Schema(description = "대표 이미지 URL", example = "https://fitbob.s3.amazonaws.com/thumbnails/article101.jpg")
    private String imageUrl;

    @Schema(description = "조회수", example = "35")
    private Integer viewCnt;

    @Schema(description = "좋아요 수", example = "12")
    private Integer likeCount;

    @Schema(description = "싫어요 수", example = "1")
    private Integer dislikeCount;
    
    @Schema(description = "내 반응 상태 (LIKE / DISLIKE / null)", example = "LIKE")
    private String myReaction;

    @Schema(description = "첨부 이미지 목록")
    private List<ArticleFileResponse> attachments;

    @Schema(description = "댓글 목록")
    private List<CommentResponse> comments;
    
    @Schema(description = "작성일시", example = "2025-05-02T12:00:00")
    private LocalDateTime createdAt;

    @Schema(description = "수정일시", example = "2025-05-02T14:00:00")
    private LocalDateTime modifiedAt;
    
    @Schema(description = "작성자 팔로우 상태 (PENDING / ACCEPTED / REJECTED / NONE)", example = "ACCEPTED")
    private String followStatus;
} 
