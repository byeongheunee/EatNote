package com.ssafy.eatnote.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "유튜브 영상 정보 DTO")
public class VideoDto {

    @Schema(description = "영상 제목", example = "5분 뱃살 타파 홈트레이닝")
    private String title;

    @Schema(description = "YouTube 영상 ID", example = "dQw4w9WgXcQ")
    private String videoId;

    @Schema(description = "영상 썸네일 이미지 URL", example = "https://i.ytimg.com/vi/dQw4w9WgXcQ/default.jpg")
    private String thumbnailUrl;

    @Schema(description = "채널명", example = "헬스갤러리TV")
    private String channelTitle;

    @Schema(description = "영상 게시 날짜 (RFC3339 형식)", example = "2024-04-22T15:30:00Z")
    private String publishedAt;

    @Schema(description = "YouTube 영상 전체 URL", example = "https://www.youtube.com/watch?v=dQw4w9WgXcQ")
    private String url;
}