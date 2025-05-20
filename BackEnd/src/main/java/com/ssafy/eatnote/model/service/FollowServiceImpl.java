package com.ssafy.eatnote.model.service;

import com.ssafy.eatnote.model.dao.FollowDao;
import com.ssafy.eatnote.model.dao.UserDao;
import com.ssafy.eatnote.model.dto.NotificationMessage;
import com.ssafy.eatnote.model.dto.User;
import com.ssafy.eatnote.model.dto.UserFollow;
import com.ssafy.eatnote.model.dto.response.UserFollowResponse;

import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.messaging.simp.SimpMessagingTemplate;

@Service
@RequiredArgsConstructor
public class FollowServiceImpl implements FollowService {

	private final SimpMessagingTemplate messagingTemplate; // WebSocket 메시지 전송
	private final NotificationService notificationService;
    private final UserDao userDao;
    private final FollowDao followDao;

    // 팔로우 요청
    @Override
    public void requestFollow(Long fromUserId, String targetNickname) {
        // 대상 유저 조회
        User toUser = userDao.findByNickname(targetNickname)
                .orElseThrow(() -> new IllegalArgumentException("해당 닉네임의 유저가 존재하지 않습니다."));

        // 트레이너끼리 팔로우 금지
        if (isBothTrainer(fromUserId, toUser.getUserId())) {
            throw new IllegalArgumentException("트레이너끼리는 팔로우할 수 없습니다.");
        }

        // 중복 요청 방지
        if (followDao.existsByFromAndTo(fromUserId, toUser.getUserId())) {
            throw new IllegalStateException("이미 팔로우 요청을 보냈거나 수락된 상태입니다.");
        }

        // 팔로우 요청 저장
        followDao.insertFollow(fromUserId, toUser.getUserId());
        
        // 알림 전송 추가
        sendFollowRequestNotification(fromUserId, toUser);
    }

    private void sendFollowRequestNotification(Long fromUserId, User toUser) {
        User fromUser = userDao.selectUserById(fromUserId);

        NotificationMessage message = NotificationMessage.builder()
                .receiverId(toUser.getUserId())
                .typeCode("FOLLOW_REQUEST")
                .content(fromUser.getNickname() + " 님이 팔로우 요청을 보냈습니다!")
                .relatedMealId(null) // 연관 없음
                .build();

        notificationService.saveNotification(message);
        messagingTemplate.convertAndSend(
            "/topic/notifications/" + toUser.getUserId(),
            message
        );
    }
    
    private boolean isBothTrainer(Long fromId, Long toId) {
        return userDao.isTrainer(fromId) && userDao.isTrainer(toId);
    }
    
    // 팔로우 취소
    @Override
    public void cancelFollow(Long fromUserId, String targetNickname) {
    	// 대상 유저 조회
        User toUser = userDao.findByNickname(targetNickname)
                .orElseThrow(() -> new IllegalArgumentException("해당 닉네임의 유저가 존재하지 않습니다."));

        int deleted = followDao.deleteFollow(fromUserId, toUser.getUserId());
        if (deleted == 0) {
            throw new IllegalStateException("팔로우 관계가 존재하지 않아 취소할 수 없습니다.");
        }
    }
    
    
    @Override
    public List<UserFollowResponse> getFollowingList(Long userId) {
        List<User> following = followDao.selectFollowing(userId);
        return following.stream().map(UserFollowResponse::from).toList();
    }

    @Override
    public List<UserFollowResponse> getFollowerList(Long userId) {
        List<User> followers = followDao.selectFollowers(userId);
        return followers.stream().map(UserFollowResponse::from).toList();
    }
    
    @Override
    public void respondToFollowRequest(Long followId, String action, Long userId) {
        // 요청 정보 조회
    	UserFollow follow = followDao.findById(followId)
            .orElseThrow(() -> new IllegalArgumentException("해당 팔로우 요청이 존재하지 않습니다."));

        // 요청 수신자가 아닌 경우 → 권한 없음
        if (!follow.getToUserId().equals(userId)) {
            throw new IllegalArgumentException("팔로우 요청에 응답할 권한이 없습니다.");
        }

        // 이미 처리된 요청
        if (!follow.getStatus().equals("PENDING")) {
            throw new IllegalStateException("이미 처리된 팔로우 요청입니다.");
        }

        // 상태 업데이트
        String newStatus;
        if (action.equalsIgnoreCase("ACCEPT")) {
            newStatus = "ACCEPTED";
            followDao.updateFollowStatus(followId, newStatus);
        } else if (action.equalsIgnoreCase("REJECT")) {
            newStatus = "REJECTED";
            followDao.updateFollowStatus(followId, newStatus);
        } else {
            throw new IllegalArgumentException("올바르지 않은 응답 타입입니다. (ACCEPT 또는 REJECT)");
        }

        // 알림 전송
        sendFollowResponseNotification(follow, newStatus);
    }
    
    private void sendFollowResponseNotification(UserFollow follow, String status) {
        User fromUser = userDao.selectUserById(follow.getFromUserId()); // 알림 받을 사람
        User toUser = userDao.selectUserById(follow.getToUserId());     // 응답한 사람

        String content;
        if (status.equals("ACCEPTED")) {
            content = toUser.getNickname() + "님이 팔로우 요청을 수락했습니다!";
        } else {
            content = toUser.getNickname() + "님이 팔로우 요청을 거절했습니다.";
        }

        NotificationMessage message = NotificationMessage.builder()
                .receiverId(fromUser.getUserId())      // 요청 보낸 사람에게 알림
                .typeCode("FOLLOW_RESPONSE")
                .content(content)
                .relatedMealId(null)
                .build();

        notificationService.saveNotification(message);
        messagingTemplate.convertAndSend(
            "/topic/notifications/" + fromUser.getUserId(),
            message
        );
    }
    
}
