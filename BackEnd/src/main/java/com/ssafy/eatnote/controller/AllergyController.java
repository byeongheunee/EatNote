package com.ssafy.eatnote.controller;

import com.ssafy.eatnote.model.dto.response.AllergyResponse;
import com.ssafy.eatnote.model.dto.response.MyApiResponse;
import com.ssafy.eatnote.model.service.AllergyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/allergies")
@RequiredArgsConstructor
@Tag(name = "알러지 API", description = "알레르기 관련 API")
public class AllergyController {

    private final AllergyService allergyService;

    @GetMapping("/grouped")
    @Operation(summary = "카테고리별 알레르기 목록 조회", description = "카테고리별로 알레르기 정보를 그룹화하여 반환합니다.")
    public MyApiResponse<Map<String, List<AllergyResponse>>> getGroupedAllergies() {
        Map<String, List<AllergyResponse>> grouped = allergyService.getAllergiesGroupedByCategory();
        return MyApiResponse.success(grouped, "ALLERGY_GROUPED_LIST_SUCCESS", "카테고리별 알레르기 목록 조회 성공");
    }
}
