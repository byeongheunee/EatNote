package com.ssafy.eatnote.model.service;

import com.ssafy.eatnote.model.dao.FollowDao;
import com.ssafy.eatnote.model.dao.UserDao;
import com.ssafy.eatnote.model.dto.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FollowServiceImpl implements FollowService {

    private final UserDao userDao;
    private final FollowDao followDao;

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
    }

    private boolean isBothTrainer(Long fromId, Long toId) {
        return userDao.isTrainer(fromId) && userDao.isTrainer(toId);
    }
}
