package com.ssafy.eatnote.model.service;

import com.ssafy.eatnote.model.dao.AllergyDao;
import com.ssafy.eatnote.model.dto.response.AllergyResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AllergyServiceImpl implements AllergyService {

    private final AllergyDao allergyDao;

    @Override
    public Map<String, List<AllergyResponse>> getAllergiesGroupedByCategory() {
        List<AllergyResponse> all = allergyDao.findAllAllergies();

        // LinkedHashMap 사용 시 카테고리 순서 유지 가능!
        return all.stream()
                .collect(Collectors.groupingBy(
                        AllergyResponse::getCategory,
                        LinkedHashMap::new,
                        Collectors.toList()
                ));
    }
    
    @Override
    public List<AllergyResponse> getAllergiesByUserId(Long userId) {
        return allergyDao.findByUserId(userId);
    }
}
