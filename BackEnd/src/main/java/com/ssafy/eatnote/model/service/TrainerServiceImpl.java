package com.ssafy.eatnote.model.service;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.ssafy.eatnote.model.dao.FollowDao;
import com.ssafy.eatnote.model.dao.MealDao;
import com.ssafy.eatnote.model.dao.TrainerDao;
import com.ssafy.eatnote.model.dto.FeedbackCalendarStat;
import com.ssafy.eatnote.model.dto.TrainerDetails;
import com.ssafy.eatnote.model.dto.User;
import com.ssafy.eatnote.model.dto.response.FolloweeFeedbackSummary;
import com.ssafy.eatnote.model.dto.response.MealListViewResponse;
import com.ssafy.eatnote.model.dto.response.PendingFollowRequestResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TrainerServiceImpl implements TrainerService {

    private final TrainerDao trainerDao;
    private final MealDao mealDao;
    private final FollowDao followDao;

    @Override
    public TrainerDetails getDetailsByUserId(Long userId) {
        return trainerDao.findByUserId(userId);
    }
    
    @Override
    public List<MealListViewResponse> getPendingFeedbackMeals(Long trainerId) {
        // 1. 나를 팔로우한 유저 목록 조회
        List<User> followers = followDao.selectFollowers(trainerId);

        if (followers == null || followers.isEmpty()) {
            return Collections.emptyList();
        }

        // 2. 유저 ID만 추출
        List<Long> followerIds = followers.stream()
                .filter(u -> u.getUserType() == 2)  // 일반 유저만
                .map(User::getUserId)
                .collect(Collectors.toList());

        if (followerIds.isEmpty()) {
            return Collections.emptyList();
        }

        // 3. 해당 유저들의 식단 중 피드백 안 된 것만 조회
        return mealDao.findMealsWithoutFeedbackByTrainer(trainerId, followerIds);
    }


    
    @Override
    public List<FeedbackCalendarStat> getFeedbackCalendarStats(Long trainerId, String month) {
        return mealDao.getFeedbackCalendarStat(trainerId, month);
    }
    
    @Override
    public List<FolloweeFeedbackSummary> getFolloweeFeedbackSummaries(Long trainerId) {
        return trainerDao.getFolloweeFeedbackSummary(trainerId);
    }
    
    
    @Override
    public List<PendingFollowRequestResponse> getPendingFollowRequests(Long trainerId) {
        return trainerDao.findPendingFollowRequests(trainerId);
    }

}