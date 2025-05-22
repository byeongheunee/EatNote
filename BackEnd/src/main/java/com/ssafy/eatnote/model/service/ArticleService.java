package com.ssafy.eatnote.model.service;

import com.ssafy.eatnote.model.dto.request.ArticleCreateRequest;
import com.ssafy.eatnote.model.dto.request.ArticleUpdateRequest;
import com.ssafy.eatnote.model.dto.response.ArticleResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ArticleService {

    // 게시글 작성 (다중 파일 포함)
    Long createArticle(ArticleCreateRequest request, MultipartFile[] files, Long userId);

    // 게시글 수정
    boolean updateArticle(int articleId, ArticleUpdateRequest request, MultipartFile[] files, Long userId);

    // 게시글 삭제
    boolean deleteArticle(int articleId, Long userId);

    // 게시글 단건 조회 (응답용)
    public ArticleResponse getArticleResponseById(int articleId, Long loginUserId);

    // 필터/검색 조건 기반 목록 조회
    public List<ArticleResponse> getArticlesWithFilters(Integer boardId, String keyword, String searchField, String sort, Long loginUserId);

    // 게시판별 게시글 목록 조회 (필요 시 유지)
    List<ArticleResponse> getArticlesByBoardId(int boardId);

    // 사용자 최근 게시글 조회 (마이페이지용)
    List<ArticleResponse> getRecentArticlesByUserId(Long userId);

    // 조회수 증가
    boolean increaseViewCount(int articleId);

}
