package com.ssafy.eatnote.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String os = System.getProperty("os.name").toLowerCase();
        String uploadPath;

        if (os.contains("win")) {
            uploadPath = "file:///C:/Users/SSAFY/eatnote-uploads/";

        } else {
            uploadPath = "file:/Users/leebyeongheon/eatnote-uploads/";
        }

        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(uploadPath);
    }
    
    // ** article 이미지 저장 시... **
    // 실제 저장 경로는 C:/Users/HTYANG/eatnote-uploads/articles/...
    // URL은 /uploads/article/...
    // 이 두 경로가 Spring에서 정적 리소스 경로로 매핑되어야 웹에서 이미지가 열린다.
}

