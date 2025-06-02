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
        message.setSubject("[EatNote] 이메일 인증 안내");

        message.setText(
            "안녕하세요, EatNote입니다.\n\n" +
            "요청하신 이메일 인증 코드입니다:\n\n" +
            "🔐 인증 코드: " + authCode + "\n\n" +
            "※ 본 인증 코드는 보안을 위해 5분간만 유효합니다.\n" +
            "다른 사람이 아닌 본인이 요청하신 것이 맞는지 확인해 주세요.\n\n" +
            "감사합니다.\n" +
            "EatNote 팀 드림"
        );

        mailSender.send(message);
    }
}
