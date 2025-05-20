package com.ssafy.eatnote.model.service;

import com.ssafy.eatnote.model.dto.request.CommentCreateRequest;
import com.ssafy.eatnote.model.dto.response.CommentResponse;

import java.util.List;

public interface CommentService {

    // 댓글 작성 (일반 댓글 or 대댓글)
    boolean createComment(CommentCreateRequest request, Long userId);

    // 댓글 수정 (작성자만 가능)
    boolean updateComment(int commentId, String newContent, Long userId);

    // 댓글 삭제 (작성자만 가능)
    boolean deleteComment(int commentId, Long userId);

    // 대상에 해당하는 모든 댓글 조회 (트리 구조 순서)
    List<CommentResponse> getComments(String targetType, Long targetId, Long loginUserId);

}