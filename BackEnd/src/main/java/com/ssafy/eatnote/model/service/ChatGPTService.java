package com.ssafy.eatnote.model.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssafy.eatnote.model.dto.MemberDetails;
import com.ssafy.eatnote.model.dto.User;
import com.ssafy.eatnote.model.dto.request.ChatGPTRequest;
import com.ssafy.eatnote.model.dto.response.ChatGPTResponse;
import com.ssafy.eatnote.model.dto.response.WeeklyFeedbackResponse;
import com.ssafy.eatnote.model.dto.response.WeeklyNutritionStatsResponse;

@Service
public class ChatGPTService {

    private final String API_KEY = "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";
    private final String API_URL = "https://api.openai.com/v1/chat/completions";

    private final RestTemplate restTemplate;

    public ChatGPTService(RestTemplateBuilder builder) {
        this.restTemplate = builder.build();
    }

    public String ask(String prompt) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(API_KEY);

        List<ChatGPTRequest.Message> messages = new ArrayList<>();
        messages.add(new ChatGPTRequest.Message("user", prompt));

        ChatGPTRequest request = new ChatGPTRequest("gpt-3.5-turbo", messages);

        HttpEntity<ChatGPTRequest> entity = new HttpEntity<>(request, headers);
        ResponseEntity<ChatGPTResponse> response = restTemplate.exchange(
                API_URL,
                HttpMethod.POST,
                entity,
                ChatGPTResponse.class
        );

        return response.getBody().getChoices().get(0).getMessage().getContent();
    }

    public String analyzeImageWithGPT(String imagePath, User user, MemberDetails member, String mealType) throws IOException {
        String base64Image = Base64.getEncoder().encodeToString(Files.readAllBytes(Path.of(imagePath)));

        String prompt = buildVisionPrompt(user, member, mealType);

        Map<String, Object> imageMap = Map.of("url", "data:image/jpeg;base64," + base64Image);
        Map<String, Object> textContent = Map.of("type", "text", "text", prompt);
        Map<String, Object> imageContent = Map.of("type", "image_url", "image_url", imageMap);

        Map<String, Object> message = Map.of("role", "user", "content", List.of(textContent, imageContent));
        Map<String, Object> body = Map.of("model", "gpt-4o", "messages", List.of(message), "max_tokens", 1000);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(API_KEY);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(API_URL, request, String.class);

        return response.getBody();
    }

    public Map<String, Object> parseJsonResponse(String response) throws JSONException {
        JSONObject json = new JSONObject(response);
        String content = json.getJSONArray("choices")
                             .getJSONObject(0)
                             .getJSONObject("message")
                             .getString("content");

        // ✅ GPT 응답에 포함된 마크다운 코드 블럭 제거
        content = content.trim();
        if (content.startsWith("```json")) {
            content = content.substring(7).trim();  // "```json" 제거
        } else if (content.startsWith("```")) {
            content = content.substring(3).trim();  // "```" 제거
        }
        if (content.endsWith("```")) {
            content = content.substring(0, content.length() - 3).trim();  // 맨 뒤 "```" 제거
        }

        JSONObject result = new JSONObject(content);  // 이제 안전하게 파싱 가능
        
        JSONArray foodsArray = result.getJSONArray("foods");
        if (foodsArray.length() == 0) {
            throw new IllegalArgumentException("GPT Vision 분석 결과 음식이 감지되지 않았습니다. 저장을 중단합니다.");
        }
        
        Map<String, Object> parsed = new HashMap<>();
        parsed.put("foods", jsonArrayToList(result.getJSONArray("foods")));
        parsed.put("calories", result.getDouble("calories"));
        parsed.put("carbohydrates", result.getDouble("carbohydrates"));
        parsed.put("protein", result.getDouble("protein"));
        parsed.put("fat", result.getDouble("fat"));
        parsed.put("sugars", result.getDouble("sugars"));
        parsed.put("sodium", result.getDouble("sodium"));
        parsed.put("feedback", result.getString("feedback"));
        return parsed;
    }


    private List<String> jsonArrayToList(JSONArray array) throws JSONException {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
            list.add(array.getString(i));
        }
        return list;
    }
    
    private String buildVisionPrompt(User user, MemberDetails member, String mealType) {
        StringBuilder sb = new StringBuilder();

        sb.append(String.format("%s세 %s의 %s 식단 사진입니다.\n", 
            user.getAge(), 
            user.getGender().equals("M") ? "남성" : "여성",
            mealType.equals("lunch")?"점심": (mealType.equals("dinner")?"저녁":(mealType.equals("breakfast")?"아침":"간식") )
        ));

        sb.append("신체 정보:\n");
        if (member.getHeight() != null)
            sb.append(String.format("- 키: %.1fcm\n", member.getHeight()));
        if (member.getWeight() != null)
            sb.append(String.format("- 몸무게: %.1fkg\n", member.getWeight()));
        if (member.getBmi() != null)
            sb.append(String.format("- BMI: %.1f\n", member.getBmi()));
        if (member.getBodyFatPercentage() != null)
            sb.append(String.format("- 체지방률: %.1f%%\n", member.getBodyFatPercentage()));
        if (member.getBodyFatMass() != null)
            sb.append(String.format("- 체지방량: %.1fg\n", member.getBodyFatMass()));
        if (member.getSkeletalMuscle() != null)
            sb.append(String.format("- 골격근량: %.1fg\n", member.getSkeletalMuscle()));
        if (member.getProtein() != null)
            sb.append(String.format("- 단백질량: %.1fg\n", member.getProtein()));
        if (member.getGoal() != null)
            sb.append(String.format("- 목표: %s\n", member.getGoal()));

        sb.append("\n이 이미지를 분석 후 다음 정보를 예측해서 반드시 JSON 형식 그대로만 응답 작성해서 보내줘. 설명, 인사, 코드 블럭(예: ```json)은 포함하지 마.:\n")
          .append("1. 음식 이름 목록\n")
          .append("2. 각 음식의 탄수화물, 단백질, 지방, 당류, 나트륨 총합\n")
          .append("3. 전체 칼로리\n")
          .append("4. 피드백 (2~3줄 요약)\n\n")
          .append("응답은 아래 JSON 형식에 따라 구성해줘:\n")
          .append("""
          {
            "foods": ["음식1", "음식2"],
            "calories": 123.4,
            "carbohydrates": 45.6,
            "protein": 23.4,
            "fat": 12.3,
            "sugars": 4.5,
            "sodium": 890.1,
            "feedback": "20세 남성이 감량을 목표로 하는 경우, 부대찌개는 다소 지방과 나트륨 함량이 높아서 식단에 포함될 경우 적당하지 않을 수 있습니다. 대신에 단백질과 영양소가 풍부한 채소나 곡물을 중심으로 한 식단을 고려하는 것이 좋을 것입니다. 또한, 칼로리를 제어하고 운동을 조합하여 건강하게 감량을 이룰 수 있을 것입니다"
          }
          """);

        return sb.toString();
    }
    
    public WeeklyFeedbackResponse generateWeeklyNutritionFeedback(User user, MemberDetails member, WeeklyNutritionStatsResponse stats) throws IOException {
        // 1. 프롬프트 생성 (buildNutritionPrompt 내부에서 문자열 완성)
        String prompt = buildNutritionPrompt(user, member, stats);

        // 2. GPT 요청 구성
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(API_KEY);

        List<Map<String, String>> messages = List.of(
            Map.of("role", "system", "content", "당신은 전문 영양 상담사입니다."),
            Map.of("role", "user", "content", prompt)
        );

        Map<String, Object> body = Map.of(
            "model", "gpt-4o",
            "messages", messages
        );

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(API_URL, request, String.class);

        // 3. 응답 파싱
        String responseText = extractJsonFromGpt(response.getBody());
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(responseText, WeeklyFeedbackResponse.class);
    }
    
    private String extractJsonFromGpt(String rawResponse) {
        // JSON만 남기고 코드 블럭 등 제거
        String content;
        try {
            JSONObject json = new JSONObject(rawResponse);
            content = json.getJSONArray("choices")
                          .getJSONObject(0)
                          .getJSONObject("message")
                          .getString("content")
                          .trim();
        } catch (JSONException e) {
            throw new RuntimeException("GPT 응답 JSON 파싱 실패", e);
        }

        if (content.startsWith("```json")) {
            content = content.substring(7).trim();
        } else if (content.startsWith("```")) {
            content = content.substring(3).trim();
        }
        if (content.endsWith("```")) {
            content = content.substring(0, content.length() - 3).trim();
        }

        return content;
    }
    
    private String buildNutritionPrompt(User user, MemberDetails member, WeeklyNutritionStatsResponse stats) {
        StringBuilder sb = new StringBuilder();

        sb.append("다음은 사용자의 신체 정보와 주간 식사 통계입니다. 이 정보를 바탕으로 피드백을 JSON 형식으로 작성해주세요.\n")
          .append("1. 주의할 점 (1~2문장)\n")
          .append("2. 개선 팁 (1~2문장)\n")
          .append("3. 추천 음식 목록 (최대 3개, 이유 포함)\n")
          .append("꼭 아래 형식의 JSON으로만 응답하세요. 설명, 인사, 코드 블럭은 절대 넣지 마세요.\n\n")
          .append("""
          {
            "warning": "...",
            "tip": "...",
            "recommendedFoods": [
              { "name": "...", "reason": "..." }
            ]
          }
          """);

        sb.append("\n[사용자 정보]\n");
        sb.append("- 성별: ").append(user.getGender().equals("M") ? "남성" : "여성").append("\n");
        if (member.getHeight() != null) sb.append("- 키: ").append(member.getHeight()).append("cm\n");
        if (member.getWeight() != null) sb.append("- 몸무게: ").append(member.getWeight()).append("kg\n");
        if (member.getGoal() != null) sb.append("- 목표: ").append(member.getGoal()).append("\n");

        sb.append("\n[주간 식사 통계]\n");
        sb.append("- 평균 칼로리: ").append(stats.getAvgCalories()).append("kcal\n");
        sb.append("- 평균 탄수화물: ").append(stats.getAvgCarbohydrates()).append("g\n");
        sb.append("- 평균 단백질: ").append(stats.getAvgProtein()).append("g\n");
        sb.append("- 평균 지방: ").append(stats.getAvgFat()).append("g\n");
        sb.append("- 총 칼로리: ").append(stats.getTotalCalories()).append("kcal\n");
        sb.append("- 식사 횟수: ").append(stats.getMealCount()).append("회\n");

        return sb.toString();
    }
}
