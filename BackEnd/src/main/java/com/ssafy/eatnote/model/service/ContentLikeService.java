package com.ssafy.eatnote.model.service;

public interface ContentLikeService {
    void toggleReaction(Long userId, String contentType, Long contentId, String likeType);
}
