package com.ssafy.eatnote.model.service;

import com.ssafy.eatnote.model.dto.MealFeedback;
import com.ssafy.eatnote.model.dto.request.MealFeedbackRequest;

public interface MealFeedbackService {
    MealFeedback saveFeedback(MealFeedbackRequest request, Long trainerId);

	void updateFeedback(Long feedbackId, MealFeedback updatedFeedback);

	void deleteFeedback(Long feedbackId);
	
	MealFeedback findFeedbackById(Long feedbackId);
	
	
}