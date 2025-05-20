package com.ssafy.eatnote.model.service;

import java.util.List;

import com.ssafy.eatnote.model.dto.Notification;
import com.ssafy.eatnote.model.dto.NotificationMessage;

public interface NotificationService {
	
	// 알림 저장
    void saveNotification(NotificationMessage message);
    
    // 알림 목록 조회
    List<Notification> getNotificationsByUser(Long userId);
    
    // 알림 읽음 처리
    void markAsRead(Long notificationId);
    
    // 사용자의 안 읽은 알림 개수를 반환
    int countUnreadNotifications(Long userId);
    
    // 해당 사용자의 모든 알림 읽음 처리
    void markAllAsRead(Long userId);
    
}