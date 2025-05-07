package com.ssafy.eatnote.model.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

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
}
