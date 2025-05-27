package com.ssafy.eatnote.controller;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ssafy.eatnote.model.dao.MemberDao;
import com.ssafy.eatnote.model.dao.UserDao;
import com.ssafy.eatnote.model.dto.MemberDetails;
import com.ssafy.eatnote.model.dto.User;
import com.ssafy.eatnote.model.dto.VideoDto;
import com.ssafy.eatnote.model.dto.response.MyApiResponse;
import com.ssafy.eatnote.model.security.UserDetailsImpl;
import com.ssafy.eatnote.model.service.ChatGPTService;
import com.ssafy.eatnote.model.service.UserService;
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
    private final ChatGPTService chatGPTService;
    private final UserDao userDao;
    private final MemberDao memberDao;
    

    @Operation(summary = "목표 기반 유튜브 영상 추천", description = "사용자의 목표(예: 감량, 증량 등)에 따라 관련 유튜브 영상을 10개 추천합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "추천 영상 조회 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 파라미터"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/recommend")
    public MyApiResponse<List<VideoDto>> recommendVideos(@RequestParam(defaultValue = "다이어트 운동") String goal) {
        List<VideoDto> videos = youtubeService.searchVideos("건강"+goal);
        return MyApiResponse.success(videos, "YOUTUBE_RECOMMEND_SUCCESS", "목표 기반 추천 영상 조회 성공");
    }

//    @Operation(summary = "유튜브 영상 검색", description = "사용자가 직접 입력한 키워드를 기반으로 유튜브 영상을 검색합니다.")
//    @ApiResponses({
//        @ApiResponse(responseCode = "200", description = "영상 검색 성공"),
//        @ApiResponse(responseCode = "400", description = "검색어 누락 또는 잘못된 요청"),
//        @ApiResponse(responseCode = "500", description = "서버 오류")
//    })
//    @GetMapping("/search")
//    public MyApiResponse<List<VideoDto>> searchVideos(@RequestParam String keyword) {
//        List<VideoDto> videos = youtubeService.searchVideos(keyword);
//        return MyApiResponse.success(videos, "YOUTUBE_SEARCH_SUCCESS", "유튜브 영상 검색 성공");
//    }
    
    
    @GetMapping("/recommendExercise")
    public MyApiResponse<List<String>> recommendExercises(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        User user = userDao.selectUserById(userDetails.getUserId());
        MemberDetails member =  memberDao.findByUserId(user.getUserId());

        String prompt = String.format(
            "다음 사용자 정보를 바탕으로 적합한 운동 4가지를 추천해줘).\n" +
            "- 성별: %s\n- 나이: %d\n- 키: %.1fcm\n- 몸무게: %.1fkg\n- 목표: %s\n" +
            "운동 이름만 콤마로 구분해서 4개만 출력해줘. 다른 기호나 문자 없이 꼭 이 형식으로 운동 4개 이름만 출력해줘 ex) '걷기, 스쿼트, 플랭크, 줄넘기'",
            user.getGender(), user.getAge(), member.getHeight(), member.getWeight(), member.getGoal()
        );

        String gptResponse = chatGPTService.ask(prompt);  //

        List<String> exercises = Arrays.stream(gptResponse.split(","))
                                       .map(String::trim)
                                       .map(e -> e.replaceAll("'", ""))
                                       .collect(Collectors.toList());

        return MyApiResponse.success(exercises, "EXERCISE_RECOMMEND_SUCCESS", "운동 추천 성공");
    }
    
}
