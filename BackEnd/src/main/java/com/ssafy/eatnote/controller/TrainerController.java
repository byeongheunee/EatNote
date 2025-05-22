package com.ssafy.eatnote.controller;

import com.ssafy.eatnote.model.dto.response.MealListViewResponse;
import com.ssafy.eatnote.model.dto.response.MyApiResponse;
import com.ssafy.eatnote.model.dto.response.PendingFollowRequestResponse;
import com.ssafy.eatnote.model.security.UserDetailsImpl;
import com.ssafy.eatnote.model.service.TrainerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/trainer")
@RequiredArgsConstructor
@Tag(name = "트레이너 전용 API", description = "트레이너 전용 기능 관련 API")
public class TrainerController {

    private final TrainerService trainerService;

    @Operation(summary = "팔로우 요청 회원 목록 조회", description = "나에게 팔로우 요청을 보냈지만 수락되지 않은 회원 목록을 반환합니다.")
    @GetMapping("/follow/requests")
    public MyApiResponse<List<PendingFollowRequestResponse>> getPendingFollowRequests(
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        try {
            Long trainerId = userDetails.getUser().getUserId();
            List<PendingFollowRequestResponse> pendingList = trainerService.getPendingFollowRequests(trainerId);
            return MyApiResponse.success(pendingList, "FOLLOW_REQUEST_LIST_SUCCESS", "팔로우 요청 목록 조회 성공");
        } catch (Exception e) {
            return MyApiResponse.failure("FOLLOW_REQUEST_LIST_FAILED", "팔로우 요청 목록 조회 실패: " + e.getMessage());
        }
    }
    
    @Operation(
    	    summary = "나를 팔로우한 회원의 미작성 식단 조회",
    	    description = "트레이너가 피드백하지 않은 팔로워 회원의 식단 목록을 조회합니다."
    	)
    	@GetMapping("/pending")
    	public MyApiResponse<?> getMealsWithoutTrainerFeedback(
    	        @AuthenticationPrincipal UserDetailsImpl userDetails) {
    	    try {
    	        if (userDetails.getUser().getUserType() != 1) {
    	            return MyApiResponse.failure(
    	                "FEEDBACK_ACCESS_FORBIDDEN",
    	                "트레이너만 접근할 수 있습니다."
    	            );
    	        }

    	        Long trainerId = userDetails.getUser().getUserId();
    	        List<MealListViewResponse> result = trainerService.getPendingFeedbackMeals(trainerId);

    	        return MyApiResponse.success(
    	            result,
    	            "PENDING_FEEDBACK_LIST_SUCCESS",
    	            "나를 팔로우한 회원의 피드백 미작성 식단 조회 성공"
    	        );
    	    } catch (Exception e) {
    	        return MyApiResponse.failure(
    	            "PENDING_FEEDBACK_LIST_FAILED",
    	            "식단 목록 조회 실패: " + e.getMessage()
    	        );
    	    }
    	}

}