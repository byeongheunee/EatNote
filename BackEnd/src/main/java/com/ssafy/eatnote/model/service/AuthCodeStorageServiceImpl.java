package com.ssafy.eatnote.model.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

@Service
public class AuthCodeStorageServiceImpl implements AuthCodeStorageService {

    private final Map<String, String> codeStorage = new ConcurrentHashMap<>();
    private final Map<String, Long> timestampStorage = new ConcurrentHashMap<>();
    private final long EXPIRATION_TIME = 5 * 60 * 1000; // 5분

    @Override
    public void saveCode(String email, String code) {
        codeStorage.put(email, code);
        timestampStorage.put(email, System.currentTimeMillis());
    }

    @Override
    public boolean verifyCode(String email, String inputCode) {
        if (!codeStorage.containsKey(email)) return false;

        long savedTime = timestampStorage.get(email);
        if (System.currentTimeMillis() - savedTime > EXPIRATION_TIME) {
            codeStorage.remove(email);
            timestampStorage.remove(email);
            return false;
        }

        return codeStorage.get(email).equals(inputCode);
    }
}
