package com.ssafy.eatnote.controller;

import com.ssafy.eatnote.model.dto.Meal;
import com.ssafy.eatnote.model.dto.MealFeedback;
import com.ssafy.eatnote.model.dto.request.MealFeedbackRequest;
import com.ssafy.eatnote.model.dto.response.FollowerMealResponse;
import com.ssafy.eatnote.model.dto.response.MealFeedbackListViewResponse;
import com.ssafy.eatnote.model.dto.response.MealListViewResponse;
import com.ssafy.eatnote.model.dto.response.MealPublicDetailResponse;
import com.ssafy.eatnote.model.dto.response.MealStatsResponse;
import com.ssafy.eatnote.model.dto.response.MyApiResponse;
import com.ssafy.eatnote.model.dto.response.TrainerFeedbackResponse;
import com.ssafy.eatnote.model.security.UserDetailsImpl;
import com.ssafy.eatnote.model.service.MealFeedbackService;
import com.ssafy.eatnote.model.service.MealService;

import io.swagger.v3.oas.annotations.*;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/meal")
@Tag(name = "식사관리 API", description = "식단 이미지 업로드 및 피드백 API")
@RequiredArgsConstructor
public class MealController {

    private final MealService mealService;

    @Operation(summary = "공개 식단 상세 조회", description = "특정 식단 ID에 해당하는 공개 식단의 상세 정보를 조회합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "정상적으로 식단 상세 정보를 반환했습니다."),
        @ApiResponse(responseCode = "404", description = "해당 식단을 찾을 수 없습니다.")
    })
    
    @GetMapping("/{mealId}")
    public MyApiResponse<MealPublicDetailResponse> getPublicMealDetail(
            @PathVariable Long mealId,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        Long loginUserId = (userDetails != null) ? userDetails.getUserId() : null;

        MealPublicDetailResponse response = mealService.getPublicMealDetail(mealId, loginUserId);
        if (response == null) {
            return MyApiResponse.failure("MEAL_NOT_FOUND", "해당 식단을 찾을 수 없습니다.");
        }

        return MyApiResponse.success(response, "MEAL_DETAIL_SUCCESS", "식단 상세 조회 성공");
    }

    @GetMapping("/{mealId}/feedback")
    @Operation(summary = "공개 식단 피드백 조회", description = "식단 ID에 해당하는 모든 트레이너 피드백을 조회합니다.")
    public MyApiResponse<List<TrainerFeedbackResponse>> getTrainerFeedbackList(@PathVariable Long mealId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
    	Long userId = userDetails.getUserId();
    	System.out.println(userId);
    	List<TrainerFeedbackResponse> feedbackList = mealService.getFeedbackListByMealId(mealId, userId);
        return MyApiResponse.success(feedbackList, "MEAL_FEEDBACK_LIST_SUCCESS", "트레이너 피드백 목록 조회 성공");
    }

    @Operation(summary = "식단 이미지 업로드", description = "식사 이미지를 업로드하면 AI가 자동 분석하고 DB에 저장합니다.")
    @PostMapping(value = "/upload", consumes = "multipart/form-data")
    public MyApiResponse<?> uploadMealImage(@RequestParam("file") MultipartFile file, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        Long userId = userDetails.getUser().getUserId();
        try {
            if (file == null || file.isEmpty()) {
                return MyApiResponse.failure("FILE_EMPTY", "파일이 비어 있습니다.");
            }
            Meal savedMeal = mealService.saveAnalyzedMeal(file, userId);
            return MyApiResponse.success(savedMeal, "MEAL_UPLOAD_SUCCESS", "식단 이미지 업로드 및 분석 성공");
        }catch (Exception e) {
            if (e.getMessage().contains("NO_FOOD_DETECTED")) {
                return MyApiResponse.failure("NO_FOOD_DETECTED", "감지된 음식이 없습니다. 사진을 확인해주세요.");
            }
            return MyApiResponse.failure("MEAL_UPLOAD_FAILED", "식단 업로드 중 오류 발생: " + e.getMessage());
        }
    }

    @Operation(summary = "내가 올린 식단 삭제", description = "본인이 올린 식단만 삭제할 수 있습니다.")
    @DeleteMapping("/{mealId}")
    public MyApiResponse<?> deleteMeal(@PathVariable Long mealId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        try {
            Long userId = userDetails.getUser().getUserId();
            mealService.deleteMealById(mealId, userId);
            return MyApiResponse.success(null, "MEAL_DELETE_SUCCESS", "식단 삭제 완료");
        } catch (Exception e) {
            return MyApiResponse.failure("MEAL_DELETE_FAILED", "식단 삭제 실패: " + e.getMessage());
        }
    }
    
    @Operation(summary = "최근 등록된 식단 10개 조회", description = "모든 회원이 최근에 등록한 식단 중 최신순 10개를 조회합니다. 로그인 여부와 관계없이 누구나 접근할 수 있습니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "최근 식단 조회 성공")
    })
    @GetMapping("/home/recent-meals")
    public MyApiResponse<List<MealListViewResponse>> getRecentMeals() {
        List<MealListViewResponse> meals = mealService.getRecentMeals();
        return MyApiResponse.success(meals, "RECENT_MEALS_FETCH_SUCCESS", "최근 식단 조회 성공");
    }

    @Operation(summary = "최근 일주일 인기 식단 10개 조회", description = "최근 7일간 업로드된 식단 중 좋아요 수가 많은 순서대로 10개의 식단을 조회합니다. 로그인 여부와 관계없이 누구나 접근할 수 있습니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "인기 식단 조회 성공")
    })
    
    @GetMapping("/home/popular-meals")
    public MyApiResponse<List<MealListViewResponse>> getPopularMealsThisWeek() {
    	List<MealListViewResponse> meals = mealService.getPopularMealsThisWeek();
    	return MyApiResponse.success(meals, "POPULAR_MEALS_FETCH_SUCCESS", "인기 식단 조회 성공");
    }
    
    @GetMapping("/followings")
    @Operation(summary = "팔로우한 사람들의 식단 조회")
    public ResponseEntity<?> getMealsOfFollowings(@AuthenticationPrincipal UserDetailsImpl userDetails) {
    	System.out.printf("🔐 userDetails: {}", userDetails);  // null이면 JWT 인증 실패!
        Long userId = userDetails.getUserId();
        List<MealListViewResponse> meals = mealService.getMealsOfMyFollowings(userId);
        return ResponseEntity.ok(MyApiResponse.success(meals, "FOLLOWING_MEALS_SUCCESS", "팔로잉 식단 조회 성공"));
    }

    @Operation(summary = "사용자 식단 통계 조회", description = "연속 식단 기록일, 총 식단 수, 피드백 받은 식단 수, 평균 칼로리를 반환합니다.")
	@ApiResponses({
	    @ApiResponse(responseCode = "200", description = "식단 통계 조회 성공"),
	    @ApiResponse(responseCode = "401", description = "인증 실패 또는 토큰 없음")
	})
	@GetMapping("/stats")
	public MyApiResponse<MealStatsResponse> getMealStats(@AuthenticationPrincipal UserDetailsImpl userDetails) {
	    Long userId = userDetails.getUserId();
	    MealStatsResponse stats = mealService.getMealStatistics(userId);
	    return MyApiResponse.success(stats, "MEAL_STATS_SUCCESS", "식단 통계 조회 성공");
	}
    
    @Operation(summary = "로그인한 회원의 최근 식단 조회", description = "로그인한 회원의 최근 20개 식단 정보를 조회합니다.")
    @GetMapping("/my/recent")
    public MyApiResponse<List<MealListViewResponse>> getMyRecentMeals(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        Long userId = userDetails.getUserId();
        List<MealListViewResponse> meals = mealService.getRecentMealsByUserId(userId);
        return MyApiResponse.success(meals, "MY_RECENT_MEALS_SUCCESS", "최근 식단 조회 성공");
    }
    
    @Operation(summary = "내가 받은 최근 피드백 5개 조회", description = "로그인한 사용자가 받은 최근 피드백 5개를 조회합니다.")
    @GetMapping("/my/recent-feedbacks")
    public MyApiResponse<List<MealFeedbackListViewResponse>> getMyRecentFeedbacks(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        Long userId = userDetails.getUserId();
        List<MealFeedbackListViewResponse> feedbacks = mealService.getRecentFeedbacksByUserId(userId);
        return MyApiResponse.success(feedbacks, "MY_RECENT_FEEDBACKS_SUCCESS", "최근 피드백 조회 성공");
    }
    
} 
