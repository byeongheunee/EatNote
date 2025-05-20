package com.ssafy.eatnote.model.service;

import com.ssafy.eatnote.model.dao.MealDao;
import com.ssafy.eatnote.model.dao.MealFeedbackDao;
import com.ssafy.eatnote.model.dao.UserDao;
import com.ssafy.eatnote.model.dto.Meal;
import com.ssafy.eatnote.model.dto.MealFeedback;
import com.ssafy.eatnote.model.dto.NotificationMessage;
import com.ssafy.eatnote.model.dto.User;
import com.ssafy.eatnote.model.dto.request.MealFeedbackRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class MealFeedbackServiceImpl implements MealFeedbackService {

	private final SimpMessagingTemplate messagingTemplate; // WebSocket 메시지 전송
	private final NotificationService notificationService;
    private final MealFeedbackDao feedbackDao;
    private final UserDao userDao;
    private final MealDao mealDao;

    @Override
    public MealFeedback saveFeedback(MealFeedbackRequest request, Long trainerId) {
        MealFeedback feedback = MealFeedback.builder()
                .mealId(request.getMealId())
                .trainerId(trainerId)
                .content(request.getContent())
                .trainerScore(request.getTrainerScore())
                .createdAt(LocalDateTime.now())
                .build();

        feedbackDao.insertFeedback(feedback);
        notifyMemberOnFeedback(feedback); // 알림 전송
        return feedback;
    }
    
    private void notifyMemberOnFeedback(MealFeedback feedback) {
        // 1. 해당 식단 정보 조회
        Meal meal = mealDao.findById(feedback.getMealId()); // user_id 포함되어야 함
        User member = userDao.selectUserById(meal.getUserId());
        User trainer = userDao.selectUserById(feedback.getTrainerId());

        NotificationMessage message = NotificationMessage.builder()
                .receiverId(member.getUserId())
                .typeCode("MEAL_FEEDBACK")
                .content(trainer.getNickname() + " 트레이너가 식단에 피드백을 남겼습니다!")
                .relatedMealId(meal.getMealId())
                .build();

        notificationService.saveNotification(message);
        messagingTemplate.convertAndSend(
            "/topic/notifications/" + member.getUserId(),
            message
        );
    }

    @Override
    public void updateFeedback(Long feedbackId, MealFeedback updatedFeedback) {
        MealFeedback existing = feedbackDao.findById(feedbackId);
        if (existing == null) {
            throw new IllegalArgumentException("해당 피드백이 존재하지 않습니다.");
        }

        existing.setContent(updatedFeedback.getContent());
        existing.setTrainerScore(updatedFeedback.getTrainerScore());
        feedbackDao.updateFeedback(existing);
    }

    @Override
    public void deleteFeedback(Long feedbackId) {
        MealFeedback existing = feedbackDao.findById(feedbackId);
        if (existing == null) {
            throw new IllegalArgumentException("해당 피드백이 존재하지 않습니다.");
        }

        feedbackDao.deleteFeedback(feedbackId);
    }
    
    @Override
    public MealFeedback findFeedbackById(Long feedbackId) {
        MealFeedback feedback = feedbackDao.findById(feedbackId);
        if (feedback == null) {
            throw new RuntimeException("❌ 피드백이 존재하지 않습니다.");
        }
        return feedback;
    }

}
