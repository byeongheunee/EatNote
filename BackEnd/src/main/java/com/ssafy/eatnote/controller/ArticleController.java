package com.ssafy.eatnote.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssafy.eatnote.model.dto.request.ArticleCreateRequest;
import com.ssafy.eatnote.model.dto.request.ArticleUpdateRequest;
import com.ssafy.eatnote.model.dto.response.ArticleResponse;
import com.ssafy.eatnote.model.dto.response.MyApiResponse;
import com.ssafy.eatnote.model.security.UserDetailsImpl;
import com.ssafy.eatnote.model.service.ArticleService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Tag(name = "게시글 API", description = "게시글 관련 API입니다.")
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/articles")
public class ArticleController {

    private final ArticleService articleService;

    @Operation(summary = "게시글 목록 조회", description = "게시판 ID, 키워드, 정렬 조건 등을 기반으로 게시글 목록을 조회합니다.")
    @GetMapping
    public ResponseEntity<MyApiResponse<List<ArticleResponse>>> getArticles(
            @RequestParam(required = false) Integer boardId,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false, defaultValue = "ALL") String searchField, // 검색 대상: TITLE, CONTENT, NICKNAME, ALL
            @RequestParam(defaultValue = "createdAt") String sort,
            @AuthenticationPrincipal UserDetailsImpl userDetails  // 로그인 사용자 주입
    ) {
        Long loginUserId = userDetails != null ? userDetails.getUserId() : null;

        List<ArticleResponse> articles = articleService.getArticlesWithFilters(boardId, keyword, searchField.toUpperCase(), sort, loginUserId); // 사용자 ID 전달
        return ResponseEntity.ok(MyApiResponse.success(articles, "ARTICLE_LIST_SUCCESS", "게시글 목록 조회 성공"));
    }

    @Operation(summary = "게시글 단건 조회", description = "게시글 ID로 게시글 상세 정보를 조회합니다.")
    @GetMapping("/{id}")
    public ResponseEntity<MyApiResponse<ArticleResponse>> getArticle(
            @PathVariable int id,
            @AuthenticationPrincipal UserDetailsImpl userDetails // 로그인 사용자 주입
    ) {
        Long loginUserId = userDetails != null ? userDetails.getUserId() : null;

        ArticleResponse article = articleService.getArticleResponseById(id, loginUserId); // 사용자 ID 함께 전달

        if (article == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(MyApiResponse.failure("ARTICLE_NOT_FOUND", "해당 게시글이 존재하지 않습니다."));
        }

        // articleService.increaseViewCount(id);  // 조회수 증가 <- 밑에서 분리해서 진행
        return ResponseEntity.ok(MyApiResponse.success(article, "ARTICLE_FETCH_SUCCESS", "게시글 조회 성공"));
    }

    @PatchMapping("/{id}/view-count")
    public ResponseEntity<MyApiResponse<Void>> increaseViewCount(@PathVariable int id) {
        articleService.increaseViewCount(id);
        return ResponseEntity.ok(
            MyApiResponse.success(null, "ARTICLE_VIEW_INCREASED", "게시글 조회수가 증가되었습니다.")
        );
    }
    
    @Operation(summary = "게시글 작성", description = "게시글을 작성합니다. 대표 이미지를 포함한 첨부 파일들을 업로드할 수 있습니다.")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MyApiResponse<Long>> createArticle(
        @RequestPart("article") String articleJson,
        @RequestPart(value = "files", required = false) MultipartFile[] files,
        @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        Long userId = userDetails.getUser().getUserId();

        ObjectMapper mapper = new ObjectMapper();
        ArticleCreateRequest request;

        try {
            request = mapper.readValue(articleJson, ArticleCreateRequest.class);
        } catch (IOException e) {
            log.error("게시글 JSON 파싱 실패: {}", articleJson);
            e.printStackTrace();
            return ResponseEntity.badRequest().body(MyApiResponse.failure("ARTICLE_JSON_PARSE_FAILED", "게시글 정보 형식이 잘못되었습니다."));
        }

        Long articleId = articleService.createArticle(request, files, userId);
        if (articleId == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(MyApiResponse.failure("ARTICLE_CREATE_FAILED", "게시글 작성에 실패했습니다."));
        }

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(MyApiResponse.success(articleId, "ARTICLE_CREATE_SUCCESS", "게시글 작성 완료"));
    }

    @PutMapping(value = "/{articleId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "게시글 수정", description = "게시글을 수정합니다. 이미지 파일도 함께 수정할 수 있습니다.")
    public ResponseEntity<MyApiResponse<Void>> updateArticle(
            @PathVariable int articleId,
            @RequestPart("article") String articleJson,  // 🧨 JSON String으로 받기
            @RequestPart(value = "files", required = false) MultipartFile[] files,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        Long userId = userDetails.getUser().getUserId();

        // JSON 문자열을 객체로 수동 파싱
        ArticleUpdateRequest request;
        try {
            ObjectMapper mapper = new ObjectMapper();
            request = mapper.readValue(articleJson, ArticleUpdateRequest.class);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(MyApiResponse.failure("ARTICLE_JSON_PARSE_FAILED", "게시글 정보 형식이 올바르지 않습니다."));
        }

        boolean updated = articleService.updateArticle(articleId, request, files, userId);

        if (!updated) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(MyApiResponse.failure("ARTICLE_UPDATE_FAILED", "게시글 수정에 실패했습니다."));
        }

        return ResponseEntity.ok(MyApiResponse.success(null, "ARTICLE_UPDATE_SUCCESS", "게시글 수정 완료"));
    }

    @Operation(summary = "게시글 삭제", description = "게시글을 삭제합니다.")
    @DeleteMapping("/{articleId}")
    public ResponseEntity<MyApiResponse<Void>> deleteArticle(
            @PathVariable int articleId,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
    	Long userId = userDetails.getUser().getUserId(); // 현재 로그인한 사용자 ID
    	
    	boolean deleted = articleService.deleteArticle(articleId, userId);

        if (!deleted) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(MyApiResponse.failure("ARTICLE_DELETE_FAILED", "게시글 삭제에 실패했습니다."));
        }

        return ResponseEntity.ok(MyApiResponse.success(null, "ARTICLE_DELETE_SUCCESS", "게시글 삭제 완료"));
    }
    
}