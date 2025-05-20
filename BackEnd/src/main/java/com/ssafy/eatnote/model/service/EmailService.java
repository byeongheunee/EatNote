package com.ssafy.eatnote.model.service;

public interface EmailService {
    void sendAuthCode(String toEmail, String authCode);
}