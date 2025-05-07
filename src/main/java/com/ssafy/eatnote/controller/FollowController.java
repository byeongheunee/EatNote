package com.ssafy.eatnote.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ssafy.eatnote.model.dto.request.FollowRequest;
import com.ssafy.eatnote.model.dto.response.MyApiResponse;
import com.ssafy.eatnote.model.security.UserDetailsImpl;
import com.ssafy.eatnote.model.service.FollowService;

import io.swagger.v3.oas.annotations.Operation;
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
    public ResponseEntity<?> requestFollow(@RequestBody FollowRequest request,
                                           @AuthenticationPrincipal UserDetailsImpl userDetails) {
        followService.requestFollow(userDetails.getUserId(), request.getNickname());
        return ResponseEntity.ok(MyApiResponse.success(null, "FOLLOW_REQUEST_SUCCESS", "팔로우 요청이 정상적으로 처리되었습니다."));
    }
}