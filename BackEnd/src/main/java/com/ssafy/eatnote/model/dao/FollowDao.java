package com.ssafy.eatnote.model.dao;

import java.util.List;
import java.util.Optional;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.ssafy.eatnote.model.dto.User;
import com.ssafy.eatnote.model.dto.UserFollow;

@Mapper
public interface FollowDao {

    /**
     * 팔로우 요청 중복 여부 확인
     * @param fromUserId 요청자 ID
     * @param toUserId   대상자 ID
     * @return true: 이미 존재, false: 없음
     */
    boolean existsByFromAndTo(@Param("fromUserId") Long fromUserId,
    						@Param("toUserId") Long toUserId);

    /**
     * 새로운 팔로우 요청 생성 (기본 상태: PENDING)
     * @param fromUserId 요청자 ID
     * @param toUserId   대상자 ID
     */
    void insertFollow(@Param("fromUserId") Long fromUserId,
    				@Param("toUserId") Long toUserId);
    
    /**
     * 팔로우 취소 요청
     * @param fromUserId 요청자 ID
     * @param toUserId   대상자 ID
     */
    int deleteFollow(@Param("fromUserId") Long fromUserId, 
    				@Param("toUserId") Long toUserId);
    
    /**
     * 상태값을 지정해서 팔로우 생성
     * @param follow UserFollow 객체 (from, to, status, requestedAt 포함)
     */
    void insertFollowObject(UserFollow follow);
    
    // 내가 팔로우한 사람들
    List<User> selectFollowing(@Param("userId") Long userId);

    // 나를 팔로우한 사람들
    List<User> selectFollowers(@Param("userId") Long userId);
    
    // follow_id로 단일 팔로우 조회
    Optional<UserFollow> findById(@Param("followId") Long followId);

    // 팔로우 상태 변경 (응답)
    void updateFollowStatus(@Param("followId") Long followId, @Param("status") String status);
    
    // 내가 팔로우하고 있는 트레이너 목록
    List<User> findFollowedTrainersByMember(Long memberId);
    
}
