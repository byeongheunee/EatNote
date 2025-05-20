package com.ssafy.eatnote.model.service;

import com.ssafy.eatnote.model.dto.response.AllergyResponse;

import java.util.List;
import java.util.Map;

public interface AllergyService {

    /**
     * 알레르기 전체 목록 조회 (카테고리별 그룹)
     * @return Map<CategoryName, List<AllergyResponse>>
     */
    Map<String, List<AllergyResponse>> getAllergiesGroupedByCategory();
    
    // 회원 알레르기 종류 조회
    List<AllergyResponse> getAllergiesByUserId(Long userId);
}
