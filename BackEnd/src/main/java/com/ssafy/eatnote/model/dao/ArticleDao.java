package com.ssafy.eatnote.model.dao;

import com.ssafy.eatnote.model.dto.Article;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface ArticleDao {

    // 글 작성
    int createArticle(Article article);

    // 글 수정
    int updateArticle(Article article);

    // 글 삭제
    int deleteArticle(int articleId);

    // 글 단건 조회
    Article getArticleById(int articleId);

    // 필터/검색 조건 기반 목록 조회
    List<Article> getArticlesWithFilters(
    	    @Param("boardId") Integer boardId,
    	    @Param("keyword") String keyword,
    	    @Param("searchField") String searchField,
    	    @Param("sort") String sort
    	);
    
    // 게시판별 글 목록
    List<Article> getArticlesByBoardId(int boardId);

    // 사용자 최근 글 목록 (예: 마이페이지)
    List<Article> getRecentArticlesByUserId(Long userId);

    // 조회수 증가
    int increaseViewCount(int articleId);
    
    // 대표 이미지만 변경
    int updateImageUrlOnly(Article article);
}
