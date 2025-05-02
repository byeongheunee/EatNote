package com.ssafy.eatnote;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import com.ssafy.eatnote.config.jwt.JwtUtil;

@SpringBootApplication
@EnableConfigurationProperties(JwtUtil.class)
public class EatNoteApplication {

	public static void main(String[] args) {
		SpringApplication.run(EatNoteApplication.class, args);
	}

}
