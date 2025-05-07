package com.ssafy.eatnote.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ssafy.eatnote.model.dto.request.FollowRequest;
import com.ssafy.eatnote.model.dto.request.FollowRespondRequest;
import com.ssafy.eatnote.model.dto.response.MyApiResponse;
import com.ssafy.eatnote.model.dto.response.UserFollowResponse;
import com.ssafy.eatnote.model.security.UserDetailsImpl;
import com.ssafy.eatnote.model.service.FollowService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/follow")
@RequiredArgsConstructor
@Tag(name = "팔로우 API", description = "회원 간 팔로우 요청 기능")
public class FollowController {

    private final FollowService followService;

    @PostMapping
    @Operation(summary = "팔로우 요청", description = "닉네임을 통해 다른 회원에게 팔로우 요청을 보냅니다.")
    @ApiResponses(value = {
    	    @ApiResponse(responseCode = "200", description = "팔로우 요청 성공",
    	        content = @Content(schema = @Schema(implementation = MyApiResponse.class))),
    	    @ApiResponse(responseCode = "400", description = "잘못된 요청 - 닉네임 없음, 트레이너끼리 팔로우",
    	        content = @Content(schema = @Schema(implementation = MyApiResponse.class))),
    	    @ApiResponse(responseCode = "409", description = "이미 요청된 팔로우",
    	        content = @Content(schema = @Schema(implementation = MyApiResponse.class))),
    	    @ApiResponse(responseCode = "500", description = "서버 내부 오류",
    	        content = @Content(schema = @Schema(implementation = MyApiResponse.class)))
    	})
    public ResponseEntity<?> requestFollow(@RequestBody FollowRequest request,
                                           @AuthenticationPrincipal UserDetailsImpl userDetails) {
        followService.requestFollow(userDetails.getUserId(), request.getNickname());
        return ResponseEntity.ok(MyApiResponse.success(null, "FOLLOW_REQUEST_SUCCESS", "팔로우 요청이 정상적으로 처리되었습니다."));
    }
    
    @DeleteMapping
    @Operation(summary = "팔로우 취소", description = "닉네임을 통해 팔로우를 취소합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "취소 성공"),
        @ApiResponse(responseCode = "400", description = "닉네임 없음 또는 존재하지 않음"),
        @ApiResponse(responseCode = "409", description = "취소할 수 없는 상태"),
    })
    public ResponseEntity<?> cancelFollow(@RequestBody FollowRequest request,
                                          @AuthenticationPrincipal UserDetailsImpl userDetails) {
        followService.cancelFollow(userDetails.getUserId(), request.getNickname());
        return ResponseEntity.ok(MyApiResponse.success(null, "FOLLOW_CANCEL_SUCCESS", "팔로우가 취소되었습니다."));
    }
    
    @GetMapping("/following")
    @Operation(summary = "팔로우 목록 조회", description = "내가 팔로우한 사용자 목록을 조회합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "정상 조회 (목록이 비어 있을 수 있음)", 
            content = @Content(schema = @Schema(implementation = MyApiResponse.class))),
        @ApiResponse(responseCode = "401", description = "인증 실패", 
            content = @Content(schema = @Schema(implementation = MyApiResponse.class))),
        @ApiResponse(responseCode = "500", description = "서버 오류", 
            content = @Content(schema = @Schema(implementation = MyApiResponse.class)))
    })
    public ResponseEntity<?> getFollowingList(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        List<UserFollowResponse> result = followService.getFollowingList(userDetails.getUserId());
        return ResponseEntity.ok(MyApiResponse.success(result, "FOLLOWING_LIST_SUCCESS", "팔로잉 목록 조회 성공"));
    }

    @GetMapping("/followers")
    @Operation(summary = "팔로워 목록 조회", description = "나를 팔로우한 사용자 목록을 조회합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "정상 조회 (팔로워가 없어도 빈 배열 반환)",
            content = @Content(schema = @Schema(implementation = MyApiResponse.class))),
        @ApiResponse(responseCode = "401", description = "인증 실패 (JWT 없음 또는 만료)",
            content = @Content(schema = @Schema(implementation = MyApiResponse.class))),
        @ApiResponse(responseCode = "500", description = "서버 오류",
            content = @Content(schema = @Schema(implementation = MyApiResponse.class)))
    })
    public ResponseEntity<?> getFollowerList(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        List<UserFollowResponse> result = followService.getFollowerList(userDetails.getUserId());
        return ResponseEntity.ok(MyApiResponse.success(result, "FOLLOWER_LIST_SUCCESS", "팔로워 목록 조회 성공"));
    }
    
    @PostMapping("/respond")
    @Operation(summary = "팔로우 요청 응답", description = "받은 팔로우 요청을 수락하거나 거절합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "응답 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 또는 권한 없음"),
        @ApiResponse(responseCode = "404", description = "요청 ID가 존재하지 않음"),
        @ApiResponse(responseCode = "409", description = "이미 처리된 요청")
    })
    public ResponseEntity<?> respondToFollow(@RequestBody FollowRespondRequest request,
                                             @AuthenticationPrincipal UserDetailsImpl userDetails) {
        followService.respondToFollowRequest(request.getFollowId(), request.getAction(), userDetails.getUserId());
        return ResponseEntity.ok(MyApiResponse.success(null, "FOLLOW_RESPONSE_SUCCESS", "팔로우 요청 처리 완료"));
    }
    
    
}