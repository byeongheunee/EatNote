package com.ssafy.eatnote.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ssafy.eatnote.model.dto.VideoDto;
import com.ssafy.eatnote.model.dto.response.MyApiResponse;
import com.ssafy.eatnote.model.service.YoutubeService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/youtube")
@Tag(name = "유튜브 관련 API", description = "유튜브 관련 API입니다.")
public class YoutubeController {

    private final YoutubeService youtubeService;

    @Operation(summary = "목표 기반 유튜브 영상 추천", description = "사용자의 목표(예: 감량, 증량 등)에 따라 관련 유튜브 영상을 10개 추천합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "추천 영상 조회 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 파라미터"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/recommend")
    public MyApiResponse<List<VideoDto>> recommendVideos(@RequestParam(defaultValue = "다이어트 운동") String goal) {
        List<VideoDto> videos = youtubeService.searchVideos(goal);
        return MyApiResponse.success(videos, "YOUTUBE_RECOMMEND_SUCCESS", "목표 기반 추천 영상 조회 성공");
    }

    @Operation(summary = "유튜브 영상 검색", description = "사용자가 직접 입력한 키워드를 기반으로 유튜브 영상을 검색합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "영상 검색 성공"),
        @ApiResponse(responseCode = "400", description = "검색어 누락 또는 잘못된 요청"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/search")
    public MyApiResponse<List<VideoDto>> searchVideos(@RequestParam String keyword) {
        List<VideoDto> videos = youtubeService.searchVideos(keyword);
        return MyApiResponse.success(videos, "YOUTUBE_SEARCH_SUCCESS", "유튜브 영상 검색 성공");
    }
}
