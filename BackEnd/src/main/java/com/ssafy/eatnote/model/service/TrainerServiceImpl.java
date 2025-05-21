package com.ssafy.eatnote.model.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.ssafy.eatnote.model.dao.MealDao;
import com.ssafy.eatnote.model.dao.TrainerDao;
import com.ssafy.eatnote.model.dto.FeedbackCalendarStat;
import com.ssafy.eatnote.model.dto.TrainerDetails;
import com.ssafy.eatnote.model.dto.response.FolloweeFeedbackSummary;
import com.ssafy.eatnote.model.dto.response.MealListViewResponse;
import com.ssafy.eatnote.model.dto.response.PendingFollowRequestResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TrainerServiceImpl implements TrainerService {

    private final TrainerDao trainerDao;
    private final MealDao mealDao;

    @Override
    public TrainerDetails getDetailsByUserId(Long userId) {
        return trainerDao.findByUserId(userId);
    }
    
    public List<MealListViewResponse> getPendingFeedbackMeals(Long trainerId) {
        return mealDao.findMealsWithoutFeedback(trainerId);
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