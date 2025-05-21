package com.ssafy.eatnote.model.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.*;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import java.util.UUID;
@Service
public class MealImageService {

	@Value("${file.meal-upload-dir}")
	private String mealUploadDir;

	public Map<String, String> saveMealImage(MultipartFile file, Long userId) throws IOException {
	    String mealType = classifyMealTimeByNow(); // 아침 / 점심 / 저녁
	    String today = LocalDate.now().toString(); // 오늘 날짜

	    // 원래 파일명에서 확장자 분리
	    String originalFilename = Paths.get(file.getOriginalFilename()).getFileName().toString(); 
	    String extension = getExtension(originalFilename);

	    // 한글/특수문자 제거 (영문자, 숫자, 언더스코어만 허용)
	    String baseOriginalName = originalFilename.substring(0, originalFilename.lastIndexOf(extension))
	                                               .replaceAll("[^a-zA-Z0-9_]", "");

	    // 랜덤 문자열 (짧게 5자리만)
	    String randomSuffix = UUID.randomUUID().toString().substring(0, 5);

	    // 최종 파일명
	    String fileName = String.format("%s_%d_%s_%s_%s%s", today, userId, mealType, baseOriginalName, randomSuffix, extension);

	    // 저장 디렉토리
	    Path userDir = Paths.get(mealUploadDir, userId.toString());
	    Files.createDirectories(userDir);

	    // 저장 경로
	    Path targetPath = userDir.resolve(fileName);
	    file.transferTo(targetPath.toFile());

	    String absolutePath = targetPath.toAbsolutePath().toString();
	    String relativePath = "/uploads/meals/" + userId + "/" + fileName;

	    // 반환
	    Map<String, String> result = new HashMap<>();
	    result.put("imagePath", relativePath);
	    result.put("absolutePath", absolutePath);
	    result.put("mealType", mealType);
	    return result;
	}



	private String classifyMealTimeByNow() {
		LocalTime now = LocalTime.now();

		if (now.isAfter(LocalTime.of(5, 0)) && now.isBefore(LocalTime.of(11, 0))) {
			return "breakfast";
		} else if (now.isBefore(LocalTime.of(16, 0))) {
			return "lunch";
		} else if (now.isBefore(LocalTime.of(21, 0))) {
			return "dinner";
		} else {
			return "extra";
		}
	}

	// 사진 파일 확장자 (.png, .jpg 보존 코드)
	private String getExtension(String fileName) {
		int dotIndex = fileName.lastIndexOf('.');
		if (dotIndex > 0 && dotIndex < fileName.length() - 1) {
			return fileName.substring(dotIndex); // 예: ".jpg"
		}
		return ".jpg"; // 확장자 없으면 빈 문자열
	}

	public Map<String, Object> analyzeMealImage(String imagePath) {
	    try {
	        String url = "http://localhost:5000/analyze";

	        File imageFile = new File(imagePath);
	        if (!imageFile.exists()) {
	            throw new RuntimeException("❌ 이미지 파일이 존재하지 않습니다: " + imagePath);
	        }

	        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
	        body.add("file", new FileSystemResource(imageFile));  // ✅ 실제 이미지 파일을 보냄

	        HttpHeaders headers = new HttpHeaders();
	        headers.setContentType(MediaType.MULTIPART_FORM_DATA);  // ✅ 중요

	        HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(body, headers);

	        RestTemplate restTemplate = new RestTemplate();
	        ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
	        
	        if (response.getStatusCode() != HttpStatus.OK) {
	        	System.out.println("🔥 Flask 응답 본문: " + response.getBody());
	            throw new RuntimeException("❌ 분석 실패: " + response.getStatusCode());
	        }

	        ObjectMapper mapper = new ObjectMapper();
	        return mapper.readValue(response.getBody(), Map.class);

	    } catch (Exception e) {
	        e.printStackTrace();
	        throw new RuntimeException("❌ 분석 중 오류 발생", e);
	    }
	}



}