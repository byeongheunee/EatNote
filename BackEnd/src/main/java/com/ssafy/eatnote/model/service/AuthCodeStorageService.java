package com.ssafy.eatnote.model.service;

public interface AuthCodeStorageService {
    void saveCode(String email, String code);
    boolean verifyCode(String email, String inputCode);
}
