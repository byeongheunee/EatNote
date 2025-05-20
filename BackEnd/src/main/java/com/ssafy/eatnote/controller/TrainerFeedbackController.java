package com.ssafy.eatnote.controller;

import com.ssafy.eatnote.model.dto.MealFeedback;
import com.ssafy.eatnote.model.dto.request.MealFeedbackRequest;
import com.ssafy.eatnote.model.dto.response.MyApiResponse;
import com.ssafy.eatnote.model.security.UserDetailsImpl;
import com.ssafy.eatnote.model.service.MealFeedbackService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

@RestController
@RequestMapping("api/trainer/feedback")
@RequiredArgsConstructor
@Tag(name = "트레이너 피드백 API", description = "트레이너가 작성한 피드백 수정 및 삭제 API")
public class TrainerFeedbackController {

    private final MealFeedbackService feedbackService;
    private final MealFeedbackService mealFeedbackService;

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
} 
