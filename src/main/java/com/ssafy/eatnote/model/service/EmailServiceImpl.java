package com.ssafy.eatnote.model.service;

import org.springframework.stereotype.Service;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Override
    public void sendAuthCode(String toEmail, String authCode) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("[EatNote] 이메일 인증 코드");
        message.setText("인증 코드: " + authCode + "\n5분 이내에 입력해주세요!");
        mailSender.send(message);
    }
}
