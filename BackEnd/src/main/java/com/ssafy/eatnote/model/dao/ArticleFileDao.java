package com.ssafy.eatnote.model.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.ssafy.eatnote.model.dto.ArticleFile;

@Mapper
public interface ArticleFileDao {

    // 게시글에 첨부된 파일 전체 조회
    List<ArticleFile> getFilesByArticleId(int articleId);

    // 파일 메타데이터 저장
    int insertArticleFile(ArticleFile file);

    // 파일 전체 삭제 (게시글 삭제 시 함께 처리)
    int deleteFilesByArticleId(int articleId);

    // 파일 ID로 개별 삭제
    int deleteById(Long fileId);
}