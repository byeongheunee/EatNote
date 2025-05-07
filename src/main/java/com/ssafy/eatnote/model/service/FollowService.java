package com.ssafy.eatnote.model.service;

import java.util.List;

import com.ssafy.eatnote.model.dto.response.UserFollowResponse;

public interface FollowService {
	
	// 팔로우 요청
    void requestFollow(Long fromUserId, String targetNickname);
    
    // 팔로우 취소
    void cancelFollow(Long fromUserId, String targetNickname);
    
    // 내가 팔로우 하는 사람들 조회 
    List<UserFollowResponse> getFollowingList(Long userId);
    
    // 나를 팔로우 하는 사람들 조회
    List<UserFollowResponse> getFollowerList(Long userId);
    
    // 팔로우 수락 or 거절
    void respondToFollowRequest(Long followId, String action, Long userId);
}