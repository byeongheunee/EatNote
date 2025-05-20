package com.ssafy.eatnote.model.service;

import com.ssafy.eatnote.model.dao.ArticleDao;
import com.ssafy.eatnote.model.dao.ArticleFileDao;
import com.ssafy.eatnote.model.dao.BoardDao;
import com.ssafy.eatnote.model.dao.CommentDao;
import com.ssafy.eatnote.model.dao.ContentLikeDao;
import com.ssafy.eatnote.model.dao.UserDao;
import com.ssafy.eatnote.model.dto.Article;
import com.ssafy.eatnote.model.dto.ArticleFile;
import com.ssafy.eatnote.model.dto.Comment;
import com.ssafy.eatnote.model.dto.ContentLike;
import com.ssafy.eatnote.model.dto.request.ArticleCreateRequest;
import com.ssafy.eatnote.model.dto.request.ArticleUpdateRequest;
import com.ssafy.eatnote.model.dto.response.ArticleFileResponse;
import com.ssafy.eatnote.model.dto.response.ArticleResponse;
import com.ssafy.eatnote.model.dto.response.CommentResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;


@Slf4j
@Service
@RequiredArgsConstructor
public class ArticleServiceImpl implements ArticleService {

	private final UserDao userDao;
	private final BoardDao boardDao;
    private final ArticleDao articleDao;
    private final ArticleFileDao articleFileDao;
    private final ContentLikeDao contentLikeDao;
    private final CommentDao commentDao;

    @Value("${file.article-upload-dir}")
    private String uploadDir;
    
    @Override
    public Long createArticle(ArticleCreateRequest request, MultipartFile[] files, Long userId) {
        List<ArticleFile> fileEntities = new ArrayList<>();
        String imageUrl = null;

        // 1. 파일 먼저 저장 (트랜잭션 밖)
        if (files != null && files.length > 0) {
            File dir = new File(uploadDir);
            if (!dir.exists()) dir.mkdirs();

            for (int i = 0; i < files.length; i++) {
                MultipartFile file = files[i];
                if (file.isEmpty()) continue;

                try {
                    String originalName = file.getOriginalFilename();
                    String savedName = "article_" + UUID.randomUUID() + "_" + originalName;
                    File dest = new File(dir, savedName);
                    file.transferTo(dest); // 트랜잭션 밖에서 저장

                    String path = "/uploads/article/" + savedName;

                    ArticleFile articleFile = ArticleFile.builder()
                            .originalName(originalName)
                            .savedName(savedName)
                            .filePath(path)
                            .fileType(file.getContentType())
                            .build();

                    fileEntities.add(articleFile);

                    // 첫 번째 파일을 대표 이미지로
                    if (i == 0) imageUrl = path;

                } catch (IOException e) {
                    log.error("파일 저장 실패: {}", file.getOriginalFilename(), e);
                    return null;
                }
            }
        }

        // 2. 트랜잭션 안에서 DB 저장
        try {
            return saveArticleWithFiles(request, fileEntities, imageUrl, userId);
        } catch (Exception e) {
            log.error("DB 저장 실패: 게시글 저장 실패로 파일 정리 중...", e);
            // 저장된 파일 삭제
            for (ArticleFile file : fileEntities) {
                File savedFile = new File(uploadDir, file.getSavedName());
                if (savedFile.exists()) {
                    boolean deleted = savedFile.delete();
                    log.warn("파일 삭제 {}: {}", deleted ? "성공" : "실패", savedFile.getAbsolutePath());
                }
            }
            return null;
        }
    }

    @Transactional
    public Long saveArticleWithFiles(ArticleCreateRequest request, List<ArticleFile> fileEntities, String imageUrl, Long userId) {
    	
    	// 게시판 접근 권한 조회
        String accessCode = boardDao.selectAccessCodeByBoardId(request.getBoardId()); 
        
        // 사용자 유형 조회
        int userType = userDao.selectUserTypeByUserId(userId);
        
        // 접근 권한 확인
        if (!canAccessBoard(accessCode, userType)) {
            log.warn("접근 권한 없음 - boardId: {}, accessCode: {}, userType: {}", request.getBoardId(), accessCode, userType);
            throw new AccessDeniedException("게시판 접근 권한이 없습니다.");
        }
        
    	Article article = Article.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .boardId(request.getBoardId())
                .userId(userId)
                .imageUrl(imageUrl)
                .createdAt(LocalDateTime.now())
                .modifiedAt(LocalDateTime.now())
                .viewCnt(0)
                .build();

        boolean inserted = articleDao.createArticle(article) > 0;
        if (!inserted) throw new RuntimeException("게시글 DB 저장 실패");

        for (ArticleFile file : fileEntities) {
            file.setArticleId(article.getArticleId());
            articleFileDao.insertArticleFile(file);
        }

        return (long) article.getArticleId();
    }

    private boolean canAccessBoard(String accessCode, int userType) {
        switch (accessCode) {
            case "ALL":
                return true;
            case "TRAINER_ONLY":
                return userType == 1 || userType == 0;
            case "ADMIN_ONLY":
                return userType == 0;
            default:
                return false;
        }
    }
    
    @Override
    @Transactional
    public boolean updateArticle(int articleId, ArticleUpdateRequest request, MultipartFile[] files, Long userId) {
        log.info("게시글 수정 시작: articleId={}, userId={}", articleId, userId);

        // 0. 작성자 확인
        Article existing = articleDao.getArticleById(articleId);
        if (existing == null) {
            log.warn("수정 실패 - 존재하지 않는 게시글: {}", articleId);
            return false;
        }
        if (!existing.getUserId().equals(userId)) {
            log.warn("수정 실패 - 작성자 불일치: 요청자={}, 실제작성자={}", userId, existing.getUserId());
            return false;
        }

        // 1. 게시글 정보 수정
        log.info("수정 요청 데이터 - title: {}, content: {}", request.getTitle(), request.getContent());

        Article article = Article.builder()
                .articleId(articleId)
                .title(request.getTitle())
                .content(request.getContent())
                .modifiedAt(LocalDateTime.now())
                .build();

        boolean updated = articleDao.updateArticle(article) > 0;
        if (!updated) {
            log.error("게시글 수정 SQL 실패: articleId={}", articleId);
            return false;
        }
        log.info("게시글 정보 수정 완료");

        // 2. 삭제 요청된 파일 처리
        if (request.getDeleteFileIds() != null && !request.getDeleteFileIds().isEmpty()) {
            log.info("삭제 요청 파일 수: {}", request.getDeleteFileIds().size());
            for (Long fileId : request.getDeleteFileIds()) {
                log.info("삭제 시도 파일 ID: {}", fileId);
                articleFileDao.deleteById(fileId);
            }
        }

        // 3. 새 파일 업로드
        if (files != null && files.length > 0) {
            log.info("업로드 요청된 새 파일 수: {}", files.length);
            for (MultipartFile file : files) {
                if (file.isEmpty()) continue;

                try {
                    String uuid = UUID.randomUUID().toString();
                    String originalName = file.getOriginalFilename();
                    String savedName = uuid + "_" + originalName;
                    File dest = new File(uploadDir, savedName);
                    file.transferTo(dest);

                    ArticleFile articleFile = ArticleFile.builder()
                            .articleId(articleId)
                            .originalName(originalName)
                            .savedName(savedName)
                            .filePath("/uploads/article/" + savedName)
                            .fileType(file.getContentType())
                            .build();

                    articleFileDao.insertArticleFile(articleFile);
                    log.info("파일 업로드 성공: {}", savedName);
                } catch (IOException e) {
                    log.error("파일 업로드 실패", e);
                    return false;
                }
            }
        }

        // 4. 대표 이미지 재설정
        List<ArticleFile> remainingFiles = articleFileDao.getFilesByArticleId(articleId);
        String newImageUrl = !remainingFiles.isEmpty() ? remainingFiles.get(0).getFilePath() : null;

        Article imageUpdate = Article.builder()
                .articleId(articleId)
                .imageUrl(newImageUrl)
                .modifiedAt(LocalDateTime.now())
                .build();

        articleDao.updateImageUrlOnly(imageUpdate);
        log.info("대표 이미지 재설정 완료: {}", newImageUrl);

        return true;
    }

    @Override
    public boolean deleteArticle(int articleId, Long userId) {
        // 1. 게시글 작성자 확인
        Article article = articleDao.getArticleById(articleId);
        if (article == null || !article.getUserId().equals(userId)) {
            return false; // 작성자 불일치
        }

        // 2. 삭제 수행 (ON DELETE CASCADE로 첨부파일도 함께 삭제됨)
        return articleDao.deleteArticle(articleId) > 0;
    }

    // 게시글 단건 조회 (응답용)
    @Override
    public ArticleResponse getArticleResponseById(int articleId, Long loginUserId) {
        Article article = articleDao.getArticleById(articleId);
        if (article == null) return null;

        // 첨부 파일 매핑
        List<ArticleFileResponse> files = articleFileDao.getFilesByArticleId(articleId)
            .stream().map(f -> ArticleFileResponse.builder()
                .fileId(f.getFileId())
                .originalName(f.getOriginalName())
                .filePath(f.getFilePath())
                .fileType(f.getFileType())
                .build())
            .collect(Collectors.toList());

        // 좋아요/싫어요 개수는 캐싱된 컬럼 사용
        int likeCount = article.getLikeCount();
        int dislikeCount = article.getDislikeCount();

        // 내 반응 상태 조회
        String myReaction = null;
        if (loginUserId != null) {
            ContentLike like = contentLikeDao.selectByUserAndContent(loginUserId, "ARTICLE", (long) articleId);
            if (like != null) {
                myReaction = like.getLikeType(); // LIKE or DISLIKE
            }
        }

        // 댓글 목록 조회 및 트리 구조 구성
        List<Comment> commentEntities = commentDao.getCommentsByTarget("ARTICLE", (long) articleId);
        List<CommentResponse> flatList = commentEntities.stream()
            .map(comment -> CommentResponse.builder()
                .commentId(comment.getCommentId())
                .content(comment.getContent())
                .userId(comment.getUserId())
                .userNickname(comment.getUserNickname())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .depth(comment.getDepth())
                .replies(new ArrayList<>())
                .build())
            .collect(Collectors.toList());

        List<CommentResponse> commentTree = buildCommentTree(flatList);

        // 응답 조립
        return toResponse(article, files, likeCount, dislikeCount, myReaction, commentTree);
    }
    
    private ArticleResponse toResponse(Article article, List<ArticleFileResponse> files,
            int likeCount, int dislikeCount, String myReaction,
            List<CommentResponse> commentTree) {
		return ArticleResponse.builder()
			.articleId(article.getArticleId())
			.title(article.getTitle())
			.content(article.getContent())
			.boardId(article.getBoardId())
			.userId(article.getUserId())
			.userNickname(article.getUserNickname())
			.imageUrl(article.getImageUrl())
			.viewCnt(article.getViewCnt())
			.likeCount(likeCount)
			.dislikeCount(dislikeCount)
			.myReaction(myReaction)
			.attachments(files)
			.comments(commentTree)
			.createdAt(article.getCreatedAt())
			.modifiedAt(article.getModifiedAt())
			.build();
		}

    private List<CommentResponse> buildCommentTree(List<CommentResponse> flatList) {
        List<CommentResponse> rootList = new ArrayList<>();
        Map<Integer, CommentResponse> idMap = new HashMap<>();

        for (CommentResponse comment : flatList) {
            idMap.put(comment.getCommentId(), comment);
        }

        for (CommentResponse comment : flatList) {
            if (comment.getDepth() == 0) {
                rootList.add(comment);
            } else {
                // 부모는 항상 나보다 먼저 등장함 (lft 정렬 기준)
                for (int i = flatList.indexOf(comment) - 1; i >= 0; i--) {
                    CommentResponse candidate = flatList.get(i);
                    if (candidate.getDepth() == comment.getDepth() - 1) {
                        candidate.getReplies().add(comment);
                        break;
                    }
                }
            }
        }

        return rootList;
    }

    @Override
    public List<ArticleResponse> getArticlesWithFilters(Integer boardId, String keyword, String sort, Long loginUserId) {
        // 1. 게시글 목록 조회 (캐싱된 like_count, dislike_count 포함)
        List<Article> articles = articleDao.getArticlesWithFilters(boardId, keyword, sort);

        // 2. 각 게시글에 대해 ArticleResponse로 변환
        return articles.stream().map(article -> {
            String myReaction = null;

            // 로그인 사용자일 경우 내 반응 조회
            if (loginUserId != null) {
                ContentLike like = contentLikeDao.selectByUserAndContent(loginUserId, "ARTICLE", (long) article.getArticleId());
                if (like != null) {
                    myReaction = like.getLikeType(); // 'LIKE' or 'DISLIKE'
                }
            }

            return ArticleResponse.builder()
                    .articleId(article.getArticleId())
                    .title(article.getTitle())
                    .content(article.getContent())
                    .boardId(article.getBoardId())
                    .userId(article.getUserId())
                    .userNickname(article.getUserNickname())
                    .imageUrl(article.getImageUrl())
                    .viewCnt(article.getViewCnt())
                    .createdAt(article.getCreatedAt())
                    .modifiedAt(article.getModifiedAt())
                    .likeCount(article.getLikeCount())
                    .dislikeCount(article.getDislikeCount())
                    .myReaction(myReaction)
                    .attachments(null)
                    .comments(null)
                    .build();
        }).collect(Collectors.toList());
    }

    @Override
    public List<ArticleResponse> getArticlesByBoardId(int boardId) {
        return articleDao.getArticlesByBoardId(boardId)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    public List<ArticleResponse> getRecentArticlesByUserId(Long userId) {
        return articleDao.getRecentArticlesByUserId(userId)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    public boolean increaseViewCount(int articleId) {
        return articleDao.increaseViewCount(articleId) > 0;
    }

    private ArticleResponse toResponse(Article article) {
        return ArticleResponse.builder()
                .articleId(article.getArticleId())
                .title(article.getTitle())
                .content(article.getContent())
                .boardId(article.getBoardId())
                .userId(article.getUserId())
//                .userNickname(article.getUserNickname())
                .imageUrl(article.getImageUrl())
                .viewCnt(article.getViewCnt())
                .createdAt(article.getCreatedAt())
                .modifiedAt(article.getModifiedAt())
                .build();
    }
}
