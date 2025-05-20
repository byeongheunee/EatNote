package com.ssafy.eatnote.model.service;

import com.ssafy.eatnote.model.dao.CommentDao;
import com.ssafy.eatnote.model.dao.ContentLikeDao;
import com.ssafy.eatnote.model.dto.Comment;
import com.ssafy.eatnote.model.dto.ContentLike;
import com.ssafy.eatnote.model.dto.request.CommentCreateRequest;
import com.ssafy.eatnote.model.dto.response.CommentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentDao commentDao;
    private final ContentLikeDao contentLikeDao;

    @Override
    @Transactional
    public boolean createComment(CommentCreateRequest request, Long userId) {
        int lft = 1, rgt = 2, depth = 0;
        String targetType = request.getTargetType();
        Long targetId = request.getTargetId();

        // 대댓글일 경우
        if (request.getParentCommentId() != null) {
            Comment parent = commentDao.getCommentById(request.getParentCommentId());
            if (parent == null
                    || !parent.getTargetType().equals(targetType)
                    || !parent.getTargetId().equals(targetId)) {
                return false;
            }

            commentDao.shiftRightRgt(targetType, targetId, parent.getRgt());
            commentDao.shiftRightLft(targetType, targetId, parent.getRgt());

            lft = parent.getRgt();
            rgt = parent.getRgt() + 1;
            depth = parent.getDepth() + 1;
        } else {
            List<Comment> all = commentDao.getCommentsByTarget(targetType, targetId);
            if (!all.isEmpty()) {
                int maxRgt = all.stream().mapToInt(Comment::getRgt).max().orElse(0);
                lft = maxRgt + 1;
                rgt = maxRgt + 2;
            }
        }

        Comment comment = Comment.builder()
                .content(request.getContent())
                .userId(userId)
                .targetType(targetType)
                .targetId(targetId)
                .lft(lft)
                .rgt(rgt)
                .depth(depth)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        return commentDao.insertComment(comment) > 0;
    }

    @Override
    public boolean updateComment(int commentId, String newContent, Long userId) {
        Comment comment = commentDao.getCommentById(commentId);
        if (comment == null || !comment.getUserId().equals(userId)) return false;

        comment.setContent(newContent);
        comment.setUpdatedAt(LocalDateTime.now());
        return commentDao.updateComment(comment) > 0;
    }

    @Override
    @Transactional
    public boolean deleteComment(int commentId, Long userId) {
        Comment comment = commentDao.getCommentById(commentId);
        if (comment == null || !comment.getUserId().equals(userId)) return false;

        return commentDao.deleteCommentRange(
                comment.getTargetType(),
                comment.getTargetId(),
                comment.getLft(),
                comment.getRgt()
        ) > 0;
    }

    @Override
    public List<CommentResponse> getComments(String targetType, Long targetId, Long loginUserId) {
        List<Comment> flatList = commentDao.getCommentsByTarget(targetType, targetId);

        return buildCommentTree(flatList, loginUserId);
    }

    private List<CommentResponse> buildCommentTree(List<Comment> comments, Long loginUserId) {
        List<CommentResponse> roots = new ArrayList<>();
        Stack<CommentResponse> stack = new Stack<>();

        for (Comment comment : comments) {
            // 좋아요/싫어요 수는 캐싱된 값 사용
            int likeCount = comment.getLikeCount();
            int dislikeCount = comment.getDislikeCount();

            // 로그인 사용자의 반응 조회
            String myReaction = null;
            if (loginUserId != null) {
                ContentLike like = contentLikeDao.selectByUserAndContent(loginUserId, "COMMENT", Long.valueOf(comment.getCommentId()));
                if (like != null) {
                    myReaction = like.getLikeType(); // LIKE / DISLIKE
                }
            }

            CommentResponse node = CommentResponse.builder()
                    .commentId(comment.getCommentId())
                    .content(comment.getContent())
                    .userId(comment.getUserId())
                    .userNickname(comment.getUserNickname())
                    .createdAt(comment.getCreatedAt())
                    .updatedAt(comment.getUpdatedAt())
                    .depth(comment.getDepth())
                    .likeCount(likeCount)
                    .dislikeCount(dislikeCount)
                    .myReaction(myReaction)
                    .replies(new ArrayList<>())
                    .build();

            while (!stack.isEmpty() && comment.getDepth() <= stack.peek().getDepth()) {
                stack.pop();
            }

            if (stack.isEmpty()) {
                roots.add(node);
            } else {
                stack.peek().getReplies().add(node);
            }

            stack.push(node);
        }

        return roots;
    }
}
