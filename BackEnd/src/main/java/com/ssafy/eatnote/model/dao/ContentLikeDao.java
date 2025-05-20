package com.ssafy.eatnote.model.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.ssafy.eatnote.model.dto.ContentLike;


@Mapper
public interface ContentLikeDao {

    /**
     * 로그인 사용자의 특정 콘텐츠에 대한 반응 조회
     */
    ContentLike selectByUserAndContent(
        @Param("userId") Long userId,
        @Param("contentType") String contentType,
        @Param("contentId") Long contentId
    );

    void insert(@Param("userId") Long userId,
                @Param("contentType") String contentType,
                @Param("contentId") Long contentId,
                @Param("likeType") String likeType);

    void updateLikeType(@Param("id") Long id,
                        @Param("likeType") String likeType);

    void deleteById(@Param("id") Long id);
    
    void incrementCount(String contentType, Long contentId, String likeType);
    
    void decrementCount(String contentType, Long contentId, String likeType);
    
}