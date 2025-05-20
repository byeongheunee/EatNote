package com.ssafy.eatnote.controller;

import com.ssafy.eatnote.model.dto.request.CommentCreateRequest;
import com.ssafy.eatnote.model.dto.response.CommentResponse;
import com.ssafy.eatnote.model.dto.response.MyApiResponse;
import com.ssafy.eatnote.model.security.UserDetailsImpl;
import com.ssafy.eatnote.model.service.CommentService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "댓글 API", description = "댓글 관련 API입니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/comments")
public class CommentController {

    private final CommentService commentService;

    // 댓글 조회
    @Operation(summary = "댓글 목록 조회", description = "대상에 해당하는 댓글을 트리 구조로 조회합니다.")
    @GetMapping
    public ResponseEntity<MyApiResponse<List<CommentResponse>>> getComments(
            @RequestParam String targetType,
            @RequestParam Long targetId,
            @AuthenticationPrincipal UserDetailsImpl userDetails  // ✅ 로그인 사용자 정보 주입
    ) {
        Long loginUserId = userDetails != null ? userDetails.getUserId() : null;

        List<CommentResponse> comments = commentService.getComments(targetType, targetId, loginUserId); // ✅ 사용자 ID 전달
        return ResponseEntity.ok(MyApiResponse.success(comments, "COMMENT_LIST_SUCCESS", "댓글 목록 조회 성공"));
    }

    // 댓글 등록
    @Operation(summary = "댓글 등록", description = "댓글 또는 대댓글을 등록합니다.")
    @PostMapping
    public ResponseEntity<MyApiResponse<Void>> createComment(
            @RequestBody CommentCreateRequest request,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        Long userId = userDetails.getUser().getUserId();

        boolean result = commentService.createComment(request, userId);

        if (!result) {
            return ResponseEntity.badRequest()
                    .body(MyApiResponse.failure("COMMENT_CREATE_FAILED", "댓글 작성에 실패했습니다."));
        }

        return ResponseEntity.ok(MyApiResponse.success(null, "COMMENT_CREATE_SUCCESS", "댓글 작성 성공"));
    }
    
    // 댓글 수정
    @Operation(summary = "댓글 수정", description = "본인이 작성한 댓글을 수정합니다.")
    @PutMapping("/{commentId}")
    public ResponseEntity<MyApiResponse<Void>> updateComment(
            @PathVariable int commentId,
            @RequestBody CommentCreateRequest request,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        Long userId = userDetails.getUser().getUserId();

        boolean result = commentService.updateComment(commentId, request.getContent(), userId);

        if (!result) {
            return ResponseEntity.status(403)
                    .body(MyApiResponse.failure("COMMENT_UPDATE_FAILED", "댓글 수정 권한이 없거나 실패했습니다."));
        }

        return ResponseEntity.ok(MyApiResponse.success(null, "COMMENT_UPDATE_SUCCESS", "댓글 수정 성공"));
    }
    
    // 댓글 삭제
    @Operation(summary = "댓글 삭제", description = "본인이 작성한 댓글을 삭제합니다.")
    @DeleteMapping("/{commentId}")
    public ResponseEntity<MyApiResponse<Void>> deleteComment(
            @PathVariable int commentId,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        Long userId = userDetails.getUser().getUserId();

        boolean result = commentService.deleteComment(commentId, userId);

        if (!result) {
            return ResponseEntity.status(403)
                    .body(MyApiResponse.failure("COMMENT_DELETE_FAILED", "댓글 삭제 권한이 없거나 실패했습니다."));
        }

        return ResponseEntity.ok(MyApiResponse.success(null, "COMMENT_DELETE_SUCCESS", "댓글 삭제 성공"));
    }
    
}
