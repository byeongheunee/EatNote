package com.ssafy.eatnote.model.scorer;

import com.ssafy.eatnote.model.dto.User;
import com.ssafy.eatnote.model.dto.MemberDetails;
import org.springframework.stereotype.Component;

@Component
public class NutritionScorer {

    public float calculateScore(
        float totalCalories,
        float carbohydrates,
        float protein,
        float fat,
        float sugars,
        float sodium,
        User user,
        MemberDetails member,
        String mealType
    ) {
        // 간식은 감점 위주 계산
        if ("extra".equals(mealType)) {
            return calculateSnackScore(totalCalories, sugars, sodium);
        }

        float weightRatio = getMealWeight(mealType);
        float recommendedCalories = getRecommendedCalories(user, member);
        float mealStandardCalories = recommendedCalories * weightRatio;

        float score = 0;

        // 1. 열량 점수
        float calorieRatio = totalCalories / mealStandardCalories;
        if (calorieRatio >= 0.9f && calorieRatio <= 1.1f) score += 3;
        else if (calorieRatio >= 0.8f && calorieRatio <= 1.2f) score += 2;
        else score += 1;

        // 2. 탄단지 비율 점수
        float carbRatio = carbohydrates * 4 / totalCalories;
        float proteinRatio = protein * 4 / totalCalories;
        float fatRatio = fat * 9 / totalCalories;

        float carbScore = (carbRatio >= 0.45 && carbRatio <= 0.65) ? 2.0f : 1.0f;
        float proteinScore = (proteinRatio >= 0.15 && proteinRatio <= 0.3) ? 2.0f : 1.0f;
        float fatScore = (fatRatio >= 0.2 && fatRatio <= 0.35) ? 1.0f : 0.5f;
        score += (carbScore + proteinScore + fatScore);

        // 3. 나트륨/당류
        if (sodium < 2000) score += 1;
        if (sugars < 20) score += 1;

        return Math.min(score, 10.0f);
    }

    private float calculateSnackScore(float calories, float sugars, float sodium) {
        float score = 10.0f;

        if (calories > 200) score -= 1;
        if (calories > 500) score -= 1;
        if (sugars > 20) score -= 1;
        if (sodium > 1000) score -= 1;

        return Math.max(score, 0.0f);
    }

    private float getMealWeight(String mealType) {
        switch (mealType) {
            case "breakfast": return 0.25f;
            case "lunch": return 0.40f;
            case "dinner": return 0.35f;
            default: return 0.0f;
        }
    }

    /**
     * 권장 칼로리 계산 (Mifflin-St Jeor 공식 + 활동지수 1.375)
     */
    public float getRecommendedCalories(User user, MemberDetails member) {
        float weight = member.getWeight();
        float height = member.getHeight();
        int age = (user.getAge() != null) ? user.getAge() : 30;

        float bmr;

        if ("M".equalsIgnoreCase(user.getGender())) {
            bmr = (10 * weight) + (6.25f * height) - (5 * age) + 5;
        } else if ("F".equalsIgnoreCase(user.getGender())) {
            bmr = (10 * weight) + (6.25f * height) - (5 * age) - 161;
        } else {
            bmr = (10 * weight) + (6.25f * height) - (5 * age) - 78;  // 중립값
        }

        float activityFactor = 1.375f;
        return bmr * activityFactor;
    }
}
