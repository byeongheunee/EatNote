package com.ssafy.eatnote.model.service;

import org.springframework.stereotype.Service;

import com.ssafy.eatnote.model.dao.ContentLikeDao;
import com.ssafy.eatnote.model.dto.ContentLike;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ContentLikeServiceImpl implements ContentLikeService {

    private final ContentLikeDao contentLikeDao;

    @Override
    public void toggleReaction(Long userId, String contentType, Long contentId, String likeType) {
        ContentLike existing = contentLikeDao.selectByUserAndContent(userId, contentType, contentId);

        if (existing == null) {
            contentLikeDao.insert(userId, contentType, contentId, likeType);
            contentLikeDao.incrementCount(contentType, contentId, likeType);
        } else if (existing.getLikeType().equals(likeType)) {
            contentLikeDao.deleteById(existing.getContentLikeId());
            contentLikeDao.decrementCount(contentType, contentId, likeType);
        } else {
            contentLikeDao.updateLikeType(existing.getContentLikeId(), likeType);
            contentLikeDao.decrementCount(contentType, contentId, existing.getLikeType());
            contentLikeDao.incrementCount(contentType, contentId, likeType);
        }
    }
}
