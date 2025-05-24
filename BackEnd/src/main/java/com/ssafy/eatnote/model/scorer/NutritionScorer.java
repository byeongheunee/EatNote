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
        if ("extra".equals(mealType)) {
            return calculateSnackScore(totalCalories, sugars, sodium);
        }

        float weightRatio = getMealWeight(mealType);
        float recommendedCalories = getRecommendedCalories(user, member);
        float mealStandardCalories = recommendedCalories * weightRatio;

        double score = 0;

        // 1. 열량 점수 (0~3점)
        score += getCalorieScore(totalCalories, mealStandardCalories);

        // 2. 탄단지 점수 (각 0~2점 또는 3점, 가중치 있음)
        float carbRatio = (carbohydrates * 4) / totalCalories;
        float proteinRatio = (protein * 4) / totalCalories;
        float fatRatio = (fat * 9) / totalCalories;

        float[] carbRange = getMacronutrientRange(member.getGoal(), "carb");
        float[] proteinRange = getMacronutrientRange(member.getGoal(), "protein");
        float[] fatRange = getMacronutrientRange(member.getGoal(), "fat");

        score += getMacroScore(carbRatio, carbRange, 2.0f);
        score += getMacroScore(proteinRatio, proteinRange, 3.0f);
        score += getMacroScore(fatRatio, fatRange, 2.0f);

        // 3. 나트륨/당류 점수
        if (sodium <= 1500) score += 1;
        else if (sodium >= 2300) score -= 0.5f;

        if (sugars <= 15) score += 1;
        else if (sugars >= 30) score -= 0.5f;

        return Math.max(0f, Math.min(Math.round(score*10)/10, 10.0f)); // 0 ~ 10 제한
    }

    // 열량 점수 (비율 기반)
    private float getCalorieScore(float total, float target) {
        float ratio = total / target;
        if (ratio >= 0.9f && ratio <= 1.1f) return 3.0f;
        else if (ratio >= 0.7f && ratio <= 1.3f) return 2.0f;
        else return Math.max(0f, 3.0f - (Math.abs(ratio - 1f) * 5));  // 부드러운 감점
    }

    // 탄단지 점수 (가중치 포함)
    private float getMacroScore(float actual, float[] range, float weight) {
        float mid = (range[0] + range[1]) / 2;
        float dist = Math.abs(actual - mid);
        if (actual >= range[0] && actual <= range[1]) {
            return weight; // 완벽
        } else {
            float penalty = dist / 0.1f * (weight / 2); // 10% 벗어나면 반감
            return Math.max(0f, weight - penalty);
        }
    }

    // 간식 점수
    private float calculateSnackScore(float calories, float sugars, float sodium) {
        float score = 10.0f;

        if (calories > 200) score -= 1;
        if (calories > 500) score -= 1;
        if (sugars > 20) score -= 1;
        if (sodium > 1000) score -= 1;

        return Math.max(score, 0.0f);
    }

    // 식사 비중 (열량 가중치)
    private float getMealWeight(String mealType) {
        switch (mealType) {
            case "breakfast": return 0.25f;
            case "lunch": return 0.40f;
            case "dinner": return 0.35f;
            default: return 0.0f;
        }
    }

    // 권장 칼로리 계산
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
            bmr = (10 * weight) + (6.25f * height) - (5 * age) - 78;
        }

        float activityFactor = getActivityFactorByGoal(member.getGoal());
        return bmr * activityFactor;
    }

    // 목표에 따른 활동지수 반환
    private float getActivityFactorByGoal(String goal) {
        if (goal == null) return 1.3f;

        switch (goal.toLowerCase()) {
            case "감량": return 1.2f;
            case "유지": return 1.375f;
            case "증량": return 1.55f;
            default: return 1.3f;
        }
    }

    // 목표에 따른 탄단지 비율 범위
    private float[] getMacronutrientRange(String goal, String nutrient) {
        switch (goal.toLowerCase()) {
            case "감량":
                switch (nutrient) {
                    case "carb": return new float[]{0.40f, 0.50f};
                    case "protein": return new float[]{0.25f, 0.35f};
                    case "fat": return new float[]{0.15f, 0.25f};
                }
            case "증량":
                switch (nutrient) {
                    case "carb": return new float[]{0.50f, 0.60f};
                    case "protein": return new float[]{0.25f, 0.40f};
                    case "fat": return new float[]{0.20f, 0.35f};
                }
            default:
                switch (nutrient) {
                    case "carb": return new float[]{0.45f, 0.65f};
                    case "protein": return new float[]{0.15f, 0.30f};
                    case "fat": return new float[]{0.20f, 0.35f};
                }
        }
        return new float[]{0f, 1f};
    }
}
