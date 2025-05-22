package com.ssafy.eatnote.model.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ssafy.eatnote.model.dao.NotificationDao;
import com.ssafy.eatnote.model.dto.Notification;
import com.ssafy.eatnote.model.dto.NotificationMessage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class NotificationServiceImpl implements NotificationService {

    private final NotificationDao notificationDao;
    
	// 알림 저장
    @Override
    public void saveNotification(NotificationMessage message) {
        Notification notification = Notification.builder()
                .userId(message.getReceiverId())
                .typeCode(message.getTypeCode())
                .content(message.getContent())
                .relatedMealId(message.getRelatedMealId())
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .build();
        
        log.info("📥 알림 DB 저장: {}", message);
        
        notificationDao.insert(notification);
    }
    
    // 알림 목록 조회
    @Override
    public List<Notification> getNotificationsByUser(Long userId) {
        return notificationDao.findByUserId(userId);
    }
    
    // 알림 읽음 처리
    @Override
    public void markAsRead(Long notificationId) {
        notificationDao.markAsRead(notificationId);
    }
    
    // 사용자의 안 읽은 알림 개수를 반환
    @Override
    public int countUnreadNotifications(Long userId) {
        return notificationDao.countUnreadByUserId(userId);
    }
    
    // 해당 사용자의 모든 알림 읽음 처리
    @Override
    public void markAllAsRead(Long userId) {
        notificationDao.markAllAsReadByUserId(userId);
    }
    
}
