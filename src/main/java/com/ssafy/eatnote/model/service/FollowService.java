package com.ssafy.eatnote.model.service;

public interface FollowService {
    void requestFollow(Long fromUserId, String targetNickname);
}