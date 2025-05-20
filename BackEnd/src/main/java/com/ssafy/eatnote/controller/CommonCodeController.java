package com.ssafy.eatnote.controller;

import com.ssafy.eatnote.model.dto.CommonCode;
import com.ssafy.eatnote.model.dto.response.MyApiResponse;
import com.ssafy.eatnote.model.service.CommonCodeService;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/common")
@RequiredArgsConstructor
public class CommonCodeController {

    private final CommonCodeService commonCodeService;

    @GetMapping("/all")
    @Operation(summary = "모든 공통 코드 조회", description = "gender_type, user_type 등 모든 ENUM 코드를 통합 조회합니다.")
    public MyApiResponse<Map<String, List<CommonCode>>> getAllCommonCodes() {
        return MyApiResponse.success(
                commonCodeService.getAllCommonCodes(),
                "COMMON_CODE_ALL_SUCCESS",
                "모든 공통 코드 목록 조회 성공"
        );
    }

    @GetMapping("/gender")
    @Operation(summary = "성별 코드 조회", description = "gender_type 테이블의 값 조회")
    public MyApiResponse<List<CommonCode>> getGenderCodes() {
        return MyApiResponse.success(
                commonCodeService.getGenderCodes(),
                "COMMON_CODE_GENDER_SUCCESS",
                "성별 코드 목록 조회 성공"
        );
    }

    @GetMapping("/user-status")
    @Operation(summary = "회원 상태 코드 조회", description = "user_status 테이블의 값 조회")
    public MyApiResponse<List<CommonCode>> getUserStatusCodes() {
        return MyApiResponse.success(
                commonCodeService.getUserStatusCodes(),
                "COMMON_CODE_USER_STATUS_SUCCESS",
                "회원 상태 코드 목록 조회 성공"
        );
    }

    @GetMapping("/user-type")
    @Operation(summary = "회원 유형 코드 조회", description = "user_type 테이블의 값 조회")
    public MyApiResponse<List<CommonCode>> getUserTypeCodes() {
        return MyApiResponse.success(
                commonCodeService.getUserTypeCodes(),
                "COMMON_CODE_USER_TYPE_SUCCESS",
                "회원 유형 코드 목록 조회 성공"
        );
    }

    @GetMapping("/goal")
    @Operation(summary = "회원 목표 코드 조회", description = "goal_type 테이블의 값 조회")
    public MyApiResponse<List<CommonCode>> getGoalCodes() {
        return MyApiResponse.success(
                commonCodeService.getGoalCodes(),
                "COMMON_CODE_GOAL_SUCCESS",
                "회원 목표 코드 목록 조회 성공"
        );
    }

    @GetMapping("/follow-status")
    @Operation(summary = "팔로우 상태 코드 조회", description = "follow_status 테이블의 값 조회")
    public MyApiResponse<List<CommonCode>> getFollowStatusCodes() {
        return MyApiResponse.success(
                commonCodeService.getFollowStatusCodes(),
                "COMMON_CODE_FOLLOW_STATUS_SUCCESS",
                "팔로우 상태 코드 목록 조회 성공"
        );
    }

    @GetMapping("/notification-type")
    @Operation(summary = "알림 타입 코드 조회", description = "notification_type 테이블의 값 조회")
    public MyApiResponse<List<CommonCode>> getNotificationTypeCodes() {
        return MyApiResponse.success(
                commonCodeService.getNotificationTypeCodes(),
                "COMMON_CODE_NOTIFICATION_TYPE_SUCCESS",
                "알림 타입 코드 목록 조회 성공"
        );
    }

    @GetMapping("/board-access")
    @Operation(summary = "게시판 접근 권한 코드 조회", description = "board_access_level 테이블의 값 조회")
    public MyApiResponse<List<CommonCode>> getBoardAccessLevelCodes() {
        return MyApiResponse.success(
                commonCodeService.getBoardAccessLevelCodes(),
                "COMMON_CODE_BOARD_ACCESS_SUCCESS",
                "게시판 접근 권한 코드 목록 조회 성공"
        );
    }

    @GetMapping("/comment-target-type")
    @Operation(summary = "댓글 대상 타입 코드 조회", description = "comment_target_type 테이블의 값 조회")
    public MyApiResponse<List<CommonCode>> getCommentTargetTypeCodes() {
        return MyApiResponse.success(
                commonCodeService.getCommentTargetTypeCodes(),
                "COMMON_CODE_COMMENT_TARGET_SUCCESS",
                "댓글 대상 타입 코드 목록 조회 성공"
        );
    }

    @GetMapping("/like-type")
    @Operation(summary = "좋아요/싫어요 타입 코드 조회", description = "like_type 테이블의 값 조회")
    public MyApiResponse<List<CommonCode>> getLikeTypeCodes() {
        return MyApiResponse.success(
                commonCodeService.getLikeTypeCodes(),
                "COMMON_CODE_LIKE_TYPE_SUCCESS",
                "좋아요/싫어요 타입 코드 목록 조회 성공"
        );
    }
}
