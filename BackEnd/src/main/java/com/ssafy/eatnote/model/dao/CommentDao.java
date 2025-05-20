package com.ssafy.eatnote.model.dao;

import com.ssafy.eatnote.model.dto.Comment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CommentDao {

    // 댓글 전체 조회 (다형성 대상 기준, lft 정렬)
    List<Comment> getCommentsByTarget(
        @Param("targetType") String targetType,
        @Param("targetId") Long targetId
    );

    // 댓글 단건 조회
    Comment getCommentById(int commentId);

    // 새로운 댓글 삽입
    int insertComment(Comment comment);

    // 댓글 수정
    int updateComment(Comment comment);

    // 댓글 삭제 (단건)
    int deleteComment(int commentId);

    // 트리 우측 값들 shift - rgt
    int shiftRightRgt(
        @Param("targetType") String targetType,
        @Param("targetId") Long targetId,
        @Param("insertRgt") int insertRgt
    );

    // 트리 좌측 값들 shift - lft
    int shiftRightLft(
        @Param("targetType") String targetType,
        @Param("targetId") Long targetId,
        @Param("insertLft") int insertLft
    );

    // 트리 구조 범위 삭제
    int deleteCommentRange(
        @Param("targetType") String targetType,
        @Param("targetId") Long targetId,
        @Param("lft") int lft,
        @Param("rgt") int rgt
    );
}
