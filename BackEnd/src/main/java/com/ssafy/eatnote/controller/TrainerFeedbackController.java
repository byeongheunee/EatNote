package com.ssafy.eatnote.controller;

import com.ssafy.eatnote.model.dto.FeedbackCalendarStat;
import com.ssafy.eatnote.model.dto.MealFeedback;
import com.ssafy.eatnote.model.dto.request.MealFeedbackRequest;
import com.ssafy.eatnote.model.dto.response.FolloweeFeedbackSummary;
import com.ssafy.eatnote.model.dto.response.MealListViewResponse;
import com.ssafy.eatnote.model.dto.response.MyApiResponse;
import com.ssafy.eatnote.model.dto.response.PendingFollowRequestResponse;
import com.ssafy.eatnote.model.security.UserDetailsImpl;
import com.ssafy.eatnote.model.service.MealFeedbackService;
import com.ssafy.eatnote.model.service.TrainerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/trainer/feedback")
@RequiredArgsConstructor
@Tag(name = "트레이너 피드백 API", description = "트레이너가 작성한 피드백 수정 및 삭제 API")
public class TrainerFeedbackController {

    private final MealFeedbackService feedbackService;
    private final MealFeedbackService mealFeedbackService;
    private final TrainerService trainerService;
    
    @Operation(summary = "트레이너 피드백 등록", description = "트레이너가 식단에 대한 피드백을 입력합니다.")
    @PostMapping("/meal/{mealId}")
    public MyApiResponse<?> giveFeedback(@PathVariable Long mealId,
                                         @RequestBody MealFeedbackRequest request,
                                         @AuthenticationPrincipal UserDetailsImpl userDetails) {
        try {
            Long trainerId = userDetails.getUser().getUserId();
            request.setMealId(mealId);
            MealFeedback feedback = mealFeedbackService.saveFeedback(request, trainerId);
            return MyApiResponse.success(feedback, "FEEDBACK_CREATE_SUCCESS", "피드백 등록 성공");
        } catch (Exception e) {
            return MyApiResponse.failure("FEEDBACK_CREATE_FAILED", "피드백 등록 중 오류 발생: " + e.getMessage());
        }
    }
    
    @Operation(summary = "트레이너 피드백 수정", description = "피드백 ID로 해당 피드백을 수정합니다.")
    @PutMapping("/{feedbackId}")
    public MyApiResponse<?> updateFeedback(@PathVariable Long feedbackId,
                                           @RequestBody MealFeedbackRequest request,
                                           @AuthenticationPrincipal UserDetailsImpl userDetails) {
        try {
            Long trainerId = userDetails.getUser().getUserId();
            MealFeedback updated = MealFeedback.builder()
                    .feedbackId(feedbackId)
                    .trainerId(trainerId)
                    .content(request.getContent())
                    .trainerScore(request.getTrainerScore())
                    .build();

            feedbackService.updateFeedback(feedbackId, updated);
            return MyApiResponse.success(null, "FEEDBACK_UPDATE_SUCCESS", "피드백이 수정되었습니다.");
        } catch (Exception e) {
            return MyApiResponse.failure("FEEDBACK_UPDATE_FAILED", "피드백 수정 실패: " + e.getMessage());
        }
    }

    @Operation(summary = "트레이너 피드백 삭제", description = "피드백 ID로 해당 피드백을 삭제합니다.")
    @DeleteMapping("/{feedbackId}")
    public MyApiResponse<?> deleteFeedback(@PathVariable Long feedbackId,
                                           @AuthenticationPrincipal UserDetailsImpl userDetails) {
        try {
            Long trainerId = userDetails.getUser().getUserId();
            MealFeedback feedback = feedbackService.findFeedbackById(feedbackId);
            if (!trainerId.equals(feedback.getTrainerId())) {
                return MyApiResponse.failure("FEEDBACK_DELETE_FORBIDDEN", "본인의 피드백만 삭제할 수 있습니다.");
            }

            feedbackService.deleteFeedback(feedbackId);
            return MyApiResponse.success(null, "FEEDBACK_DELETE_SUCCESS", "피드백이 삭제되었습니다.");
        } catch (Exception e) {
            return MyApiResponse.failure("FEEDBACK_DELETE_FAILED", "피드백 삭제 실패: " + e.getMessage());
        }
    }

    @Operation(summary = "트레이너 피드백 단건 조회", description = "수정을 위해 기존 피드백 내용을 불러옵니다.")
    @GetMapping("/{feedbackId}")
    public MyApiResponse<?> getFeedbackById(@PathVariable Long feedbackId,
                                            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        try {
            MealFeedback feedback = feedbackService.findFeedbackById(feedbackId);
            if (!feedback.getTrainerId().equals(userDetails.getUser().getUserId())) {
                return MyApiResponse.failure("FEEDBACK_ACCESS_FORBIDDEN", "본인의 피드백만 조회할 수 있습니다.");
            }
            return MyApiResponse.success(feedback, "FEEDBACK_DETAIL_SUCCESS", "피드백 상세 조회 성공");
        } catch (Exception e) {
            return MyApiResponse.failure("FEEDBACK_DETAIL_NOT_FOUND", "피드백 조회 실패: " + e.getMessage());
        }
    }

   

    @Operation(summary = "피드백 미작성 식단 목록 조회", description = "트레이너가 피드백하지 않은 식단 목록을 조회합니다.")
    @GetMapping("/pending")
    public MyApiResponse<?> getPendingFeedbackMeals(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        try {
            if (userDetails.getUser().getUserType() != 1) {
                return MyApiResponse.failure("FEEDBACK_ACCESS_FORBIDDEN", "트레이너만 접근할 수 있습니다.");
            }
            Long trainerId = userDetails.getUser().getUserId();
            List<MealListViewResponse> result = trainerService.getPendingFeedbackMeals(trainerId);
            return MyApiResponse.success(result, "PENDING_FEEDBACK_LIST_SUCCESS", "미작성 피드백 식단 목록 조회 성공");
        } catch (Exception e) {
            return MyApiResponse.failure("PENDING_FEEDBACK_LIST_FAILED", "식단 목록 조회 실패: " + e.getMessage());
        }
    }
    
    @Operation(summary = "피드백 달력 통계 조회", description = "트레이너의 특정 월 피드백 작성/요청 통계를 반환합니다.")
    @GetMapping("/statistics")
    public MyApiResponse<?> getFeedbackCalendarStats(
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        @RequestParam("month") String month
    ) {
        try {
            if (userDetails.getUser().getUserType() != 1) {
                return MyApiResponse.failure("CALENDAR_STATS_FORBIDDEN", "트레이너만 접근 가능합니다.");
            }

            Long trainerId = userDetails.getUser().getUserId();
            List<FeedbackCalendarStat> result = trainerService.getFeedbackCalendarStats(trainerId, month);
            return MyApiResponse.success(result, "CALENDAR_STATS_SUCCESS", "달력 통계 조회 성공");
        } catch (Exception e) {
            return MyApiResponse.failure("CALENDAR_STATS_FAILED", "통계 조회 실패: " + e.getMessage());
        }
    }
    
    @Operation(summary = "팔로우 회원별 피드백 요약 조회", description = "트레이너가 팔로우한 회원별 요청/작성 피드백 수를 조회합니다.")
    @GetMapping("/followings")
    public MyApiResponse<?> getFolloweeFeedbackSummary(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        try { 
            if (userDetails.getUser().getUserType() != 1) {
                return MyApiResponse.failure("FOLLOWEE_SUMMARY_FORBIDDEN", "트레이너만 접근할 수 있습니다.");
            }

            Long trainerId = userDetails.getUser().getUserId();
            List<FolloweeFeedbackSummary> result = trainerService.getFolloweeFeedbackSummaries(trainerId);
            return MyApiResponse.success(result, "FOLLOWEE_SUMMARY_SUCCESS", "회원 피드백 요약 조회 성공");

        } catch (Exception e) {
            return MyApiResponse.failure("FOLLOWEE_SUMMARY_FAILED", "회원 피드백 요약 조회 실패: " + e.getMessage());
        }
    }
    
}
