package com.ssafy.eatnote.controller;

import com.ssafy.eatnote.model.dto.request.LikeRequest;
import com.ssafy.eatnote.model.dto.response.MyApiResponse;
import com.ssafy.eatnote.model.security.UserDetailsImpl;
import com.ssafy.eatnote.model.service.ContentLikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/likes")
@RequiredArgsConstructor
public class ContentLikeController {

    private final ContentLikeService contentLikeService;

    @PostMapping
    public ResponseEntity<MyApiResponse<Void>> toggleLike(
            @RequestBody LikeRequest request,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        contentLikeService.toggleReaction(
                userDetails.getUserId(),
                request.getContentType(),
                request.getContentId(),
                request.getLikeType()
        );

        return ResponseEntity.ok(
                MyApiResponse.success(null, "LIKE_TOGGLED_SUCCESS", "좋아요/싫어요 상태가 변경되었습니다.")
        );
    }
}