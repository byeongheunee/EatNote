package com.ssafy.eatnote.model.dao;

import com.ssafy.eatnote.model.dto.Notification;
import org.apache.ibatis.annotations.Param;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface NotificationDao {
	
	// 알림 저장
    void insert(Notification notification);
    
    // 알림 목록 조회
    List<Notification> findByUserId(@Param("userId") Long userId);
    
    // 특정 알림 읽음 처리
    void markAsRead(@Param("notificationId") Long notificationId);
    
    // 사용자의 안 읽은 알림 개수를 반환
    int countUnreadByUserId(@Param("userId") Long userId);
    
    // 해당 사용자의 모든 알림 읽음 처리
    void markAllAsReadByUserId(@Param("userId") Long userId);
}