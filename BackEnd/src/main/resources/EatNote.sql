DROP DATABASE IF EXISTS eatnote;

CREATE DATABASE eatnote;

USE eatnote;


-- ---------------------------------------------------------------------
-- `users` <<< ENUM 정보
-- ---------------------------------------------------------------------

CREATE TABLE gender_type (
    gender_code VARCHAR(10) PRIMARY KEY,  -- 'M', 'F', 'OTHER'
    label       VARCHAR(50) NOT NULL      -- '남성', '여성', '기타'
);

INSERT INTO gender_type VALUES
('M', '남성'),
('F', '여성'),
('OTHER', '기타');


CREATE TABLE user_status (
    status_code VARCHAR(20) PRIMARY KEY,   -- 'ACTIVE', 'INACTIVE', 'DELETED'
    description VARCHAR(100)
);

INSERT INTO user_status VALUES
('ACTIVE', '정상'),
('INACTIVE', '일시정지'),
('DELETED', '탈퇴');


CREATE TABLE user_type (
    type_code  TINYINT PRIMARY KEY,     -- 0, 1, 2
    label      VARCHAR(50) NOT NULL     -- '관리자', '트레이너', '회원'
);

INSERT INTO user_type VALUES
(0, '관리자'),
(1, '트레이너'),
(2, '회원');

-- ---------------------------------------------------------------------
-- Table `users` <<< 사용자 
-- ---------------------------------------------------------------------
CREATE TABLE users (
    user_id        BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    email          VARCHAR(100) NOT NULL UNIQUE,
    password       VARCHAR(255) NOT NULL,
    name           VARCHAR(50) NOT NULL,
    nickname       VARCHAR(50) NOT NULL UNIQUE,
    age 		   INT NOT NULL,
    gender         VARCHAR(10),
    profile_image  VARCHAR(255),
    user_type      TINYINT NOT NULL,
    status         VARCHAR(20) DEFAULT 'ACTIVE',
    created_at     DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at     DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    last_login     DATETIME,

    FOREIGN KEY (gender) REFERENCES gender_type(gender_code),
    FOREIGN KEY (user_type) REFERENCES user_type(type_code),
    FOREIGN KEY (status) REFERENCES user_status(status_code)
);


-- 회원
INSERT INTO users (email, password, name, nickname, age, gender, profile_image, user_type, status, created_at, updated_at, last_login) VALUES
('member1@eatnote.com', '$2a$10$K7u3K6Hf8yEaq1bJLC6neOxXn1KMPXGOGYQzjvshPH3DlZAzEGC8K', '홍회원1', '건강한하늘', 35, 'F', '/images/profile1.png', 2, 'ACTIVE', '2024-01-01 00:00:00', '2024-03-01 00:00:00', '2024-02-29 19:00:00'),
('member2@eatnote.com', '$2a$10$K7u3K6Hf8yEaq1bJLC6neOxXn1KMPXGOGYQzjvshPH3DlZAzEGC8K', '홍회원2', '운동하는민수', 20, 'M', '/images/profile2.png', 2, 'ACTIVE', '2024-01-02 00:00:00', '2024-03-02 00:00:00', '2024-03-01 19:00:00'),
('member3@eatnote.com', '$2a$10$K7u3K6Hf8yEaq1bJLC6neOxXn1KMPXGOGYQzjvshPH3DlZAzEGC8K', '홍회원3', '슬기로운생활', 32, 'M', '/images/profile3.png', 2, 'ACTIVE', '2024-01-03 00:00:00', '2024-03-03 00:00:00', '2024-03-02 19:00:00'),
('member4@eatnote.com', '$2a$10$K7u3K6Hf8yEaq1bJLC6neOxXn1KMPXGOGYQzjvshPH3DlZAzEGC8K', '홍회원4', '식단요정', 34, 'M', '/images/profile4.png', 2, 'ACTIVE', '2024-01-04 00:00:00', '2024-03-04 00:00:00', '2024-03-03 19:00:00'),
('member5@eatnote.com', '$2a$10$K7u3K6Hf8yEaq1bJLC6neOxXn1KMPXGOGYQzjvshPH3DlZAzEGC8K', '홍회원5', '다이어터지은', 38, 'M', '/images/profile5.png', 2, 'ACTIVE', '2024-01-05 00:00:00', '2024-03-05 00:00:00', '2024-03-04 19:00:00'),
('member6@eatnote.com', '$2a$10$K7u3K6Hf8yEaq1bJLC6neOxXn1KMPXGOGYQzjvshPH3DlZAzEGC8K', '홍회원6', '단백질충전', 27, 'M', '/images/profile6.png', 2, 'ACTIVE', '2024-01-06 00:00:00', '2024-03-06 00:00:00', '2024-03-05 19:00:00'),
('member7@eatnote.com', '$2a$10$K7u3K6Hf8yEaq1bJLC6neOxXn1KMPXGOGYQzjvshPH3DlZAzEGC8K', '홍회원7', '오늘도운동', 22, 'F', '/images/profile7.png', 2, 'ACTIVE', '2024-01-07 00:00:00', '2024-03-07 00:00:00', '2024-03-06 19:00:00'),
('member8@eatnote.com', '$2a$10$K7u3K6Hf8yEaq1bJLC6neOxXn1KMPXGOGYQzjvshPH3DlZAzEGC8K', '홍회원8', '칼로리헌터', 26, 'M', '/images/profile8.png', 2, 'ACTIVE', '2024-01-08 00:00:00', '2024-03-08 00:00:00', '2024-03-07 19:00:00'),
('member9@eatnote.com', '$2a$10$K7u3K6Hf8yEaq1bJLC6neOxXn1KMPXGOGYQzjvshPH3DlZAzEGC8K', '홍회원9', '헬스왕철수', 36, 'F', '/images/profile9.png', 2, 'ACTIVE', '2024-01-09 00:00:00', '2024-03-09 00:00:00', '2024-03-08 19:00:00'),
('member10@eatnote.com', '$2a$10$K7u3K6Hf8yEaq1bJLC6neOxXn1KMPXGOGYQzjvshPH3DlZAzEGC8K', '홍회원10', '런닝맨진수', 35, 'F', '/images/profile10.png', 2, 'ACTIVE', '2024-01-10 00:00:00', '2024-03-10 00:00:00', '2024-03-09 19:00:00'),
('member11@eatnote.com', '$2a$10$K7u3K6Hf8yEaq1bJLC6neOxXn1KMPXGOGYQzjvshPH3DlZAzEGC8K', '홍회원11', '홈트민정', 33, 'M', '/images/profile11.png', 2, 'ACTIVE', '2024-01-11 00:00:00', '2024-03-11 00:00:00', '2024-03-10 19:00:00'),
('member12@eatnote.com', '$2a$10$K7u3K6Hf8yEaq1bJLC6neOxXn1KMPXGOGYQzjvshPH3DlZAzEGC8K', '홍회원12', '지방파괴자', 20, 'F', '/images/profile12.png', 2, 'ACTIVE', '2024-01-12 00:00:00', '2024-03-12 00:00:00', '2024-03-11 19:00:00'),
('member13@eatnote.com', '$2a$10$K7u3K6Hf8yEaq1bJLC6neOxXn1KMPXGOGYQzjvshPH3DlZAzEGC8K', '홍회원13', '근손실방지', 22, 'M', '/images/profile13.png', 2, 'ACTIVE', '2024-01-13 00:00:00', '2024-03-13 00:00:00', '2024-03-12 19:00:00'),
('member14@eatnote.com', '$2a$10$K7u3K6Hf8yEaq1bJLC6neOxXn1KMPXGOGYQzjvshPH3DlZAzEGC8K', '홍회원14', '매일걷기', 40, 'M', '/images/profile14.png', 2, 'ACTIVE', '2024-01-14 00:00:00', '2024-03-14 00:00:00', '2024-03-13 19:00:00'),
('member15@eatnote.com', '$2a$10$K7u3K6Hf8yEaq1bJLC6neOxXn1KMPXGOGYQzjvshPH3DlZAzEGC8K', '홍회원15', '탄수화물러버', 25, 'M', '/images/profile15.png', 2, 'ACTIVE', '2024-01-15 00:00:00', '2024-03-15 00:00:00', '2024-03-14 19:00:00'),
('member16@eatnote.com', '$2a$10$K7u3K6Hf8yEaq1bJLC6neOxXn1KMPXGOGYQzjvshPH3DlZAzEGC8K', '홍회원16', '스트레칭중독', 34, 'M', '/images/profile16.png', 2, 'ACTIVE', '2024-01-16 00:00:00', '2024-03-16 00:00:00', '2024-03-15 19:00:00'),
('member17@eatnote.com', '$2a$10$K7u3K6Hf8yEaq1bJLC6neOxXn1KMPXGOGYQzjvshPH3DlZAzEGC8K', '홍회원17', '다이어트시작', 23, 'M', '/images/profile17.png', 2, 'ACTIVE', '2024-01-17 00:00:00', '2024-03-17 00:00:00', '2024-03-16 19:00:00'),
('member18@eatnote.com', '$2a$10$K7u3K6Hf8yEaq1bJLC6neOxXn1KMPXGOGYQzjvshPH3DlZAzEGC8K', '홍회원18', '바디프로필꿈나무', 34, 'F', '/images/profile18.png', 2, 'ACTIVE', '2024-01-18 00:00:00', '2024-03-18 00:00:00', '2024-03-17 19:00:00'),
('member19@eatnote.com', 'hashed_p$2a$10$K7u3K6Hf8yEaq1bJLC6neOxXn1KMPXGOGYQzjvshPH3DlZAzEGC8Kw_19', '홍회원19', '식단지킴이', 39, 'F', '/images/profile19.png', 2, 'ACTIVE', '2024-01-19 00:00:00', '2024-03-19 00:00:00', '2024-03-18 19:00:00'),
('member20@eatnote.com', '$2a$10$K7u3K6Hf8yEaq1bJLC6neOxXn1KMPXGOGYQzjvshPH3DlZAzEGC8K', '홍회원20', '운동인증러', 30, 'F', '/images/profile20.png', 2, 'ACTIVE', '2024-01-20 00:00:00', '2024-03-20 00:00:00', '2024-03-19 19:00:00'),
('member21@eatnote.com', '$2a$10$K7u3K6Hf8yEaq1bJLC6neOxXn1KMPXGOGYQzjvshPH3DlZAzEGC8K', '홍회원21', '물많이마셔요', 21, 'F', '/images/profile21.png', 2, 'ACTIVE', '2024-01-21 00:00:00', '2024-03-21 00:00:00', '2024-03-20 19:00:00'),
('member22@eatnote.com', '$2a$10$K7u3K6Hf8yEaq1bJLC6neOxXn1KMPXGOGYQzjvshPH3DlZAzEGC8K', '홍회원22', '조깅매니아', 33, 'M', '/images/profile22.png', 2, 'ACTIVE', '2024-01-22 00:00:00', '2024-03-22 00:00:00', '2024-03-21 19:00:00'),
('member23@eatnote.com', '$2a$10$K7u3K6Hf8yEaq1bJLC6neOxXn1KMPXGOGYQzjvshPH3DlZAzEGC8K', '홍회원23', '탄단지전사', 34, 'M', '/images/profile23.png', 2, 'ACTIVE', '2024-01-23 00:00:00', '2024-03-23 00:00:00', '2024-03-22 19:00:00'),
('member24@eatnote.com', '$2a$10$K7u3K6Hf8yEaq1bJLC6neOxXn1KMPXGOGYQzjvshPH3DlZAzEGC8K', '홍회원24', '유산소마스터', 34, 'M', '/images/profile24.png', 2, 'ACTIVE', '2024-01-24 00:00:00', '2024-03-24 00:00:00', '2024-03-23 19:00:00'),
('member25@eatnote.com', '$2a$10$K7u3K6Hf8yEaq1bJLC6neOxXn1KMPXGOGYQzjvshPH3DlZAzEGC8K', '홍회원25', '체중계와친구', 39, 'M', '/images/profile25.png', 2, 'ACTIVE', '2024-01-25 00:00:00', '2024-03-25 00:00:00', '2024-03-24 19:00:00'),
('member26@eatnote.com', '$2a$10$K7u3K6Hf8yEaq1bJLC6neOxXn1KMPXGOGYQzjvshPH3DlZAzEGC8K', '홍회원26', '매일기록러', 24, 'M', '/images/profile26.png', 2, 'ACTIVE', '2024-01-26 00:00:00', '2024-03-26 00:00:00', '2024-03-25 19:00:00'),
('member27@eatnote.com', '$2a$10$K7u3K6Hf8yEaq1bJLC6neOxXn1KMPXGOGYQzjvshPH3DlZAzEGC8K', '홍회원27', '헬창꿈나무', 27, 'M', '/images/profile27.png', 2, 'ACTIVE', '2024-01-27 00:00:00', '2024-03-27 00:00:00', '2024-03-26 19:00:00'),
('member28@eatnote.com', '$2a$10$K7u3K6Hf8yEaq1bJLC6neOxXn1KMPXGOGYQzjvshPH3DlZAzEGC8K', '홍회원28', '식단일기장', 38, 'F', '/images/profile28.png', 2, 'ACTIVE', '2024-01-28 00:00:00', '2024-03-28 00:00:00', '2024-03-27 19:00:00'),
('member29@eatnote.com', '$2a$10$K7u3K6Hf8yEaq1bJLC6neOxXn1KMPXGOGYQzjvshPH3DlZAzEGC8K', '홍회원29', '헬스장가는길', 31, 'M', '/images/profile29.png', 2, 'ACTIVE', '2024-01-29 00:00:00', '2024-03-29 00:00:00', '2024-03-28 19:00:00'),
('member30@eatnote.com', '$2a$10$K7u3K6Hf8yEaq1bJLC6neOxXn1KMPXGOGYQzjvshPH3DlZAzEGC8K', '홍회원30', '몸짱될거야', 28, 'M', '/images/profile30.png', 2, 'ACTIVE', '2024-01-30 00:00:00', '2024-03-30 00:00:00', '2024-03-29 19:00:00');

-- 트레이너
INSERT INTO users (email, password, name, nickname, age, gender, profile_image, user_type, status, created_at, updated_at, last_login) VALUES
('trainer1@eatnote.com', '$2a$10$K7u3K6Hf8yEaq1bJLC6neOxXn1KMPXGOGYQzjvshPH3DlZAzEGC8K', '김강민', '강철트레이너', 29, 'M', '/images/profile1.png', 1, 'ACTIVE', '2024-01-05 09:00:00', '2024-04-20 08:30:00', '2024-04-19 21:50:00'),
('trainer2@eatnote.com', '$2a$10$K7u3K6Hf8yEaq1bJLC6neOxXn1KMPXGOGYQzjvshPH3DlZAzEGC8K', '이서연', '핏여신서연', 26, 'F', '/images/profile2.png', 1, 'ACTIVE', '2024-02-10 10:30:00', '2024-04-20 08:35:00', '2024-04-19 20:45:00'),
('trainer3@eatnote.com', '$2a$10$K7u3K6Hf8yEaq1bJLC6neOxXn1KMPXGOGYQzjvshPH3DlZAzEGC8K', '박진우', '코어진우', 32, 'M', '/images/profile3.png', 1, 'ACTIVE', '2024-03-15 08:40:00', '2024-04-20 08:40:00', '2024-04-19 19:40:00'),
('trainer4@eatnote.com', '$2a$10$K7u3K6Hf8yEaq1bJLC6neOxXn1KMPXGOGYQzjvshPH3DlZAzEGC8K', '최민지', '민지노핏', 30, 'F', '/images/profile4.png', 1, 'ACTIVE', '2024-04-01 09:15:00', '2024-04-20 08:45:00', '2024-04-19 18:30:00'),
('trainer5@eatnote.com', '$2a$10$K7u3K6Hf8yEaq1bJLC6neOxXn1KMPXGOGYQzjvshPH3DlZAzEGC8K', '정우성', '운동하는정쌤', 35, 'M', '/images/profile5.png', 1, 'ACTIVE', '2024-04-05 07:20:00', '2024-04-20 08:50:00', '2024-04-19 17:20:00'),
('trainer6@eatnote.com', '$2a$10$K7u3K6Hf8yEaq1bJLC6neOxXn1KMPXGOGYQzjvshPH3DlZAzEGC8K', '한유리', '트레이너유리', 28, 'F', '/images/profile6.png', 1, 'ACTIVE', '2024-04-10 12:00:00', '2024-04-20 08:55:00', '2024-04-19 16:10:00'),
('trainer7@eatnote.com', '$2a$10$K7u3K6Hf8yEaq1bJLC6neOxXn1KMPXGOGYQzjvshPH3DlZAzEGC8K', '윤도현', '윤핏', 31, 'M', '/images/profile7.png', 1, 'ACTIVE', '2024-04-13 11:00:00', '2024-04-20 09:00:00', '2024-04-19 15:05:00'),
('trainer8@eatnote.com', '$2a$10$K7u3K6Hf8yEaq1bJLC6neOxXn1KMPXGOGYQzjvshPH3DlZAzEGC8K', '장하늘', '하늘트레이너', 27, 'F', '/images/profile8.png', 1, 'ACTIVE', '2024-04-15 14:10:00', '2024-04-20 09:05:00', '2024-04-19 14:55:00'),
('trainer9@eatnote.com', '$2a$10$K7u3K6Hf8yEaq1bJLC6neOxXn1KMPXGOGYQzjvshPH3DlZAzEGC8K', '오상민', '상민코치', 33, 'M', '/images/profile9.png', 1, 'ACTIVE', '2024-04-17 15:20:00', '2024-04-20 09:10:00', '2024-04-19 13:50:00'),
('trainer10@eatnote.com', '$2a$10$K7u3K6Hf8yEaq1bJLC6neOxXn1KMPXGOGYQzjvshPH3DlZAzEGC8K', '서지현', '지현쌤', 25, 'F', '/images/profile10.png', 1, 'ACTIVE', '2024-04-18 16:30:00', '2024-04-20 09:15:00', '2024-04-19 12:40:00');

-- 관리자 
INSERT INTO users (email, password, name, nickname, age, gender, user_type, status)
VALUES 
('admin@eatnote.com', 'hashed_pw_1', '관리자', '관리자', 24, 'M', 0, 'ACTIVE');


-- ---------------------------------------------------------------------
-- `member_details` <<< ENUM 정보
-- ---------------------------------------------------------------------

CREATE TABLE goal_type (
    goal_code VARCHAR(20) PRIMARY KEY,
    description VARCHAR(100)
);

INSERT INTO goal_type VALUES
('감량', '체중 감량'),
('증량', '근육 증가'),
('유지', '체중 유지'),
('체력향상', '체력 향상');


-- ---------------------------------------------------------------------
-- Table `member_details` <<< 회원 추가 정보
-- ---------------------------------------------------------------------
CREATE TABLE member_details (
    user_id             BIGINT UNSIGNED PRIMARY KEY, -- 회원 ID (users 테이블 참조)
    height              FLOAT,                       -- 키 (cm)
    weight              FLOAT,                       -- 체중 (kg)
    body_water          FLOAT,                       -- 체수분
    protein             FLOAT,                       -- 단백질량
    mineral             FLOAT,                       -- 무기질
    body_fat            FLOAT,                       -- 체지방
    skeletal_muscle     FLOAT,                       -- 골격근량
    body_fat_mass       FLOAT,                       -- 체지방량
    bmi                 FLOAT,                       -- BMI 지수
    body_fat_percentage FLOAT,                       -- 체지방률
    goal                VARCHAR(20),                 -- 목표 (감량, 증량 등)
    trainer_id          BIGINT UNSIGNED,             -- 연결된 트레이너 ID (users 테이블 참조)
													 -- 백엔드에서 user_type = 1인지 검증 로직 필요!

    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (goal) REFERENCES goal_type(goal_code),
    FOREIGN KEY (trainer_id) REFERENCES users(user_id)
        ON DELETE SET NULL
        ON UPDATE CASCADE
);

INSERT INTO member_details (user_id, height, weight, body_water, protein, mineral, body_fat, skeletal_muscle, body_fat_mass, bmi, body_fat_percentage, goal, trainer_id) VALUES
(1, 156.3, 79.2, 48.9, 7.9, 2.9, 16.9, 35.4, 11.0, 32.4, 13.9, '증량', 5),
(2, 160.7, 70.0, 44.9, 9.5, 4.8, 17.1, 39.3, 22.9, 27.1, 32.7, '감량', 1),
(3, 167.8, 89.2, 34.6, 9.5, 4.5, 12.7, 23.4, 17.1, 31.7, 19.2, '유지', 5),
(4, 153.4, 60.8, 48.1, 9.3, 2.5, 11.4, 26.6, 18.2, 25.8, 29.9, '증량', 7),
(5, 170.0, 62.8, 30.9, 9.0, 4.0, 22.3, 27.4, 17.5, 21.7, 27.9, '유지', 1),
(6, 150.5, 63.8, 31.4, 11.8, 2.4, 12.5, 21.9, 17.7, 28.2, 27.7, '감량', 4),
(7, 157.5, 75.1, 45.2, 10.4, 3.0, 23.3, 39.6, 14.1, 30.3, 18.8, '감량', 3),
(8, 182.3, 81.4, 37.5, 7.8, 2.8, 18.6, 36.3, 22.7, 24.5, 27.9, '증량', 5),
(9, 170.1, 56.6, 30.2, 10.4, 4.9, 22.3, 23.8, 22.2, 19.6, 39.2, '감량', 7),
(10, 167.9, 58.7, 40.2, 14.7, 4.6, 10.8, 21.7, 24.3, 20.8, 41.4, '유지', 10),
(11, 166.1, 55.0, 36.6, 8.2, 2.6, 28.0, 23.0, 12.0, 19.9, 21.8, '증량', 6),
(12, 170.6, 53.2, 49.9, 7.6, 3.9, 11.6, 32.3, 13.5, 18.3, 25.4, '증량', 4),
(13, 164.8, 83.1, 42.1, 9.5, 3.9, 28.6, 34.2, 12.8, 30.6, 15.4, '유지', 7),
(14, 181.0, 71.2, 49.5, 10.8, 4.0, 12.5, 28.8, 23.5, 21.7, 33.0, '유지', 4),
(15, 156.7, 48.6, 35.1, 13.4, 2.8, 20.9, 24.3, 11.1, 19.8, 22.8, '증량', 5),
(16, 150.3, 49.4, 31.9, 9.7, 3.5, 26.4, 33.3, 10.4, 21.9, 21.1, '유지', 2),
(17, 155.2, 78.2, 33.2, 13.5, 4.5, 10.7, 26.3, 15.2, 32.5, 19.4, '감량', 8),
(18, 176.4, 83.1, 30.6, 14.2, 4.4, 14.0, 20.3, 20.2, 26.7, 24.3, '감량', 4),
(19, 168.2, 76.9, 32.2, 7.4, 3.6, 10.6, 36.6, 12.7, 27.2, 16.5, '감량', 3),
(20, 183.9, 60.0, 35.7, 9.6, 4.6, 19.0, 29.2, 15.8, 17.7, 26.3, '유지', 7),
(21, 175.6, 79.6, 33.7, 11.9, 4.3, 26.5, 29.2, 17.3, 25.8, 21.7, '유지', 10),
(22, 166.9, 81.3, 33.1, 7.4, 2.2, 22.6, 28.2, 18.3, 29.2, 22.5, '감량', 5),
(23, 184.8, 59.5, 45.1, 10.8, 2.6, 15.0, 20.2, 17.9, 17.4, 30.1, '증량', 3),
(24, 175.3, 83.3, 46.9, 12.8, 4.4, 27.4, 36.1, 16.1, 27.1, 19.3, '증량', 4),
(25, 164.0, 87.4, 49.1, 8.5, 4.7, 28.9, 23.7, 22.6, 32.5, 25.9, '유지', 8),
(26, 165.7, 65.5, 30.9, 14.1, 4.0, 18.2, 24.3, 11.1, 23.9, 16.9, '유지', 9),
(27, 151.3, 83.9, 41.0, 7.1, 3.5, 23.3, 20.1, 13.7, 36.7, 16.3, '증량', 3),
(28, 171.4, 52.1, 48.6, 13.4, 4.2, 19.4, 24.6, 23.6, 17.7, 45.3, '증량', 7),
(29, 181.5, 89.1, 38.6, 13.4, 3.3, 24.0, 32.2, 17.0, 27.0, 19.1, '유지', 8),
(30, 168.6, 49.8, 37.2, 13.3, 2.5, 11.3, 31.4, 15.6, 17.5, 31.3, '감량', 4);





-- ---------------------------------------------------------------------
-- Table `trainer_details` <<< 트레이너 추가 정보
-- ---------------------------------------------------------------------
CREATE TABLE trainer_details (
    user_id              BIGINT UNSIGNED PRIMARY KEY,
    phone                VARCHAR(20),
    address              VARCHAR(200),
    gym_name             VARCHAR(100),
    gym_website          VARCHAR(200),
    certification_number VARCHAR(100),
    certification_image  VARCHAR(300),
    business_number      VARCHAR(50),
    introduction         TEXT,
    career               TEXT,
    instagram_url        VARCHAR(200),
    is_verified          BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (user_id) REFERENCES users(user_id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);

INSERT INTO trainer_details (user_id, phone, address, gym_name, gym_website, certification_number, certification_image, business_number, introduction, career, instagram_url, is_verified) VALUES
(31, '010-1234-1200', '서울시 강남구 역삼동 100', '피트니스존', 'https://피트니스존.com', 'CERT-2024-1000', '/certs/trainer1.jpg', '111-22-33000', '안녕하세요. 피트니스존에서 활동 중인 전문 트레이너입니다.', '7년 경력의 트레이너로 다양한 회원을 지도해왔습니다.', 'https://instagram.com/trainer1', TRUE),
(32, '010-1234-1201', '서울시 강남구 역삼동 101', '헬스킹짐', 'https://헬스킹짐.com', 'CERT-2024-1001', '/certs/trainer2.jpg', '111-22-33001', '안녕하세요. 헬스킹짐에서 활동 중인 전문 트레이너입니다.', '6년 경력의 트레이너로 다양한 회원을 지도해왔습니다.', 'https://instagram.com/trainer2', FALSE),
(33, '010-1234-1202', '서울시 강남구 역삼동 102', '바디업센터', 'https://바디업센터.com', 'CERT-2024-1002', '/certs/trainer3.jpg', '111-22-33002', '안녕하세요. 바디업센터에서 활동 중인 전문 트레이너입니다.', '3년 경력의 트레이너로 다양한 회원을 지도해왔습니다.', 'https://instagram.com/trainer3', TRUE),
(34, '010-1234-1203', '서울시 강남구 역삼동 103', '코어짐', 'https://코어짐.com', 'CERT-2024-1003', '/certs/trainer4.jpg', '111-22-33003', '안녕하세요. 코어짐에서 활동 중인 전문 트레이너입니다.', '7년 경력의 트레이너로 다양한 회원을 지도해왔습니다.', 'https://instagram.com/trainer4', FALSE),
(35, '010-1234-1204', '서울시 강남구 역삼동 104', '무브핏', 'https://무브핏.com', 'CERT-2024-1004', '/certs/trainer5.jpg', '111-22-33004', '안녕하세요. 무브핏에서 활동 중인 전문 트레이너입니다.', '7년 경력의 트레이너로 다양한 회원을 지도해왔습니다.', 'https://instagram.com/trainer5', TRUE),
(36, '010-1234-1205', '서울시 강남구 역삼동 105', '바디챌린지', 'https://바디챌린지.com', 'CERT-2024-1005', '/certs/trainer6.jpg', '111-22-33005', '안녕하세요. 바디챌린지에서 활동 중인 전문 트레이너입니다.', '6년 경력의 트레이너로 다양한 회원을 지도해왔습니다.', 'https://instagram.com/trainer6', FALSE),
(37, '010-1234-1206', '서울시 강남구 역삼동 106', '에너지짐', 'https://에너지짐.com', 'CERT-2024-1006', '/certs/trainer7.jpg', '111-22-33006', '안녕하세요. 에너지짐에서 활동 중인 전문 트레이너입니다.', '3년 경력의 트레이너로 다양한 회원을 지도해왔습니다.', 'https://instagram.com/trainer7', TRUE),
(38, '010-1234-1207', '서울시 강남구 역삼동 107', '파워짐', 'https://파워짐.com', 'CERT-2024-1007', '/certs/trainer8.jpg', '111-22-33007', '안녕하세요. 파워짐에서 활동 중인 전문 트레이너입니다.', '4년 경력의 트레이너로 다양한 회원을 지도해왔습니다.', 'https://instagram.com/trainer8', FALSE),
(39, '010-1234-1208', '서울시 강남구 역삼동 108', '다이나믹짐', 'https://다이나믹짐.com', 'CERT-2024-1008', '/certs/trainer9.jpg', '111-22-33008', '안녕하세요. 다이나믹짐에서 활동 중인 전문 트레이너입니다.', '9년 경력의 트레이너로 다양한 회원을 지도해왔습니다.', 'https://instagram.com/trainer9', TRUE),
(40, '010-1234-1209', '서울시 강남구 역삼동 109', '핏밸런스', 'https://핏밸런스.com', 'CERT-2024-1009', '/certs/trainer10.jpg', '111-22-33009', '안녕하세요. 핏밸런스에서 활동 중인 전문 트레이너입니다.', '3년 경력의 트레이너로 다양한 회원을 지도해왔습니다.', 'https://instagram.com/trainer10', FALSE);



-- ---------------------------------------------------------------------
-- `user_follow` <<< ENUM 정보
-- ---------------------------------------------------------------------


CREATE TABLE follow_status (
    status_code   VARCHAR(20) PRIMARY KEY,       -- 'PENDING', 'ACCEPTED', 'REJECTED'
    description   VARCHAR(100) NOT NULL          -- '요청중', '수락됨', '거절됨'
);

INSERT INTO follow_status VALUES
('PENDING', '요청중'),
('ACCEPTED', '수락됨'),
('REJECTED', '거절됨');


-- ---------------------------------------------------------------------
-- Table `user_follow` <<< 팔로우 기능
-- ---------------------------------------------------------------------
CREATE TABLE user_follow (
    follow_id     BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    from_user_id  BIGINT UNSIGNED NOT NULL,  -- 팔로우 요청자
    to_user_id    BIGINT UNSIGNED NOT NULL,  -- 팔로우 대상
    status        VARCHAR(20) DEFAULT 'PENDING',-- follow_status 참조 (PENDING, ACCEPTED, REJECTED)
    requested_at  DATETIME DEFAULT CURRENT_TIMESTAMP, -- 팔로우 요청이 발생한 시간을 자동 저장
    responded_at  DATETIME DEFAULT NULL, -- 요청을 수락 또는 거절한 시점을 저장

    FOREIGN KEY (from_user_id) REFERENCES users(user_id) ON DELETE CASCADE, -- 요청한 사용자가 탈퇴하면 → 해당 팔로우 요청도 함께 삭제
    FOREIGN KEY (to_user_id) REFERENCES users(user_id) ON DELETE CASCADE, -- 받은 사람이 탈퇴하면 → 요청도 같이 삭제
	FOREIGN KEY (status)       REFERENCES follow_status(status_code),

    UNIQUE (from_user_id, to_user_id) -- 중복 팔로우 방지
);


INSERT INTO user_follow (from_user_id, to_user_id, status, requested_at, responded_at)
VALUES 
(1, 33, 'PENDING', '2024-04-01 12:00:00', NULL),
(1, 35, 'PENDING', '2024-04-01 12:00:00', NULL),
(1, 38, 'ACCEPTED', '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
(1, 34, 'PENDING', '2024-04-01 12:00:00', NULL),
(1, 37, 'ACCEPTED', '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
(1, 17, 'PENDING', '2024-04-01 12:00:00', NULL),
(1, 29, 'ACCEPTED', '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
(1, 10, 'ACCEPTED', '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
(1, 4, 'PENDING', '2024-04-01 12:00:00', NULL),
(1, 11, 'ACCEPTED', '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
(2, 38, 'PENDING', '2024-04-01 12:00:00', NULL),
(2, 36, 'PENDING', '2024-04-01 12:00:00', NULL),
(2, 35, 'ACCEPTED', '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
(2, 31, 'ACCEPTED', '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
(2, 37, 'PENDING', '2024-04-01 12:00:00', NULL),
(2, 1, 'PENDING', '2024-04-01 12:00:00', NULL),
(2, 13, 'ACCEPTED', '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
(2, 9, 'PENDING', '2024-04-01 12:00:00', NULL),
(2, 29, 'ACCEPTED', '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
(2, 5, 'PENDING', '2024-04-01 12:00:00', NULL),
(3, 36, 'ACCEPTED', '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
(3, 33, 'PENDING', '2024-04-01 12:00:00', NULL),
(3, 39, 'ACCEPTED', '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
(3, 37, 'PENDING', '2024-04-01 12:00:00', NULL),
(3, 34, 'PENDING', '2024-04-01 12:00:00', NULL),
(3, 2, 'PENDING', '2024-04-01 12:00:00', NULL),
(3, 10, 'PENDING', '2024-04-01 12:00:00', NULL),
(3, 26, 'ACCEPTED', '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
(3, 24, 'PENDING', '2024-04-01 12:00:00', NULL),
(3, 21, 'ACCEPTED', '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
(4, 39, 'PENDING', '2024-04-01 12:00:00', NULL),
(4, 31, 'PENDING', '2024-04-01 12:00:00', NULL),
(4, 37, 'ACCEPTED', '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
(4, 36, 'ACCEPTED', '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
(4, 33, 'ACCEPTED', '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
(4, 30, 'ACCEPTED', '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
(4, 7, 'PENDING', '2024-04-01 12:00:00', NULL),
(4, 8, 'ACCEPTED', '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
(4, 20, 'PENDING', '2024-04-01 12:00:00', NULL),
(4, 1, 'PENDING', '2024-04-01 12:00:00', NULL),
(5, 33, 'ACCEPTED', '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
(5, 39, 'PENDING', '2024-04-01 12:00:00', NULL),
(5, 40, 'PENDING', '2024-04-01 12:00:00', NULL),
(5, 32, 'PENDING', '2024-04-01 12:00:00', NULL),
(5, 34, 'PENDING', '2024-04-01 12:00:00', NULL),
(5, 15, 'ACCEPTED', '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
(5, 20, 'PENDING', '2024-04-01 12:00:00', NULL),
(5, 29, 'PENDING', '2024-04-01 12:00:00', NULL),
(5, 1, 'ACCEPTED', '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
(5, 3, 'ACCEPTED', '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
(6, 8, 'ACCEPTED', '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
(6, 12, 'PENDING', '2024-04-01 12:00:00', NULL),
(6, 13, 'ACCEPTED', '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
(6, 16, 'PENDING', '2024-04-01 12:00:00', NULL),
(6, 25, 'ACCEPTED', '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
(6, 38, 'PENDING', '2024-04-01 12:00:00', NULL),
(6, 33, 'ACCEPTED', '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
(6, 40, 'PENDING', '2024-04-01 12:00:00', NULL),
(6, 39, 'ACCEPTED', '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
(6, 32, 'ACCEPTED', '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
(7, 33, 'ACCEPTED', '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
(7, 32, 'ACCEPTED', '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
(7, 35, 'ACCEPTED', '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
(7, 39, 'ACCEPTED', '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
(7, 31, 'ACCEPTED', '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
(7, 29, 'PENDING', '2024-04-01 12:00:00', NULL),
(7, 6, 'PENDING', '2024-04-01 12:00:00', NULL),
(7, 19, 'PENDING', '2024-04-01 12:00:00', NULL),
(7, 2, 'PENDING', '2024-04-01 12:00:00', NULL),
(7, 14, 'ACCEPTED', '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
(8, 32, 'ACCEPTED', '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
(8, 40, 'ACCEPTED', '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
(8, 33, 'PENDING', '2024-04-01 12:00:00', NULL),
(8, 34, 'PENDING', '2024-04-01 12:00:00', NULL),
(8, 39, 'ACCEPTED', '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
(8, 19, 'PENDING', '2024-04-01 12:00:00', NULL),
(8, 15, 'PENDING', '2024-04-01 12:00:00', NULL),
(8, 29, 'PENDING', '2024-04-01 12:00:00', NULL),
(8, 22, 'ACCEPTED', '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
(8, 3, 'ACCEPTED', '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
(9, 32, 'PENDING', '2024-04-01 12:00:00', NULL),
(9, 34, 'PENDING', '2024-04-01 12:00:00', NULL),
(9, 33, 'ACCEPTED', '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
(9, 31, 'PENDING', '2024-04-01 12:00:00', NULL),
(9, 39, 'PENDING', '2024-04-01 12:00:00', NULL),
(9, 29, 'PENDING', '2024-04-01 12:00:00', NULL),
(9, 19, 'ACCEPTED', '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
(9, 26, 'PENDING', '2024-04-01 12:00:00', NULL),
(9, 2, 'PENDING', '2024-04-01 12:00:00', NULL),
(9, 5, 'PENDING', '2024-04-01 12:00:00', NULL),
(10, 35, 'ACCEPTED', '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
(10, 32, 'PENDING', '2024-04-01 12:00:00', NULL),
(10, 37, 'ACCEPTED', '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
(10, 34, 'ACCEPTED', '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
(10, 31, 'PENDING', '2024-04-01 12:00:00', NULL),
(10, 27, 'PENDING', '2024-04-01 12:00:00', NULL),
(10, 9, 'PENDING', '2024-04-01 12:00:00', NULL),
(10, 12, 'PENDING', '2024-04-01 12:00:00', NULL),
(10, 11, 'PENDING', '2024-04-01 12:00:00', NULL),
(10, 15, 'PENDING', '2024-04-01 12:00:00', NULL),
(11, 37, 'PENDING', '2024-04-01 12:00:00', NULL),
(11, 33, 'ACCEPTED', '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
(11, 38, 'PENDING', '2024-04-01 12:00:00', NULL),
(11, 40, 'PENDING', '2024-04-01 12:00:00', NULL),
(11, 39, 'PENDING', '2024-04-01 12:00:00', NULL),
(11, 22, 'ACCEPTED', '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
(11, 28, 'PENDING', '2024-04-01 12:00:00', NULL),
(11, 7, 'ACCEPTED', '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
(11, 3, 'PENDING', '2024-04-01 12:00:00', NULL),
(11, 15, 'PENDING', '2024-04-01 12:00:00', NULL),
(12, 39, 'ACCEPTED', '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
(12, 31, 'PENDING', '2024-04-01 12:00:00', NULL),
(12, 40, 'PENDING', '2024-04-01 12:00:00', NULL),
(12, 38, 'PENDING', '2024-04-01 12:00:00', NULL),
(12, 34, 'ACCEPTED', '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
(12, 1, 'ACCEPTED', '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
(12, 29, 'PENDING', '2024-04-01 12:00:00', NULL),
(12, 23, 'ACCEPTED', '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
(12, 8, 'ACCEPTED', '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
(12, 27, 'PENDING', '2024-04-01 12:00:00', NULL),
(13, 31, 'ACCEPTED', '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
(13, 32, 'PENDING', '2024-04-01 12:00:00', NULL),
(13, 40, 'ACCEPTED', '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
(13, 34, 'PENDING', '2024-04-01 12:00:00', NULL),
(13, 36, 'ACCEPTED', '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
(13, 4, 'ACCEPTED', '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
(13, 24, 'PENDING', '2024-04-01 12:00:00', NULL),
(13, 6, 'ACCEPTED', '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
(13, 14, 'PENDING', '2024-04-01 12:00:00', NULL),
(13, 20, 'ACCEPTED', '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
(14, 38, 'PENDING', '2024-04-01 12:00:00', NULL),
(14, 36, 'PENDING', '2024-04-01 12:00:00', NULL),
(14, 32, 'PENDING', '2024-04-01 12:00:00', NULL),
(14, 31, 'PENDING', '2024-04-01 12:00:00', NULL),
(14, 33, 'ACCEPTED', '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
(14, 20, 'ACCEPTED', '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
(14, 25, 'PENDING', '2024-04-01 12:00:00', NULL),
(14, 19, 'PENDING', '2024-04-01 12:00:00', NULL),
(14, 1, 'ACCEPTED', '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
(14, 27, 'ACCEPTED', '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
(15, 35, 'ACCEPTED', '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
(15, 31, 'PENDING', '2024-04-01 12:00:00', NULL),
(15, 32, 'PENDING', '2024-04-01 12:00:00', NULL),
(15, 33, 'ACCEPTED', '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
(15, 37, 'PENDING', '2024-04-01 12:00:00', NULL),
(15, 16, 'PENDING', '2024-04-01 12:00:00', NULL),
(15, 28, 'PENDING', '2024-04-01 12:00:00', NULL),
(15, 12, 'PENDING', '2024-04-01 12:00:00', NULL),
(15, 26, 'ACCEPTED', '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
(15, 20, 'ACCEPTED', '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
(16, 36, 'ACCEPTED', '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
(16, 37, 'ACCEPTED', '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
(16, 39, 'ACCEPTED', '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
(16, 33, 'ACCEPTED', '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
(16, 40, 'PENDING', '2024-04-01 12:00:00', NULL),
(16, 13, 'PENDING', '2024-04-01 12:00:00', NULL),
(16, 24, 'ACCEPTED', '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
(16, 1, 'ACCEPTED', '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
(16, 25, 'PENDING', '2024-04-01 12:00:00', NULL),
(16, 8, 'ACCEPTED', '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
(17, 33, 'ACCEPTED', '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
(17, 40, 'PENDING', '2024-04-01 12:00:00', NULL),
(17, 35, 'PENDING', '2024-04-01 12:00:00', NULL),
(17, 31, 'PENDING', '2024-04-01 12:00:00', NULL),
(17, 34, 'PENDING', '2024-04-01 12:00:00', NULL),
(17, 30, 'ACCEPTED', '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
(17, 24, 'ACCEPTED', '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
(17, 12, 'PENDING', '2024-04-01 12:00:00', NULL),
(17, 26, 'PENDING', '2024-04-01 12:00:00', NULL),
(17, 22, 'ACCEPTED', '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
(18, 39, 'ACCEPTED', '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
(18, 40, 'ACCEPTED', '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
(18, 38, 'PENDING', '2024-04-01 12:00:00', NULL),
(18, 31, 'PENDING', '2024-04-01 12:00:00', NULL),
(18, 32, 'PENDING', '2024-04-01 12:00:00', NULL),
(18, 7, 'PENDING', '2024-04-01 12:00:00', NULL),
(18, 28, 'PENDING', '2024-04-01 12:00:00', NULL),
(18, 26, 'PENDING', '2024-04-01 12:00:00', NULL),
(18, 29, 'ACCEPTED', '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
(18, 14, 'PENDING', '2024-04-01 12:00:00', NULL),
(19, 32, 'ACCEPTED', '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
(19, 36, 'ACCEPTED', '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
(19, 39, 'ACCEPTED', '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
(19, 35, 'ACCEPTED', '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
(19, 40, 'ACCEPTED', '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
(19, 9, 'ACCEPTED', '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
(19, 30, 'PENDING', '2024-04-01 12:00:00', NULL),
(19, 26, 'PENDING', '2024-04-01 12:00:00', NULL),
(19, 7, 'PENDING', '2024-04-01 12:00:00', NULL),
(19, 3, 'PENDING', '2024-04-01 12:00:00', NULL),
(20, 34, 'PENDING', '2024-04-01 12:00:00', NULL),
(20, 36, 'ACCEPTED', '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
(20, 38, 'PENDING', '2024-04-01 12:00:00', NULL),
(20, 35, 'PENDING', '2024-04-01 12:00:00', NULL),
(20, 33, 'ACCEPTED', '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
(20, 23, 'ACCEPTED', '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
(20, 17, 'ACCEPTED', '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
(20, 19, 'PENDING', '2024-04-01 12:00:00', NULL),
(20, 25, 'ACCEPTED', '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
(20, 14, 'PENDING', '2024-04-01 12:00:00', NULL),
(21, 31, 'ACCEPTED', '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
(21, 40, 'ACCEPTED', '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
(21, 32, 'ACCEPTED', '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
(21, 38, 'PENDING', '2024-04-01 12:00:00', NULL),
(21, 33, 'ACCEPTED', '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
(21, 24, 'PENDING', '2024-04-01 12:00:00', NULL),
(21, 27, 'PENDING', '2024-04-01 12:00:00', NULL),
(21, 18, 'PENDING', '2024-04-01 12:00:00', NULL),
(21, 23, 'PENDING', '2024-04-01 12:00:00', NULL),
(21, 28, 'PENDING', '2024-04-01 12:00:00', NULL),
(22, 36, 'PENDING', '2024-04-01 12:00:00', NULL),
(22, 32, 'PENDING', '2024-04-01 12:00:00', NULL),
(22, 38, 'ACCEPTED', '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
(22, 37, 'ACCEPTED', '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
(22, 40, 'ACCEPTED', '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
(22, 21, 'PENDING', '2024-04-01 12:00:00', NULL),
(22, 26, 'PENDING', '2024-04-01 12:00:00', NULL),
(22, 11, 'PENDING', '2024-04-01 12:00:00', NULL),
(22, 19, 'PENDING', '2024-04-01 12:00:00', NULL),
(22, 13, 'ACCEPTED', '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
(23, 31, 'PENDING', '2024-04-01 12:00:00', NULL),
(23, 35, 'ACCEPTED', '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
(23, 34, 'PENDING', '2024-04-01 12:00:00', NULL),
(23, 38, 'ACCEPTED', '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
(23, 36, 'ACCEPTED', '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
(23, 4, 'PENDING', '2024-04-01 12:00:00', NULL),
(23, 17, 'PENDING', '2024-04-01 12:00:00', NULL),
(23, 24, 'ACCEPTED', '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
(23, 25, 'PENDING', '2024-04-01 12:00:00', NULL),
(23, 12, 'ACCEPTED', '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
(24, 40, 'PENDING', '2024-04-01 12:00:00', NULL),
(24, 35, 'PENDING', '2024-04-01 12:00:00', NULL),
(24, 34, 'PENDING', '2024-04-01 12:00:00', NULL),
(24, 39, 'ACCEPTED', '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
(24, 32, 'ACCEPTED', '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
(24, 6, 'PENDING', '2024-04-01 12:00:00', NULL),
(24, 2, 'ACCEPTED', '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
(24, 27, 'PENDING', '2024-04-01 12:00:00', NULL),
(24, 26, 'PENDING', '2024-04-01 12:00:00', NULL),
(24, 1, 'ACCEPTED', '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
(25, 36, 'PENDING', '2024-04-01 12:00:00', NULL),
(25, 39, 'PENDING', '2024-04-01 12:00:00', NULL),
(25, 32, 'ACCEPTED', '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
(25, 33, 'PENDING', '2024-04-01 12:00:00', NULL),
(25, 37, 'ACCEPTED', '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
(25, 1, 'ACCEPTED', '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
(25, 10, 'PENDING', '2024-04-01 12:00:00', NULL),
(25, 16, 'PENDING', '2024-04-01 12:00:00', NULL),
(25, 13, 'ACCEPTED', '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
(25, 6, 'PENDING', '2024-04-01 12:00:00', NULL),
(26, 36, 'PENDING', '2024-04-01 12:00:00', NULL),
(26, 39, 'PENDING', '2024-04-01 12:00:00', NULL),
(26, 35, 'PENDING', '2024-04-01 12:00:00', NULL),
(26, 40, 'PENDING', '2024-04-01 12:00:00', NULL),
(26, 37, 'ACCEPTED', '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
(26, 18, 'ACCEPTED', '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
(26, 22, 'ACCEPTED', '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
(26, 27, 'ACCEPTED', '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
(26, 21, 'PENDING', '2024-04-01 12:00:00', NULL),
(26, 12, 'PENDING', '2024-04-01 12:00:00', NULL),
(27, 39, 'PENDING', '2024-04-01 12:00:00', NULL),
(27, 36, 'ACCEPTED', '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
(27, 32, 'ACCEPTED', '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
(27, 35, 'PENDING', '2024-04-01 12:00:00', NULL),
(27, 38, 'PENDING', '2024-04-01 12:00:00', NULL),
(27, 20, 'PENDING', '2024-04-01 12:00:00', NULL),
(27, 14, 'PENDING', '2024-04-01 12:00:00', NULL),
(27, 3, 'PENDING', '2024-04-01 12:00:00', NULL),
(27, 12, 'PENDING', '2024-04-01 12:00:00', NULL),
(27, 22, 'ACCEPTED', '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
(28, 34, 'ACCEPTED', '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
(28, 31, 'PENDING', '2024-04-01 12:00:00', NULL),
(28, 37, 'ACCEPTED', '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
(28, 32, 'PENDING', '2024-04-01 12:00:00', NULL),
(28, 35, 'PENDING', '2024-04-01 12:00:00', NULL),
(28, 23, 'PENDING', '2024-04-01 12:00:00', NULL),
(28, 29, 'PENDING', '2024-04-01 12:00:00', NULL),
(28, 30, 'PENDING', '2024-04-01 12:00:00', NULL),
(28, 21, 'ACCEPTED', '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
(28, 19, 'PENDING', '2024-04-01 12:00:00', NULL),
(29, 40, 'ACCEPTED', '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
(29, 39, 'PENDING', '2024-04-01 12:00:00', NULL),
(29, 31, 'ACCEPTED', '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
(29, 36, 'ACCEPTED', '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
(29, 33, 'PENDING', '2024-04-01 12:00:00', NULL),
(29, 23, 'PENDING', '2024-04-01 12:00:00', NULL),
(29, 4, 'ACCEPTED', '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
(29, 5, 'ACCEPTED', '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
(29, 22, 'PENDING', '2024-04-01 12:00:00', NULL),
(29, 28, 'PENDING', '2024-04-01 12:00:00', NULL),
(30, 38, 'ACCEPTED', '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
(30, 39, 'ACCEPTED', '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
(30, 36, 'PENDING', '2024-04-01 12:00:00', NULL),
(30, 32, 'ACCEPTED', '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
(30, 35, 'ACCEPTED', '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
(30, 27, 'PENDING', '2024-04-01 12:00:00', NULL),
(30, 18, 'PENDING', '2024-04-01 12:00:00', NULL),
(30, 4, 'ACCEPTED', '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
(30, 9, 'ACCEPTED', '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
(30, 19, 'ACCEPTED', '2024-04-01 12:00:00', '2024-04-01 12:00:00');





-- ---------------------------------------------------------------------
-- 백엔드 로직 설계 --

-- 트레이너 ↔ 트레이너 ❌
-- 제약 조건을 DB에서 처리하기 어렵기 때문에, 백엔드에서 다음 로직을 반드시 넣어야 함
-- // 팔로우 요청 시 제한 조건
-- if (fromUserType == 1 && toUserType == 1) {
--     throw new IllegalArgumentException("트레이너끼리는 팔로우할 수 없습니다.");
-- }


-- 자동 팔로우 연결 (회원가입 시)
-- 회원가입 시 트레이너 아이디 입력 → user_follow 테이블에 다음과 같이 자동 INSERT:
-- from: 일반회원, to: 트레이너
-- INSERT INTO user_follow (from_user_id, to_user_id, status, requested_at, responded_at)
-- VALUES (?, ?, 'ACCEPTED', NOW(), NOW());

-- 트레이너는 누가 팔로우했는지 확인 가능
-- 나(트레이너)를 팔로우한 회원 리스트
-- SELECT u.user_id, u.nickname
-- FROM user_follow f
-- JOIN users u ON f.from_user_id = u.user_id
-- WHERE f.to_user_id = :trainerId AND f.status = 'ACCEPTED';

-- 트레이너 알림 로직 (회원 식단 업로드 시)
-- 식단 업로드 시 → user_follow 테이블에서 해당 회원을 팔로우하고 있는 트레이너 검색
-- 트레이너에게 알림 전송 (DB 저장 or FCM 등 활용)
-- SELECT to_user_id AS trainer_id
-- FROM user_follow
-- WHERE from_user_id = :memberId AND status = 'ACCEPTED'
-- AND to_user_id IN (SELECT user_id FROM users WHERE user_type = 1);

-- 일반회원은 팔로우한 회원의 식단 열람 가능
-- SELECT m.*
-- FROM meal m
-- JOIN user_follow f ON m.user_id = f.to_user_id
-- WHERE f.from_user_id = :myUserId AND f.status = 'ACCEPTED';


-- ---------------------------------------------------------------------









-- ---------------------------------------------------------------------
-- Table `allergy` <<< 알레르기 정보
-- ---------------------------------------------------------------------
CREATE TABLE allergy (
    allergy_id      INT AUTO_INCREMENT PRIMARY KEY,
    name            VARCHAR(100) NOT NULL,  -- 땅콩, 새우, 유당 불내성 등
    category        VARCHAR(50) NOT NULL  -- 예: 견과류, 해산물, 곡물 등
);

INSERT INTO allergy (name, category) VALUES
('땅콩 알레르기', '견과류'),
('호두 알레르기', '견과류'),
('아몬드 알레르기', '견과류'),
('캐슈넛 알레르기', '견과류'),
('피스타치오 알레르기', '견과류'),
('헤이즐넛 알레르기', '견과류'),
('브라질넛 알레르기', '견과류'),
('마카다미아 알레르기', '견과류'),
('잣 알레르기', '견과류'),
('참깨 알레르기', '씨앗류'),
('해바라기씨 알레르기', '씨앗류'),
('아마씨 알레르기', '씨앗류');

-- 해산물
INSERT INTO allergy (name, category) VALUES
('새우 알레르기', '해산물'),
('게 알레르기', '해산물'),
('랍스터 알레르기', '해산물'),
('조개류 알레르기', '해산물'),
('오징어/문어 알레르기', '해산물'),
('생선 알레르기', '해산물'),
('어패류 일반 알레르기', '해산물');

-- 유제품
INSERT INTO allergy (name, category) VALUES
('우유 단백질 알레르기', '유제품'),
('유당 불내성', '유제품'),
('카제인 알레르기', '유제품'),
('유청 단백질 알레르기', '유제품'),
('치즈 알레르기', '유제품'),
('요구르트 알레르기', '유제품');

-- 곡물
INSERT INTO allergy (name, category) VALUES
('밀 알레르기', '곡물'),
('글루텐 알레르기/셀리악병', '곡물'),
('보리 알레르기', '곡물'),
('호밀 알레르기', '곡물'),
('옥수수 알레르기', '곡물'),
('쌀 알레르기', '곡물'),
('귀리 알레르기', '곡물'),
('메밀 알레르기', '곡물'),
('수수 알레르기', '곡물');

-- 과일
INSERT INTO allergy (name, category) VALUES
('사과 알레르기', '과일'),
('복숭아/자두/체리 알레르기', '과일'),
('키위 알레르기', '과일'),
('바나나 알레르기', '과일'),
('감귤류 알레르기', '과일'),
('베리류 알레르기', '과일'),
('멜론/수박 알레르기', '과일'),
('아보카도 알레르기', '과일'),
('망고 알레르기', '과일'),
('파인애플 알레르기', '과일');

-- 채소
INSERT INTO allergy (name, category) VALUES
('토마토 알레르기', '채소'),
('가지 알레르기', '채소'),
('감자/감자류 알레르기', '채소'),
('콩과 식물 알레르기', '채소'),
('대두/콩 알레르기', '채소'),
('셀러리 알레르기', '채소'),
('당근 알레르기', '채소'),
('양파/마늘/파 알레르기', '채소'),
('브로콜리/양배추 알레르기', '채소'),
('시금치 알레르기', '채소');

-- 육류
INSERT INTO allergy (name, category) VALUES
('소고기 알레르기', '육류'),
('돼지고기 알레르기', '육류'),
('닭고기 알레르기', '육류'),
('양고기 알레르기', '육류'),
('알파-갈 증후군(붉은 육류 알레르기)', '육류'),
('내장육 알레르기', '육류');

-- ---------------------------------------------------------------------



-- ---------------------------------------------------------------------
-- Table `user_allergy` <<< 사용자가 가지고 있는 알레르기 정보 
-- ---------------------------------------------------------------------
CREATE TABLE user_allergy (
    user_id      BIGINT UNSIGNED,
    allergy_id   INT,
    PRIMARY KEY (user_id, allergy_id),
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (allergy_id) REFERENCES allergy(allergy_id) ON DELETE CASCADE
);

-- 예: user_id = 1번 사용자가 땅콩(1), 새우(13), 유당불내성(21)을 선택한 경우
INSERT INTO user_allergy (user_id, allergy_id) VALUES
(1, 11),

(2, 46),
(2, 30),
(2, 26),
(3, 49),
(3, 8),
(3, 20),
(3, 42),
(4, 33),
(4, 16),
(4, 17),
(5, 12),
(6, 36),
(6, 50),
(6, 13),
(6, 34),
(6, 23),
(7, 34),
(8, 51),
(8, 29),
(8, 11),
(9, 41),
(9, 2),
(9, 53),
(9, 22),
(10, 58),
(10, 37),
(10, 50),
(10, 8),
(11, 44),
(11, 33),
(11, 2),
(11, 11),
(12, 27),
(12, 26),
(12, 58),
(12, 35),
(13, 38),
(13, 1),
(13, 13),
(13, 51),
(13, 3),
(14, 22),
(14, 42),
(14, 6),
(14, 24),
(15, 57),
(15, 6),
(16, 50),
(16, 57),
(17, 46),
(17, 34),
(18, 34),
(18, 44),
(18, 39),
(18, 14),
(19, 1),
(19, 9),
(20, 40),
(20, 13),
(20, 49),
(21, 54),
(21, 1),
(21, 34),
(21, 46),
(22, 14),
(22, 34),
(22, 48),
(22, 22),
(23, 27),
(23, 58),
(23, 51),
(23, 14),
(23, 23),
(24, 3),
(25, 41),
(25, 34),
(26, 22),
(27, 20),
(27, 29),
(28, 15),
(28, 52),
(28, 34),
(29, 4),
(29, 21),
(30, 20),
(30, 11),
(30, 42),
(30, 27);





-- ---------------------------------------------------------------------
-- Table `meal` <<< 식단 등록
-- ---------------------------------------------------------------------
CREATE TABLE meal (
    meal_id         BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    user_id         BIGINT UNSIGNED NOT NULL,             -- 식단 작성자 (일반회원)
    image_url       VARCHAR(500) NOT NULL,                -- 식단 이미지 경로
    detected_foods  TEXT,                                 -- AI가 분석한 음식 이름 리스트 (예: "닭가슴살, 고구마")
    total_calories  FLOAT,                                -- 총 칼로리 (예상값)
    carbohydrates   FLOAT,                                -- 탄수화물 (g)
    protein         FLOAT,                                -- 단백질 (g)
    fat             FLOAT,                                -- 지방 (g)
    sugars			FLOAT,   							  -- 당류 (g)
	sodium			FLOAT,   							  -- 나트륨 (g)
    meal_time       DATETIME DEFAULT CURRENT_TIMESTAMP,   -- 식사 시각
    created_at      DATETIME DEFAULT CURRENT_TIMESTAMP,
    auto_score 		FLOAT,
    ai_feedback 	TEXT,
    meal_type		VARCHAR(10),                          -- 식사 종류 (아침, 점심, 저녁 자동 구)
	like_count      INT DEFAULT 0,  					  -- 좋아요 수
    dislike_count   INT DEFAULT 0,  					  -- 싫어요 수
    
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);




INSERT INTO meal (
    user_id, image_url, detected_foods, total_calories, carbohydrates,
    protein, fat, sugars, sodium, meal_time, created_at,
    auto_score, ai_feedback, meal_type, like_count, dislike_count
) VALUES (
    15, '/uploads/meals/15/cinnamon_punch.jpg', '식혜', 413.7,
    49.4, 29.4, 7.8, 10.4, 781.6,
    '2025-05-05 23:47:52', '2025-05-05 23:47:52', 28.1, 'Too much fat',
    '아침', 3, 3
);
INSERT INTO meal (
    user_id, image_url, detected_foods, total_calories, carbohydrates,
    protein, fat, sugars, sodium, meal_time, created_at,
    auto_score, ai_feedback, meal_type, like_count, dislike_count
) VALUES (
    26, '/uploads/meals/26/cold_raw_fish.jpg', '회', 352.0,
    81.3, 46.3, 13.3, 10.2, 491.5,
    '2025-05-18 03:47:52', '2025-05-18 03:47:52', 77.1, 'Too much fat',
    '아침', 14, 5
);
INSERT INTO meal (
    user_id, image_url, detected_foods, total_calories, carbohydrates,
    protein, fat, sugars, sodium, meal_time, created_at,
    auto_score, ai_feedback, meal_type, like_count, dislike_count
) VALUES (
    30, '/uploads/meals/30/doenjang_stew.jpg', '된장찌개', 544.9,
    73.4, 30.2, 23.9, 7.3, 925.7,
    '2025-05-20 22:47:52', '2025-05-20 22:47:52', 84.6, 'Balanced meal',
    '점심', 17, 1
);
INSERT INTO meal (
    user_id, image_url, detected_foods, total_calories, carbohydrates,
    protein, fat, sugars, sodium, meal_time, created_at,
    auto_score, ai_feedback, meal_type, like_count, dislike_count
) VALUES (
    16, '/uploads/meals/16/dumpling.jpg', '만두', 492.8,
    60.7, 29.9, 11.1, 10.8, 764.0,
    '2025-05-16 13:47:52', '2025-05-16 13:47:52', 3.6, 'Lacking protein',
    '아침', 8, 5
);
INSERT INTO meal (
    user_id, image_url, detected_foods, total_calories, carbohydrates,
    protein, fat, sugars, sodium, meal_time, created_at,
    auto_score, ai_feedback, meal_type, like_count, dislike_count
) VALUES (
    27, '/uploads/meals/27/fried_chicken.jpg', '치킨', 589.0,
    68.7, 27.8, 7.2, 7.0, 586.0,
    '2025-05-12 08:47:52', '2025-05-12 08:47:52', 81.1, 'Too much fat',
    '아침', 1, 3
);
INSERT INTO meal (
    user_id, image_url, detected_foods, total_calories, carbohydrates,
    protein, fat, sugars, sodium, meal_time, created_at,
    auto_score, ai_feedback, meal_type, like_count, dislike_count
) VALUES (
    9, '/uploads/meals/9/fried_shrimp.jpg', '튀김 새우', 723.3,
    72.4, 18.0, 12.0, 15.8, 404.3,
    '2025-04-29 12:47:52', '2025-04-29 12:47:52', 99.1, 'Balanced meal',
    '점심', 9, 4
);
INSERT INTO meal (
    user_id, image_url, detected_foods, total_calories, carbohydrates,
    protein, fat, sugars, sodium, meal_time, created_at,
    auto_score, ai_feedback, meal_type, like_count, dislike_count
) VALUES (
    25, '/uploads/meals/25/galbi_grilled.jpg', '갈비구이', 235.9,
    51.7, 16.1, 22.6, 19.6, 832.0,
    '2025-05-18 15:47:52', '2025-05-18 15:47:52', 70.2, 'Balanced meal',
    '점심', 20, 3
);
INSERT INTO meal (
    user_id, image_url, detected_foods, total_calories, carbohydrates,
    protein, fat, sugars, sodium, meal_time, created_at,
    auto_score, ai_feedback, meal_type, like_count, dislike_count
) VALUES (
    30, '/uploads/meals/30/galbitang.jpg', '갈비탕', 502.5,
    62.6, 33.5, 25.8, 9.3, 153.4,
    '2025-04-29 14:47:52', '2025-04-29 14:47:52', 71.0, 'Too much fat',
    '저녁', 16, 0
);
INSERT INTO meal (
    user_id, image_url, detected_foods, total_calories, carbohydrates,
    protein, fat, sugars, sodium, meal_time, created_at,
    auto_score, ai_feedback, meal_type, like_count, dislike_count
) VALUES (
    21, '/uploads/meals/21/gimbap.jpg', '김밥', 743.2,
    68.8, 13.4, 10.1, 5.4, 997.2,
    '2025-04-21 01:47:52', '2025-04-21 01:47:52', 3.8, 'Too much fat',
    '점심', 15, 5
);
INSERT INTO meal (
    user_id, image_url, detected_foods, total_calories, carbohydrates,
    protein, fat, sugars, sodium, meal_time, created_at,
    auto_score, ai_feedback, meal_type, like_count, dislike_count
) VALUES (
    3, '/uploads/meals/3/grilled_fish.jpg', '생선구이', 632.5,
    67.7, 45.6, 20.0, 6.7, 657.2,
    '2025-04-26 05:47:52', '2025-04-26 05:47:52', 26.5, 'Too much fat',
    '저녁', 15, 2
);
INSERT INTO meal (
    user_id, image_url, detected_foods, total_calories, carbohydrates,
    protein, fat, sugars, sodium, meal_time, created_at,
    auto_score, ai_feedback, meal_type, like_count, dislike_count
) VALUES (
    19, '/uploads/meals/19/hangwa.jpg', 'Hangwa', 222.0,
    51.4, 37.0, 24.6, 7.7, 531.0,
    '2025-05-20 18:47:52', '2025-05-20 18:47:52', 1.5, 'Too much fat',
    '저녁', 15, 1
);
INSERT INTO meal (
    user_id, image_url, detected_foods, total_calories, carbohydrates,
    protein, fat, sugars, sodium, meal_time, created_at,
    auto_score, ai_feedback, meal_type, like_count, dislike_count
) VALUES (
    3, '/uploads/meals/3/jajangmyeon.jpg', 'Jajangmyeon', 292.9,
    87.8, 36.5, 10.2, 14.1, 269.8,
    '2025-04-26 09:47:52', '2025-04-26 09:47:52', 26.5, 'Too much fat',
    '저녁', 16, 5
);
INSERT INTO meal (
    user_id, image_url, detected_foods, total_calories, carbohydrates,
    protein, fat, sugars, sodium, meal_time, created_at,
    auto_score, ai_feedback, meal_type, like_count, dislike_count
) VALUES (
    7, '/uploads/meals/7/jjimdak.jpg', 'Jjimdak', 694.0,
    48.7, 20.1, 29.3, 2.3, 962.2,
    '2025-05-12 23:47:52', '2025-05-12 23:47:52', 77.9, 'Lacking protein',
    '점심', 10, 0
);
INSERT INTO meal (
    user_id, image_url, detected_foods, total_calories, carbohydrates,
    protein, fat, sugars, sodium, meal_time, created_at,
    auto_score, ai_feedback, meal_type, like_count, dislike_count
) VALUES (
    23, '/uploads/meals/23/jokbal.jpg', 'Jokbal', 638.5,
    67.6, 15.2, 6.8, 18.7, 996.5,
    '2025-04-29 04:47:52', '2025-04-29 04:47:52', 53.7, 'Too much fat',
    '점심', 1, 2
);
INSERT INTO meal (
    user_id, image_url, detected_foods, total_calories, carbohydrates,
    protein, fat, sugars, sodium, meal_time, created_at,
    auto_score, ai_feedback, meal_type, like_count, dislike_count
) VALUES (
    8, '/uploads/meals/8/kimchi_stew.jpg', '김치찌개', 329.5,
    48.4, 48.5, 15.5, 5.9, 696.1,
    '2025-04-21 08:47:52', '2025-04-21 08:47:52', 29.2, 'Balanced meal',
    '점심', 11, 5
);
INSERT INTO meal (
    user_id, image_url, detected_foods, total_calories, carbohydrates,
    protein, fat, sugars, sodium, meal_time, created_at,
    auto_score, ai_feedback, meal_type, like_count, dislike_count
) VALUES (
    21, '/uploads/meals/21/napa_kimchi.jpg', 'Napa Kimchi', 745.3,
    86.2, 22.3, 20.7, 18.3, 283.0,
    '2025-04-27 08:47:52', '2025-04-27 08:47:52', 47.3, 'Balanced meal',
    '점심', 4, 3
);
INSERT INTO meal (
    user_id, image_url, detected_foods, total_calories, carbohydrates,
    protein, fat, sugars, sodium, meal_time, created_at,
    auto_score, ai_feedback, meal_type, like_count, dislike_count
) VALUES (
    7, '/uploads/meals/7/pickled_perilla.jpg', 'Pickled Perilla', 389.2,
    37.9, 45.2, 15.9, 4.1, 102.5,
    '2025-05-01 12:47:52', '2025-05-01 12:47:52', 65.7, 'Balanced meal',
    '아침', 18, 0
);
INSERT INTO meal (
    user_id, image_url, detected_foods, total_calories, carbohydrates,
    protein, fat, sugars, sodium, meal_time, created_at,
    auto_score, ai_feedback, meal_type, like_count, dislike_count
) VALUES (
    11, '/uploads/meals/11/pizza.jpg', 'Pizza', 268.7,
    69.3, 11.6, 14.9, 14.9, 685.8,
    '2025-05-15 07:47:52', '2025-05-15 07:47:52', 22.3, 'Lacking protein',
    '점심', 17, 4
);
INSERT INTO meal (
    user_id, image_url, detected_foods, total_calories, carbohydrates,
    protein, fat, sugars, sodium, meal_time, created_at,
    auto_score, ai_feedback, meal_type, like_count, dislike_count
) VALUES (
    25, '/uploads/meals/25/samgyetang.jpg', 'Samgyetang', 511.4,
    56.2, 33.0, 26.2, 8.9, 112.3,
    '2025-04-23 05:47:52', '2025-04-23 05:47:52', 19.9, 'Balanced meal',
    '저녁', 1, 2
);
INSERT INTO meal (
    user_id, image_url, detected_foods, total_calories, carbohydrates,
    protein, fat, sugars, sodium, meal_time, created_at,
    auto_score, ai_feedback, meal_type, like_count, dislike_count
) VALUES (
    27, '/uploads/meals/27/soy_braised_beans.jpg', 'Soy Braised Beans', 795.2,
    84.8, 47.8, 15.7, 11.0, 542.4,
    '2025-05-05 10:47:52', '2025-05-05 10:47:52', 92.4, 'Balanced meal',
    '아침', 5, 5
);
INSERT INTO meal (
    user_id, image_url, detected_foods, total_calories, carbohydrates,
    protein, fat, sugars, sodium, meal_time, created_at,
    auto_score, ai_feedback, meal_type, like_count, dislike_count
) VALUES (
    27, '/uploads/meals/27/steamed_cockles.jpg', 'Steamed Cockles', 696.7,
    83.1, 39.8, 5.9, 16.7, 114.1,
    '2025-05-16 05:47:52', '2025-05-16 05:47:52', 37.6, 'Balanced meal',
    '저녁', 20, 0
);
INSERT INTO meal (
    user_id, image_url, detected_foods, total_calories, carbohydrates,
    protein, fat, sugars, sodium, meal_time, created_at,
    auto_score, ai_feedback, meal_type, like_count, dislike_count
) VALUES (
    17, '/uploads/meals/17/steamed_egg.jpg', 'Steamed Egg', 477.3,
    89.7, 45.7, 7.1, 6.9, 356.8,
    '2025-04-28 23:47:52', '2025-04-28 23:47:52', 10.0, 'Lacking protein',
    '점심', 3, 5
);
INSERT INTO meal (
    user_id, image_url, detected_foods, total_calories, carbohydrates,
    protein, fat, sugars, sodium, meal_time, created_at,
    auto_score, ai_feedback, meal_type, like_count, dislike_count
) VALUES (
    24, '/uploads/meals/24/steamed_seafood.jpg', 'Steamed Seafood', 771.9,
    66.8, 43.1, 27.9, 12.2, 522.7,
    '2025-05-06 00:47:52', '2025-05-06 00:47:52', 11.8, 'Lacking protein',
    '점심', 15, 4
);
INSERT INTO meal (
    user_id, image_url, detected_foods, total_calories, carbohydrates,
    protein, fat, sugars, sodium, meal_time, created_at,
    auto_score, ai_feedback, meal_type, like_count, dislike_count
) VALUES (
    26, '/uploads/meals/26/stirfried_potato_strips.jpg', 'Stirfried Potato Strips', 248.4,
    84.5, 46.8, 5.3, 19.6, 537.5,
    '2025-05-03 04:47:52', '2025-05-03 04:47:52', 28.7, 'Too much fat',
    '점심', 0, 1
);
INSERT INTO meal (
    user_id, image_url, detected_foods, total_calories, carbohydrates,
    protein, fat, sugars, sodium, meal_time, created_at,
    auto_score, ai_feedback, meal_type, like_count, dislike_count
) VALUES (
    30, '/uploads/meals/30/stirfried_sausage.jpg', 'Stirfried Sausage', 392.3,
    38.6, 33.4, 29.7, 8.6, 771.4,
    '2025-05-16 10:47:52', '2025-05-16 10:47:52', 73.5, 'Too much fat',
    '저녁', 0, 5
);
INSERT INTO meal (
    user_id, image_url, detected_foods, total_calories, carbohydrates,
    protein, fat, sugars, sodium, meal_time, created_at,
    auto_score, ai_feedback, meal_type, like_count, dislike_count
) VALUES (
    23, '/uploads/meals/23/sundae_sausage.jpg', 'Sundae Sausage', 259.1,
    43.5, 31.0, 14.0, 15.8, 250.7,
    '2025-05-12 20:47:52', '2025-05-12 20:47:52', 65.7, 'Balanced meal',
    '아침', 14, 0
);
INSERT INTO meal (
    user_id, image_url, detected_foods, total_calories, carbohydrates,
    protein, fat, sugars, sodium, meal_time, created_at,
    auto_score, ai_feedback, meal_type, like_count, dislike_count
) VALUES (
    3, '/uploads/meals/3/sweet_rice_drink.jpg', 'Sweet Rice Drink', 217.7,
    30.3, 11.4, 22.8, 14.4, 618.7,
    '2025-05-10 03:47:52', '2025-05-10 03:47:52', 67.2, 'Too much fat',
    '저녁', 2, 4
);
INSERT INTO meal (
    user_id, image_url, detected_foods, total_calories, carbohydrates,
    protein, fat, sugars, sodium, meal_time, created_at,
    auto_score, ai_feedback, meal_type, like_count, dislike_count
) VALUES (
    3, '/uploads/meals/3/tteokbokki.jpg', '떡볶이', 232.4,
    37.8, 16.1, 27.0, 10.7, 798.2,
    '2025-05-05 20:47:52', '2025-05-05 20:47:52', 81.2, 'Balanced meal',
    '저녁', 18, 0
);
INSERT INTO meal (
    user_id, image_url, detected_foods, total_calories, carbohydrates,
    protein, fat, sugars, sodium, meal_time, created_at,
    auto_score, ai_feedback, meal_type, like_count, dislike_count
) VALUES (
    7, '/uploads/meals/7/yakgwa.jpg', 'Yakgwa', 523.7,
    70.2, 18.4, 16.3, 12.7, 627.1,
    '2025-05-18 20:47:52', '2025-05-18 20:47:52', 13.4, 'Lacking protein',
    '저녁', 15, 2
);
INSERT INTO meal (
    user_id, image_url, detected_foods, total_calories, carbohydrates,
    protein, fat, sugars, sodium, meal_time, created_at,
    auto_score, ai_feedback, meal_type, like_count, dislike_count
) VALUES (
    2, '/uploads/meals/2/yukhoe.jpg', 'Yukhoe', 359.7,
    49.7, 13.5, 10.8, 7.0, 380.6,
    '2025-05-12 23:47:52', '2025-05-12 23:47:52', 3.2, 'Balanced meal',
    '저녁', 16, 4
);





-- ---------------------------------------------------------------------
-- Table `meal_feedback` <<< 트레이너 피드백
-- ---------------------------------------------------------------------
CREATE TABLE meal_feedback (
    feedback_id   BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    meal_id       BIGINT UNSIGNED NOT NULL,         -- 피드백 대상 식단
    trainer_id    BIGINT UNSIGNED NOT NULL,         -- 피드백을 남긴 트레이너
    content       TEXT,                             -- 피드백 내용
    trainer_score FLOAT DEFAULT NULL,
    created_at    DATETIME DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (meal_id) REFERENCES meal(meal_id) ON DELETE CASCADE,
    FOREIGN KEY (trainer_id) REFERENCES users(user_id) ON DELETE CASCADE
);

INSERT INTO meal_feedback (meal_id, trainer_id, content, trainer_score, created_at)
VALUES
(1, 3, '단백질 섭취가 좋아요. 채소를 조금 더 추가해보세요.', 8.5, '2025-05-13 21:10:00'),
(2, 4, '균형 잡힌 식단입니다. 다음엔 과일도 추가해보세요.', 9.0, '2025-05-13 21:15:00');



CREATE TABLE notification_type (
    type_code VARCHAR(50) PRIMARY KEY,     -- 예: 'NEW_MEAL', 'MEAL_FEEDBACK', 'FOLLOW'
    description VARCHAR(100)               -- 설명: "새 식단 등록", "피드백 등록", "팔로우 요청"
);

INSERT INTO notification_type (type_code, description)
VALUES 
  ('NEW_MEAL', '회원이 식단을 등록했을 때'),
  ('MEAL_FEEDBACK', '트레이너가 피드백을 남겼을 때'),
  ('FOLLOW_REQUEST', '팔로우 요청이 왔을 때'),
  ('FOLLOW_RESPONSE', '팔로우 요청에 응답했을 때');
  
-- ---------------------------------------------------------------------
-- Table `notification` <<< 알림 기능
-- ---------------------------------------------------------------------
CREATE TABLE notification (
    notification_id  BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    user_id          BIGINT UNSIGNED NOT NULL,
    type_code        VARCHAR(50) NOT NULL,
    content          VARCHAR(255),
    related_meal_id  BIGINT UNSIGNED DEFAULT NULL,
    is_read          BOOLEAN DEFAULT FALSE,
    created_at       DATETIME DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (user_id) REFERENCES users(user_id),
    FOREIGN KEY (type_code) REFERENCES notification_type(type_code),
    FOREIGN KEY (related_meal_id) REFERENCES meal(meal_id)
);



-- ---------------------------------------------------------------------
-- `board` <<< ENUM 정보
-- ---------------------------------------------------------------------
CREATE TABLE board_access_level (
    access_code   VARCHAR(20) PRIMARY KEY,       -- 'ALL', 'TRAINER_ONLY', 'ADMIN_ONLY'
    description   VARCHAR(100) NOT NULL          -- '모든 사용자', '트레이너 전용', '관리자 전용'
);

INSERT INTO board_access_level VALUES
('ALL', '모든 사용자 접근 가능'),
('TRAINER_ONLY', '트레이너 전용 게시판'),
('ADMIN_ONLY', '관리자 전용 게시판');

-- ---------------------------------------------------------------------
-- Table `board` <<< 게시판
-- ---------------------------------------------------------------------
CREATE TABLE board (
    board_id       INT AUTO_INCREMENT PRIMARY KEY,           -- 게시판 고유 ID
    name           VARCHAR(100) NOT NULL,                    -- 게시판 이름
    description    TEXT,                                     -- 설명
    access_code    VARCHAR(20) DEFAULT 'ALL',                -- board_access_level 참조
    created_at     DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at     DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    FOREIGN KEY (access_code) REFERENCES board_access_level(access_code)
) ENGINE = InnoDB;


INSERT INTO board (name, description, access_code)
VALUES 
('자유 게시판', '누구나 자유롭게 이야기할 수 있는 공간입니다.', 'ALL'),
('식단 공유 게시판', '오늘의 식단을 공유해보세요!', 'ALL'),
('트레이너 전용 게시판', '트레이너만 접근 가능한 정보 공유 게시판입니다.', 'TRAINER_ONLY'),
('공지사항', '관리자 공지 및 업데이트 사항을 안내합니다.', 'ADMIN_ONLY'),
('Q&A 게시판', '궁금한 점을 질문하고 답변을 주고받는 공간입니다.', 'ALL');




-- ---------------------------------------------------------------------
-- Table `article`
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS article (
  article_id   INT AUTO_INCREMENT PRIMARY KEY,             -- 게시글 ID
  title        VARCHAR(200) NOT NULL,                      -- 제목
  content      TEXT NOT NULL,                              -- 본문
  board_id     INT NOT NULL,                               -- 소속 게시판
  user_id      BIGINT UNSIGNED NOT NULL,                   -- 작성자 (users 테이블 참조)
  image_url     VARCHAR(500),                              -- 대표 이미지 (썸네일 용)
  view_cnt     INT DEFAULT 0,                              -- 조회수 (기본값 0)
  like_count   INT DEFAULT 0,    				  		   -- 추가
  dislike_count INT DEFAULT 0,  						   -- 좋아요 수
  created_at   DATETIME DEFAULT CURRENT_TIMESTAMP,         -- 싫어요 수
  modified_at  DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, -- 수정일

  CONSTRAINT fk_article_board FOREIGN KEY (board_id)
      REFERENCES board(board_id)
      ON DELETE CASCADE
      ON UPDATE CASCADE,

  CONSTRAINT fk_article_user FOREIGN KEY (user_id)
      REFERENCES users(user_id)
      ON DELETE CASCADE
      ON UPDATE CASCADE
) ENGINE = InnoDB;



-- ---------------------------------------------------------------------
-- Table `article_file`     <<< (다중 이미지/파일 관리용)
-- ---------------------------------------------------------------------
CREATE TABLE article_file (
  file_id        BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,  -- 파일 고유 ID
  article_id     INT NOT NULL,                                -- 연결된 게시글 ID
  original_name  VARCHAR(255) NOT NULL,                       -- 원본 파일명 (사용자가 업로드한 이름)
  saved_name     VARCHAR(255) NOT NULL,                       -- 서버 저장 파일명 (UUID 등으로 중복 방지)
  file_path      VARCHAR(500) NOT NULL,                       -- 전체 경로 또는 URL
  file_type      VARCHAR(100),                                -- MIME 타입 (image/png, application/pdf 등)
  uploaded_at    DATETIME DEFAULT CURRENT_TIMESTAMP,

  FOREIGN KEY (article_id) REFERENCES article(article_id)
    ON DELETE CASCADE
    ON UPDATE CASCADE
) ENGINE = InnoDB;


-- 🔹 board_id = 1
INSERT INTO article (title, content, board_id, user_id, image_url, view_cnt, like_count, dislike_count, created_at, modified_at)
VALUES
('오늘 하루 어땠나요?', '모두 화이팅입니다!', 1, 25, '/images/article_b1_1.jpg', 280, 28, 14, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('식단 어떻게 구성하세요?', '이 팁 진짜 꿀팁이에요.', 1, 24, NULL, 383, 28, 4, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('운동 인증합니다!', '운동 열심히 하고 왔어요!', 1, 14, NULL, 64, 66, 6, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('운동 인증합니다!', '이 팁 진짜 꿀팁이에요.', 1, 26, NULL, 425, 25, 17, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('꿀팁 공유합니다!', '식단 조절로 많이 변화했어요.', 1, 23, NULL, 489, 20, 6, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('오늘의 식단', '식단 조절로 많이 변화했어요.', 1, 1, '/images/article_b1_6.jpg', 327, 49, 6, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('운동 루틴 추천 좀', '이 팁 진짜 꿀팁이에요.', 1, 23, NULL, 466, 28, 11, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('다이어트 후기입니다', '운동 열심히 하고 왔어요!', 1, 40, NULL, 222, 17, 11, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('오늘 하루 어땠나요?', '식단 조절로 많이 변화했어요.', 1, 4, NULL, 73, 92, 12, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('운동 루틴 추천 좀', '이 팁 진짜 꿀팁이에요.', 1, 25, NULL, 209, 75, 12, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('오늘 하루 어땠나요?', '식단 조절로 많이 변화했어요.', 1, 7, '/images/article_b1_11.jpg', 489, 51, 17, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('식단 어떻게 구성하세요?', '이렇게 먹었어요!', 1, 12, NULL, 87, 0, 9, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('질문 있어요!', '모두 화이팅입니다!', 1, 36, NULL, 339, 61, 10, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('운동 루틴 추천 좀', '이 루틴 효과 좋아요.', 1, 12, NULL, 119, 72, 20, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('좋은 하루!', '식단 조절로 많이 변화했어요.', 1, 40, NULL, 260, 12, 9, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('이 앱 정말 좋아요!', '운동 열심히 하고 왔어요!', 1, 28, '/images/article_b1_16.jpg', 398, 45, 5, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('꿀팁 공유합니다!', '모두 화이팅입니다!', 1, 3, NULL, 67, 25, 18, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('질문 있어요!', '운동 열심히 하고 왔어요!', 1, 23, NULL, 441, 3, 17, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('운동 루틴 추천 좀', '이렇게 식단 구성하면 괜찮을까요?', 1, 16, NULL, 427, 26, 13, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('좋은 하루!', '오늘은 이런저런 일이 많았네요.', 1, 14, NULL, 328, 39, 2, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('운동 루틴 추천 좀', '모두 화이팅입니다!', 1, 20, '/images/article_b1_21.jpg', 22, 32, 9, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('오늘의 식단', '정말 유용한 기능이네요!', 1, 32, NULL, 277, 29, 6, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('이 앱 정말 좋아요!', '이렇게 먹었어요!', 1, 3, NULL, 63, 70, 17, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('운동 인증합니다!', '이렇게 식단 구성하면 괜찮을까요?', 1, 8, NULL, 471, 20, 5, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('오늘의 식단', '식단 조절로 많이 변화했어요.', 1, 1, NULL, 194, 80, 9, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('꿀팁 공유합니다!', '이 팁 진짜 꿀팁이에요.', 1, 7, '/images/article_b1_26.jpg', 306, 32, 7, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('질문 있어요!', '이 팁 진짜 꿀팁이에요.', 1, 27, NULL, 312, 44, 16, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('다이어트 후기입니다', '정말 유용한 기능이네요!', 1, 1, NULL, 327, 64, 6, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('이 앱 정말 좋아요!', '이 루틴 효과 좋아요.', 1, 23, NULL, 321, 58, 5, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('좋은 하루!', '이렇게 식단 구성하면 괜찮을까요?', 1, 33, NULL, 488, 22, 19, '2024-04-01 12:00:00', '2024-04-01 12:00:00');

-- 🔹 board_id = 2
INSERT INTO article (title, content, board_id, user_id, image_url, view_cnt, like_count, dislike_count, created_at, modified_at)
VALUES
('오늘의 식단', '궁금한 게 있어요!', 2, 14, '/images/article_b2_1.jpg', 129, 99, 4, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('오늘 하루 어땠나요?', '모두 화이팅입니다!', 2, 12, NULL, 231, 86, 18, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('꿀팁 공유합니다!', '모두 화이팅입니다!', 2, 17, NULL, 465, 75, 0, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('질문 있어요!', '이렇게 먹었어요!', 2, 29, NULL, 292, 78, 10, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('식단 어떻게 구성하세요?', '이 루틴 효과 좋아요.', 2, 9, NULL, 485, 6, 6, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('좋은 하루!', '이 팁 진짜 꿀팁이에요.', 2, 33, '/images/article_b2_6.jpg', 334, 33, 6, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('식단 어떻게 구성하세요?', '이렇게 식단 구성하면 괜찮을까요?', 2, 36, NULL, 179, 89, 19, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('오늘의 식단', '궁금한 게 있어요!', 2, 11, NULL, 26, 100, 12, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('꿀팁 공유합니다!', '이 루틴 효과 좋아요.', 2, 10, NULL, 274, 22, 18, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('질문 있어요!', '이 팁 진짜 꿀팁이에요.', 2, 35, NULL, 310, 38, 12, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('오늘 하루 어땠나요?', '오늘은 이런저런 일이 많았네요.', 2, 38, '/images/article_b2_11.jpg', 95, 91, 8, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('다이어트 후기입니다', '이렇게 먹었어요!', 2, 16, NULL, 228, 73, 12, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('오늘의 식단', '이 루틴 효과 좋아요.', 2, 39, NULL, 225, 82, 20, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('오늘 하루 어땠나요?', '이렇게 식단 구성하면 괜찮을까요?', 2, 11, NULL, 179, 12, 16, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('이 앱 정말 좋아요!', '운동 열심히 하고 왔어요!', 2, 14, NULL, 183, 90, 4, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('오늘의 식단', '모두 화이팅입니다!', 2, 29, '/images/article_b2_16.jpg', 330, 60, 2, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('꿀팁 공유합니다!', '이 팁 진짜 꿀팁이에요.', 2, 23, NULL, 444, 41, 5, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('식단 어떻게 구성하세요?', '오늘은 이런저런 일이 많았네요.', 2, 25, NULL, 242, 46, 12, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('좋은 하루!', '모두 화이팅입니다!', 2, 22, NULL, 441, 0, 2, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('좋은 하루!', '운동 열심히 하고 왔어요!', 2, 31, NULL, 116, 40, 19, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('꿀팁 공유합니다!', '오늘은 이런저런 일이 많았네요.', 2, 26, '/images/article_b2_21.jpg', 337, 7, 13, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('오늘의 식단', '식단 조절로 많이 변화했어요.', 2, 23, NULL, 364, 50, 9, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('운동 루틴 추천 좀', '모두 화이팅입니다!', 2, 9, NULL, 414, 53, 16, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('오늘 하루 어땠나요?', '이렇게 먹었어요!', 2, 21, NULL, 209, 38, 8, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('꿀팁 공유합니다!', '오늘은 이런저런 일이 많았네요.', 2, 40, NULL, 210, 63, 9, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('오늘 하루 어땠나요?', '정말 유용한 기능이네요!', 2, 33, '/images/article_b2_26.jpg', 142, 99, 6, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('오늘 하루 어땠나요?', '이 팁 진짜 꿀팁이에요.', 2, 32, NULL, 269, 18, 3, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('이 앱 정말 좋아요!', '오늘은 이런저런 일이 많았네요.', 2, 11, NULL, 453, 79, 20, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('이 앱 정말 좋아요!', '궁금한 게 있어요!', 2, 1, NULL, 157, 46, 17, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('이 앱 정말 좋아요!', '정말 유용한 기능이네요!', 2, 1, NULL, 327, 75, 19, '2024-04-01 12:00:00', '2024-04-01 12:00:00');

-- 🔹 board_id = 3
INSERT INTO article (title, content, board_id, user_id, image_url, view_cnt, like_count, dislike_count, created_at, modified_at)
VALUES
('이 앱 정말 좋아요!', '이렇게 먹었어요!', 3, 40, '/images/article_b3_1.jpg', 331, 48, 11, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('오늘의 식단', '정말 유용한 기능이네요!', 3, 40, NULL, 302, 7, 1, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('식단 어떻게 구성하세요?', '운동 열심히 하고 왔어요!', 3, 37, NULL, 178, 61, 9, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('식단 어떻게 구성하세요?', '운동 열심히 하고 왔어요!', 3, 37, NULL, 19, 31, 2, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('식단 어떻게 구성하세요?', '이 루틴 효과 좋아요.', 3, 33, NULL, 294, 30, 11, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('운동 루틴 추천 좀', '정말 유용한 기능이네요!', 3, 37, '/images/article_b3_6.jpg', 70, 31, 18, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('꿀팁 공유합니다!', '정말 유용한 기능이네요!', 3, 38, NULL, 497, 37, 19, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('이 앱 정말 좋아요!', '식단 조절로 많이 변화했어요.', 3, 38, NULL, 66, 14, 13, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('오늘의 식단', '오늘은 이런저런 일이 많았네요.', 3, 40, NULL, 263, 2, 12, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('다이어트 후기입니다', '모두 화이팅입니다!', 3, 37, NULL, 482, 65, 7, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('꿀팁 공유합니다!', '이 루틴 효과 좋아요.', 3, 34, '/images/article_b3_11.jpg', 436, 77, 12, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('질문 있어요!', '이 루틴 효과 좋아요.', 3, 39, NULL, 395, 38, 6, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('이 앱 정말 좋아요!', '운동 열심히 하고 왔어요!', 3, 33, NULL, 113, 69, 10, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('운동 인증합니다!', '모두 화이팅입니다!', 3, 36, NULL, 336, 19, 19, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('오늘의 식단', '식단 조절로 많이 변화했어요.', 3, 31, NULL, 110, 32, 19, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('오늘의 식단', '운동 열심히 하고 왔어요!', 3, 39, '/images/article_b3_16.jpg', 84, 48, 10, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('운동 루틴 추천 좀', '궁금한 게 있어요!', 3, 40, NULL, 425, 70, 15, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('오늘의 식단', '궁금한 게 있어요!', 3, 40, NULL, 198, 12, 5, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('운동 루틴 추천 좀', '운동 열심히 하고 왔어요!', 3, 36, NULL, 318, 40, 3, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('운동 인증합니다!', '모두 화이팅입니다!', 3, 40, NULL, 304, 86, 10, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('좋은 하루!', '정말 유용한 기능이네요!', 3, 35, '/images/article_b3_21.jpg', 293, 46, 0, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('질문 있어요!', '모두 화이팅입니다!', 3, 37, NULL, 476, 56, 17, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('오늘의 식단', '모두 화이팅입니다!', 3, 38, NULL, 317, 78, 6, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('질문 있어요!', '이렇게 식단 구성하면 괜찮을까요?', 3, 31, NULL, 248, 61, 11, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('꿀팁 공유합니다!', '이렇게 먹었어요!', 3, 31, NULL, 455, 93, 18, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('다이어트 후기입니다', '모두 화이팅입니다!', 3, 31, '/images/article_b3_26.jpg', 43, 48, 17, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('좋은 하루!', '이렇게 식단 구성하면 괜찮을까요?', 3, 39, NULL, 284, 34, 4, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('이 앱 정말 좋아요!', '운동 열심히 하고 왔어요!', 3, 34, NULL, 315, 98, 0, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('꿀팁 공유합니다!', '이렇게 식단 구성하면 괜찮을까요?', 3, 35, NULL, 432, 100, 20, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('오늘의 식단', '이렇게 먹었어요!', 3, 34, NULL, 432, 72, 3, '2024-04-01 12:00:00', '2024-04-01 12:00:00');

-- 🔹 board_id = 4
INSERT INTO article (title, content, board_id, user_id, image_url, view_cnt, like_count, dislike_count, created_at, modified_at)
VALUES
('운동 인증합니다!', '모두 화이팅입니다!', 4, 41, '/images/article_b4_1.jpg', 76, 11, 13, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('질문 있어요!', '운동 열심히 하고 왔어요!', 4, 41, NULL, 289, 84, 10, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('꿀팁 공유합니다!', '이렇게 식단 구성하면 괜찮을까요?', 4, 41, NULL, 136, 33, 0, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('다이어트 후기입니다', '오늘은 이런저런 일이 많았네요.', 4, 41, NULL, 486, 88, 7, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('운동 인증합니다!', '모두 화이팅입니다!', 4, 41, NULL, 64, 52, 9, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('좋은 하루!', '이렇게 식단 구성하면 괜찮을까요?', 4, 41, '/images/article_b4_6.jpg', 95, 9, 19, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('오늘 하루 어땠나요?', '이렇게 식단 구성하면 괜찮을까요?', 4, 41, NULL, 25, 17, 2, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('오늘 하루 어땠나요?', '이 팁 진짜 꿀팁이에요.', 4, 41, NULL, 401, 81, 19, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('오늘의 식단', '이렇게 먹었어요!', 4, 41, NULL, 136, 60, 8, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('질문 있어요!', '이 팁 진짜 꿀팁이에요.', 4, 41, NULL, 108, 92, 4, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('꿀팁 공유합니다!', '이 루틴 효과 좋아요.', 4, 41, '/images/article_b4_11.jpg', 104, 7, 8, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('운동 인증합니다!', '정말 유용한 기능이네요!', 4, 41, NULL, 438, 37, 20, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('좋은 하루!', '모두 화이팅입니다!', 4, 41, NULL, 474, 78, 20, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('좋은 하루!', '정말 유용한 기능이네요!', 4, 41, NULL, 40, 26, 0, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('운동 인증합니다!', '운동 열심히 하고 왔어요!', 4, 41, NULL, 331, 44, 16, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('운동 루틴 추천 좀', '궁금한 게 있어요!', 4, 41, '/images/article_b4_16.jpg', 242, 15, 3, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('좋은 하루!', '식단 조절로 많이 변화했어요.', 4, 41, NULL, 88, 41, 14, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('이 앱 정말 좋아요!', '모두 화이팅입니다!', 4, 41, NULL, 257, 57, 10, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('이 앱 정말 좋아요!', '이렇게 식단 구성하면 괜찮을까요?', 4, 41, NULL, 270, 86, 9, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('좋은 하루!', '오늘은 이런저런 일이 많았네요.', 4, 41, NULL, 325, 3, 20, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('식단 어떻게 구성하세요?', '이렇게 식단 구성하면 괜찮을까요?', 4, 41, '/images/article_b4_21.jpg', 222, 37, 4, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('질문 있어요!', '오늘은 이런저런 일이 많았네요.', 4, 41, NULL, 68, 78, 11, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('운동 인증합니다!', '정말 유용한 기능이네요!', 4, 41, NULL, 434, 99, 20, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('좋은 하루!', '운동 열심히 하고 왔어요!', 4, 41, NULL, 169, 28, 0, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('다이어트 후기입니다', '오늘은 이런저런 일이 많았네요.', 4, 41, NULL, 286, 10, 5, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('꿀팁 공유합니다!', '이렇게 먹었어요!', 4, 41, '/images/article_b4_26.jpg', 71, 52, 10, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('오늘 하루 어땠나요?', '모두 화이팅입니다!', 4, 41, NULL, 199, 52, 20, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('다이어트 후기입니다', '이렇게 먹었어요!', 4, 41, NULL, 70, 70, 2, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('질문 있어요!', '이렇게 식단 구성하면 괜찮을까요?', 4, 41, NULL, 392, 44, 16, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('질문 있어요!', '모두 화이팅입니다!', 4, 41, NULL, 405, 77, 17, '2024-04-01 12:00:00', '2024-04-01 12:00:00');

-- 🔹 board_id = 5
INSERT INTO article (title, content, board_id, user_id, image_url, view_cnt, like_count, dislike_count, created_at, modified_at)
VALUES
('오늘의 식단', '식단 조절로 많이 변화했어요.', 5, 23, '/images/article_b5_1.jpg', 208, 51, 19, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('다이어트 후기입니다', '궁금한 게 있어요!', 5, 38, NULL, 162, 93, 13, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('꿀팁 공유합니다!', '운동 열심히 하고 왔어요!', 5, 24, NULL, 461, 71, 5, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('다이어트 후기입니다', '이 팁 진짜 꿀팁이에요.', 5, 16, NULL, 198, 17, 8, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('오늘의 식단', '운동 열심히 하고 왔어요!', 5, 25, NULL, 461, 86, 19, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('꿀팁 공유합니다!', '이 루틴 효과 좋아요.', 5, 36, '/images/article_b5_6.jpg', 498, 78, 5, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('식단 어떻게 구성하세요?', '이 루틴 효과 좋아요.', 5, 29, NULL, 301, 50, 11, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('좋은 하루!', '이렇게 식단 구성하면 괜찮을까요?', 5, 5, NULL, 476, 60, 10, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('오늘 하루 어땠나요?', '이 팁 진짜 꿀팁이에요.', 5, 13, NULL, 278, 66, 9, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('좋은 하루!', '이렇게 먹었어요!', 5, 2, NULL, 452, 46, 6, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('운동 루틴 추천 좀', '오늘은 이런저런 일이 많았네요.', 5, 19, '/images/article_b5_11.jpg', 150, 13, 8, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('이 앱 정말 좋아요!', '정말 유용한 기능이네요!', 5, 24, NULL, 211, 0, 16, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('식단 어떻게 구성하세요?', '이렇게 식단 구성하면 괜찮을까요?', 5, 35, NULL, 88, 53, 9, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('오늘 하루 어땠나요?', '모두 화이팅입니다!', 5, 30, NULL, 196, 73, 7, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('이 앱 정말 좋아요!', '운동 열심히 하고 왔어요!', 5, 34, NULL, 301, 7, 17, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('꿀팁 공유합니다!', '이 팁 진짜 꿀팁이에요.', 5, 19, '/images/article_b5_16.jpg', 399, 49, 20, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('식단 어떻게 구성하세요?', '운동 열심히 하고 왔어요!', 5, 10, NULL, 458, 1, 20, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('오늘의 식단', '오늘은 이런저런 일이 많았네요.', 5, 6, NULL, 131, 92, 13, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('식단 어떻게 구성하세요?', '모두 화이팅입니다!', 5, 5, NULL, 383, 12, 18, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('질문 있어요!', '오늘은 이런저런 일이 많았네요.', 5, 35, NULL, 22, 19, 19, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('이 앱 정말 좋아요!', '정말 유용한 기능이네요!', 5, 32, '/images/article_b5_21.jpg', 190, 75, 8, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('운동 인증합니다!', '이 팁 진짜 꿀팁이에요.', 5, 6, NULL, 188, 80, 4, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('식단 어떻게 구성하세요?', '오늘은 이런저런 일이 많았네요.', 5, 36, NULL, 266, 60, 9, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('오늘의 식단', '운동 열심히 하고 왔어요!', 5, 2, NULL, 374, 83, 15, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('운동 인증합니다!', '오늘은 이런저런 일이 많았네요.', 5, 19, NULL, 114, 18, 13, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('식단 어떻게 구성하세요?', '정말 유용한 기능이네요!', 5, 17, '/images/article_b5_26.jpg', 80, 70, 10, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('꿀팁 공유합니다!', '정말 유용한 기능이네요!', 5, 8, NULL, 56, 21, 10, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('좋은 하루!', '운동 열심히 하고 왔어요!', 5, 3, NULL, 259, 55, 9, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('이 앱 정말 좋아요!', '오늘은 이런저런 일이 많았네요.', 5, 1, NULL, 455, 4, 8, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('이 앱 정말 좋아요!', '이 팁 진짜 꿀팁이에요.', 5, 6, NULL, 391, 54, 8, '2024-04-01 12:00:00', '2024-04-01 12:00:00');


-- ---------------------------------------------------------------------
-- `comment_target_type` >>> ENUM 정보
-- ---------------------------------------------------------------------
CREATE TABLE comment_target_type (
    type_code   VARCHAR(20) PRIMARY KEY,     -- 예: 'ARTICLE', 'MEAL'
    label       VARCHAR(50) NOT NULL         -- 예: '게시글', '식단'
);

-- 초기 데이터 삽입
INSERT INTO comment_target_type (type_code, label) VALUES
('ARTICLE', '게시글'),
('MEAL', '식단');



-- ---------------------------------------------------------------------
-- Table `comment`
-- ---------------------------------------------------------------------
CREATE TABLE comment (
  comment_id   INT AUTO_INCREMENT PRIMARY KEY,
  content      VARCHAR(1000) NOT NULL,               -- 댓글 내용
  user_id      BIGINT UNSIGNED NOT NULL,             -- 작성자
  target_type  VARCHAR(20) NOT NULL,                 -- 댓글 대상 타입 ('ARTICLE', 'MEAL', 등)
  target_id    BIGINT UNSIGNED NOT NULL,             -- 댓글 대상 ID

  lft          INT NOT NULL,                         -- 트리 왼쪽값
  rgt          INT NOT NULL,                         -- 트리 오른쪽값
  depth        INT NOT NULL,                         -- 댓글 깊이

  like_count   INT DEFAULT 0,     					 -- 좋아요 수
  dislike_count INT DEFAULT 0,    					 -- 싫어요 수
  
  created_at   DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_at   DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

  FOREIGN KEY (user_id) REFERENCES users(user_id)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  
  -- target_type은 참조 테이블과 외래키로 연결
  FOREIGN KEY (target_type) REFERENCES comment_target_type(type_code)
    ON DELETE RESTRICT
    ON UPDATE CASCADE
) ENGINE = InnoDB;



INSERT INTO comment (content, user_id, target_type, target_id, lft, rgt, depth, like_count, dislike_count, created_at, updated_at)
VALUES
('정말 유익한 정보네요!', 32, 'ARTICLE', 1, 1, 2, 0, 25, 2, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('댓글 남기고 갑니다~', 6, 'ARTICLE', 1, 3, 4, 0, 13, 2, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('공감합니다!', 27, 'ARTICLE', 1, 5, 6, 0, 13, 2, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('좋은 글 감사합니다!', 10, 'ARTICLE', 2, 1, 2, 0, 6, 10, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('정말 유익한 정보네요!', 6, 'ARTICLE', 2, 3, 4, 0, 25, 1, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('공감합니다!', 39, 'ARTICLE', 2, 5, 6, 0, 28, 3, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('질문 하나 드릴게요.', 16, 'ARTICLE', 3, 1, 2, 0, 20, 10, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('좋은 글 감사합니다!', 3, 'ARTICLE', 3, 3, 4, 0, 26, 5, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('정말 유익한 정보네요!', 22, 'ARTICLE', 3, 5, 6, 0, 7, 1, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('궁금한 점이 있어요~', 7, 'ARTICLE', 4, 1, 2, 0, 2, 2, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('정말 유익한 정보네요!', 37, 'ARTICLE', 4, 3, 4, 0, 6, 1, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('좋은 글 감사합니다!', 26, 'ARTICLE', 4, 5, 6, 0, 8, 8, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('정말 유익한 정보네요!', 18, 'ARTICLE', 5, 1, 2, 0, 12, 3, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('저도 같은 생각이에요.', 26, 'ARTICLE', 5, 3, 4, 0, 1, 3, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('저도 같은 생각이에요.', 10, 'ARTICLE', 5, 5, 6, 0, 6, 0, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('정말 유익한 정보네요!', 16, 'ARTICLE', 6, 1, 2, 0, 9, 9, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('질문 하나 드릴게요.', 6, 'ARTICLE', 6, 3, 4, 0, 2, 3, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('이 식단 참고할게요!', 12, 'ARTICLE', 6, 5, 6, 0, 9, 7, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('댓글 남기고 갑니다~', 30, 'ARTICLE', 7, 1, 2, 0, 18, 10, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('정말 유익한 정보네요!', 33, 'ARTICLE', 7, 3, 4, 0, 4, 10, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('댓글 남기고 갑니다~', 21, 'ARTICLE', 7, 5, 6, 0, 29, 4, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('정말 유익한 정보네요!', 10, 'ARTICLE', 8, 1, 2, 0, 29, 5, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('좋은 글 감사합니다!', 17, 'ARTICLE', 8, 3, 4, 0, 30, 1, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('궁금한 점이 있어요~', 9, 'ARTICLE', 8, 5, 6, 0, 30, 9, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('많은 도움이 되었어요.', 9, 'ARTICLE', 9, 1, 2, 0, 20, 1, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('정말 유익한 정보네요!', 13, 'ARTICLE', 9, 3, 4, 0, 1, 1, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('좋은 글 감사합니다!', 15, 'ARTICLE', 9, 5, 6, 0, 10, 10, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('저도 같은 생각이에요.', 17, 'ARTICLE', 10, 1, 2, 0, 30, 3, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('정말 유익한 정보네요!', 38, 'ARTICLE', 10, 3, 4, 0, 25, 7, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('공감합니다!', 23, 'ARTICLE', 10, 5, 6, 0, 15, 4, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('궁금한 점이 있어요~', 29, 'ARTICLE', 11, 1, 2, 0, 13, 10, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('공감합니다!', 33, 'ARTICLE', 11, 3, 4, 0, 17, 6, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('공감합니다!', 34, 'ARTICLE', 11, 5, 6, 0, 29, 6, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('질문 하나 드릴게요.', 6, 'ARTICLE', 12, 1, 2, 0, 26, 4, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('이 식단 참고할게요!', 9, 'ARTICLE', 12, 3, 4, 0, 15, 2, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('궁금한 점이 있어요~', 30, 'ARTICLE', 12, 5, 6, 0, 28, 10, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('이 식단 참고할게요!', 41, 'ARTICLE', 13, 1, 2, 0, 25, 7, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('좋은 글 감사합니다!', 3, 'ARTICLE', 13, 3, 4, 0, 23, 10, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('댓글 남기고 갑니다~', 33, 'ARTICLE', 13, 5, 6, 0, 6, 10, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('공감합니다!', 41, 'ARTICLE', 14, 1, 2, 0, 29, 8, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('많은 도움이 되었어요.', 26, 'ARTICLE', 14, 3, 4, 0, 28, 4, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('댓글 남기고 갑니다~', 1, 'ARTICLE', 14, 5, 6, 0, 13, 8, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('궁금한 점이 있어요~', 4, 'ARTICLE', 15, 1, 2, 0, 7, 7, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('궁금한 점이 있어요~', 39, 'ARTICLE', 15, 3, 4, 0, 9, 4, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('좋은 글 감사합니다!', 1, 'ARTICLE', 15, 5, 6, 0, 1, 9, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('질문 하나 드릴게요.', 7, 'ARTICLE', 16, 1, 2, 0, 12, 9, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('정말 유익한 정보네요!', 8, 'ARTICLE', 16, 3, 4, 0, 1, 6, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('댓글 남기고 갑니다~', 35, 'ARTICLE', 16, 5, 6, 0, 0, 7, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('궁금한 점이 있어요~', 40, 'ARTICLE', 17, 1, 2, 0, 10, 0, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('질문 하나 드릴게요.', 30, 'ARTICLE', 17, 3, 4, 0, 0, 9, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('저도 같은 생각이에요.', 24, 'ARTICLE', 17, 5, 6, 0, 21, 2, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('공감합니다!', 24, 'ARTICLE', 18, 1, 2, 0, 3, 2, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('이 식단 참고할게요!', 37, 'ARTICLE', 18, 3, 4, 0, 7, 0, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('좋은 글 감사합니다!', 21, 'ARTICLE', 18, 5, 6, 0, 17, 9, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('이 식단 참고할게요!', 32, 'ARTICLE', 19, 1, 2, 0, 20, 6, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('이 식단 참고할게요!', 13, 'ARTICLE', 19, 3, 4, 0, 19, 9, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('저도 같은 생각이에요.', 11, 'ARTICLE', 19, 5, 6, 0, 22, 5, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('좋은 글 감사합니다!', 27, 'ARTICLE', 20, 1, 2, 0, 4, 8, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('궁금한 점이 있어요~', 8, 'ARTICLE', 20, 3, 4, 0, 16, 2, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('많은 도움이 되었어요.', 34, 'ARTICLE', 20, 5, 6, 0, 14, 1, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('많은 도움이 되었어요.', 20, 'ARTICLE', 21, 1, 2, 0, 3, 8, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('궁금한 점이 있어요~', 15, 'ARTICLE', 21, 3, 4, 0, 18, 4, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('좋은 글 감사합니다!', 30, 'ARTICLE', 21, 5, 6, 0, 19, 4, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('정말 유익한 정보네요!', 39, 'ARTICLE', 22, 1, 2, 0, 25, 6, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('많은 도움이 되었어요.', 34, 'ARTICLE', 22, 3, 4, 0, 7, 10, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('많은 도움이 되었어요.', 31, 'ARTICLE', 22, 5, 6, 0, 16, 9, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('정말 유익한 정보네요!', 19, 'ARTICLE', 23, 1, 2, 0, 5, 4, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('이 식단 참고할게요!', 7, 'ARTICLE', 23, 3, 4, 0, 20, 10, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('댓글 남기고 갑니다~', 16, 'ARTICLE', 23, 5, 6, 0, 20, 5, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('질문 하나 드릴게요.', 41, 'ARTICLE', 24, 1, 2, 0, 5, 9, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('이 식단 참고할게요!', 30, 'ARTICLE', 24, 3, 4, 0, 11, 7, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('질문 하나 드릴게요.', 23, 'ARTICLE', 24, 5, 6, 0, 19, 8, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('댓글 남기고 갑니다~', 18, 'ARTICLE', 25, 1, 2, 0, 19, 0, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('저도 같은 생각이에요.', 20, 'ARTICLE', 25, 3, 4, 0, 10, 10, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('공감합니다!', 40, 'ARTICLE', 25, 5, 6, 0, 29, 9, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('많은 도움이 되었어요.', 5, 'ARTICLE', 26, 1, 2, 0, 8, 0, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('좋은 글 감사합니다!', 30, 'ARTICLE', 26, 3, 4, 0, 21, 1, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('궁금한 점이 있어요~', 3, 'ARTICLE', 26, 5, 6, 0, 4, 8, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('정말 유익한 정보네요!', 9, 'ARTICLE', 27, 1, 2, 0, 4, 7, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('공감합니다!', 13, 'ARTICLE', 27, 3, 4, 0, 15, 6, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('질문 하나 드릴게요.', 2, 'ARTICLE', 27, 5, 6, 0, 8, 5, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('저도 같은 생각이에요.', 16, 'ARTICLE', 28, 1, 2, 0, 7, 3, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('질문 하나 드릴게요.', 26, 'ARTICLE', 28, 3, 4, 0, 5, 1, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('저도 같은 생각이에요.', 31, 'ARTICLE', 28, 5, 6, 0, 27, 5, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('댓글 남기고 갑니다~', 17, 'ARTICLE', 29, 1, 2, 0, 18, 10, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('공감합니다!', 33, 'ARTICLE', 29, 3, 4, 0, 15, 1, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('좋은 글 감사합니다!', 14, 'ARTICLE', 29, 5, 6, 0, 5, 1, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('정말 유익한 정보네요!', 26, 'ARTICLE', 30, 1, 2, 0, 17, 5, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('많은 도움이 되었어요.', 17, 'ARTICLE', 30, 3, 4, 0, 4, 6, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('공감합니다!', 7, 'ARTICLE', 30, 5, 6, 0, 11, 9, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('댓글 남기고 갑니다~', 41, 'ARTICLE', 31, 1, 2, 0, 7, 5, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('이 식단 참고할게요!', 12, 'ARTICLE', 31, 3, 4, 0, 29, 8, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('이 식단 참고할게요!', 16, 'ARTICLE', 31, 5, 6, 0, 10, 9, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('공감합니다!', 40, 'ARTICLE', 32, 1, 2, 0, 25, 10, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('저도 같은 생각이에요.', 39, 'ARTICLE', 32, 3, 4, 0, 5, 10, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('이 식단 참고할게요!', 7, 'ARTICLE', 32, 5, 6, 0, 21, 2, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('이 식단 참고할게요!', 9, 'ARTICLE', 33, 1, 2, 0, 22, 7, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('많은 도움이 되었어요.', 12, 'ARTICLE', 33, 3, 4, 0, 1, 7, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('질문 하나 드릴게요.', 39, 'ARTICLE', 33, 5, 6, 0, 26, 3, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('질문 하나 드릴게요.', 23, 'ARTICLE', 34, 1, 2, 0, 12, 10, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('공감합니다!', 28, 'ARTICLE', 34, 3, 4, 0, 0, 1, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('궁금한 점이 있어요~', 14, 'ARTICLE', 34, 5, 6, 0, 4, 2, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('질문 하나 드릴게요.', 18, 'ARTICLE', 35, 1, 2, 0, 14, 5, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('정말 유익한 정보네요!', 24, 'ARTICLE', 35, 3, 4, 0, 15, 10, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('이 식단 참고할게요!', 33, 'ARTICLE', 35, 5, 6, 0, 0, 5, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('좋은 글 감사합니다!', 29, 'ARTICLE', 36, 1, 2, 0, 7, 3, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('많은 도움이 되었어요.', 28, 'ARTICLE', 36, 3, 4, 0, 14, 3, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('이 식단 참고할게요!', 20, 'ARTICLE', 36, 5, 6, 0, 12, 8, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('저도 같은 생각이에요.', 36, 'ARTICLE', 37, 1, 2, 0, 14, 3, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('좋은 글 감사합니다!', 15, 'ARTICLE', 37, 3, 4, 0, 4, 5, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('저도 같은 생각이에요.', 35, 'ARTICLE', 37, 5, 6, 0, 10, 3, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('이 식단 참고할게요!', 7, 'ARTICLE', 38, 1, 2, 0, 10, 10, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('댓글 남기고 갑니다~', 39, 'ARTICLE', 38, 3, 4, 0, 25, 3, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('댓글 남기고 갑니다~', 23, 'ARTICLE', 38, 5, 6, 0, 18, 4, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('많은 도움이 되었어요.', 5, 'ARTICLE', 39, 1, 2, 0, 15, 3, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('궁금한 점이 있어요~', 3, 'ARTICLE', 39, 3, 4, 0, 17, 9, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('공감합니다!', 15, 'ARTICLE', 39, 5, 6, 0, 18, 5, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('정말 유익한 정보네요!', 35, 'ARTICLE', 40, 1, 2, 0, 21, 2, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('좋은 글 감사합니다!', 1, 'ARTICLE', 40, 3, 4, 0, 30, 6, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('댓글 남기고 갑니다~', 5, 'ARTICLE', 40, 5, 6, 0, 26, 8, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('댓글 남기고 갑니다~', 28, 'ARTICLE', 41, 1, 2, 0, 19, 7, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('댓글 남기고 갑니다~', 24, 'ARTICLE', 41, 3, 4, 0, 21, 3, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('많은 도움이 되었어요.', 17, 'ARTICLE', 41, 5, 6, 0, 4, 8, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('저도 같은 생각이에요.', 13, 'ARTICLE', 42, 1, 2, 0, 7, 7, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('많은 도움이 되었어요.', 28, 'ARTICLE', 42, 3, 4, 0, 16, 7, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('공감합니다!', 37, 'ARTICLE', 42, 5, 6, 0, 10, 3, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('공감합니다!', 32, 'ARTICLE', 43, 1, 2, 0, 2, 3, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('많은 도움이 되었어요.', 6, 'ARTICLE', 43, 3, 4, 0, 0, 3, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('댓글 남기고 갑니다~', 1, 'ARTICLE', 43, 5, 6, 0, 18, 10, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('좋은 글 감사합니다!', 24, 'ARTICLE', 44, 1, 2, 0, 10, 1, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('저도 같은 생각이에요.', 26, 'ARTICLE', 44, 3, 4, 0, 9, 2, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('궁금한 점이 있어요~', 41, 'ARTICLE', 44, 5, 6, 0, 14, 0, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('좋은 글 감사합니다!', 32, 'ARTICLE', 45, 1, 2, 0, 6, 0, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('정말 유익한 정보네요!', 35, 'ARTICLE', 45, 3, 4, 0, 13, 1, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('이 식단 참고할게요!', 15, 'ARTICLE', 45, 5, 6, 0, 24, 10, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('많은 도움이 되었어요.', 13, 'ARTICLE', 46, 1, 2, 0, 28, 0, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('질문 하나 드릴게요.', 24, 'ARTICLE', 46, 3, 4, 0, 7, 0, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('좋은 글 감사합니다!', 31, 'ARTICLE', 46, 5, 6, 0, 3, 0, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('공감합니다!', 14, 'ARTICLE', 47, 1, 2, 0, 9, 8, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('정말 유익한 정보네요!', 26, 'ARTICLE', 47, 3, 4, 0, 0, 9, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('저도 같은 생각이에요.', 34, 'ARTICLE', 47, 5, 6, 0, 22, 8, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('좋은 글 감사합니다!', 14, 'ARTICLE', 48, 1, 2, 0, 0, 2, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('정말 유익한 정보네요!', 23, 'ARTICLE', 48, 3, 4, 0, 21, 1, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('이 식단 참고할게요!', 9, 'ARTICLE', 48, 5, 6, 0, 27, 5, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('댓글 남기고 갑니다~', 21, 'ARTICLE', 49, 1, 2, 0, 8, 2, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('질문 하나 드릴게요.', 6, 'ARTICLE', 49, 3, 4, 0, 3, 9, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('공감합니다!', 27, 'ARTICLE', 49, 5, 6, 0, 18, 5, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('정말 유익한 정보네요!', 26, 'ARTICLE', 50, 1, 2, 0, 11, 4, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('이 식단 참고할게요!', 9, 'ARTICLE', 50, 3, 4, 0, 3, 6, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('좋은 글 감사합니다!', 41, 'ARTICLE', 50, 5, 6, 0, 14, 0, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('이 식단 참고할게요!', 3, 'ARTICLE', 51, 1, 2, 0, 15, 2, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('정말 유익한 정보네요!', 5, 'ARTICLE', 51, 3, 4, 0, 3, 5, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('좋은 글 감사합니다!', 19, 'ARTICLE', 51, 5, 6, 0, 10, 7, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('저도 같은 생각이에요.', 18, 'ARTICLE', 52, 1, 2, 0, 26, 5, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('정말 유익한 정보네요!', 2, 'ARTICLE', 52, 3, 4, 0, 16, 6, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('많은 도움이 되었어요.', 11, 'ARTICLE', 52, 5, 6, 0, 14, 9, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('저도 같은 생각이에요.', 12, 'ARTICLE', 53, 1, 2, 0, 17, 8, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('정말 유익한 정보네요!', 2, 'ARTICLE', 53, 3, 4, 0, 5, 8, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('이 식단 참고할게요!', 13, 'ARTICLE', 53, 5, 6, 0, 2, 1, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('많은 도움이 되었어요.', 25, 'ARTICLE', 54, 1, 2, 0, 11, 10, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('질문 하나 드릴게요.', 19, 'ARTICLE', 54, 3, 4, 0, 5, 5, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('댓글 남기고 갑니다~', 14, 'ARTICLE', 54, 5, 6, 0, 3, 7, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('공감합니다!', 13, 'ARTICLE', 55, 1, 2, 0, 19, 4, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('질문 하나 드릴게요.', 27, 'ARTICLE', 55, 3, 4, 0, 11, 9, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('많은 도움이 되었어요.', 33, 'ARTICLE', 55, 5, 6, 0, 30, 7, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('저도 같은 생각이에요.', 6, 'ARTICLE', 56, 1, 2, 0, 9, 3, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('공감합니다!', 13, 'ARTICLE', 56, 3, 4, 0, 29, 3, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('공감합니다!', 35, 'ARTICLE', 56, 5, 6, 0, 29, 3, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('저도 같은 생각이에요.', 19, 'ARTICLE', 57, 1, 2, 0, 0, 6, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('많은 도움이 되었어요.', 30, 'ARTICLE', 57, 3, 4, 0, 27, 10, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('이 식단 참고할게요!', 14, 'ARTICLE', 57, 5, 6, 0, 30, 9, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('정말 유익한 정보네요!', 6, 'ARTICLE', 58, 1, 2, 0, 4, 8, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('질문 하나 드릴게요.', 24, 'ARTICLE', 58, 3, 4, 0, 22, 3, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('정말 유익한 정보네요!', 26, 'ARTICLE', 58, 5, 6, 0, 2, 2, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('좋은 글 감사합니다!', 38, 'ARTICLE', 59, 1, 2, 0, 9, 1, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('질문 하나 드릴게요.', 13, 'ARTICLE', 59, 3, 4, 0, 20, 9, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('공감합니다!', 11, 'ARTICLE', 59, 5, 6, 0, 23, 5, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('저도 같은 생각이에요.', 41, 'ARTICLE', 60, 1, 2, 0, 28, 1, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('이 식단 참고할게요!', 16, 'ARTICLE', 60, 3, 4, 0, 17, 0, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('정말 유익한 정보네요!', 9, 'ARTICLE', 60, 5, 6, 0, 27, 3, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('공감합니다!', 11, 'ARTICLE', 61, 1, 2, 0, 24, 3, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('궁금한 점이 있어요~', 17, 'ARTICLE', 61, 3, 4, 0, 12, 6, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('궁금한 점이 있어요~', 33, 'ARTICLE', 61, 5, 6, 0, 9, 6, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('정말 유익한 정보네요!', 41, 'ARTICLE', 62, 1, 2, 0, 6, 1, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('댓글 남기고 갑니다~', 13, 'ARTICLE', 62, 3, 4, 0, 13, 7, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('이 식단 참고할게요!', 1, 'ARTICLE', 62, 5, 6, 0, 3, 2, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('공감합니다!', 2, 'ARTICLE', 63, 1, 2, 0, 26, 6, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('저도 같은 생각이에요.', 29, 'ARTICLE', 63, 3, 4, 0, 27, 10, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('저도 같은 생각이에요.', 37, 'ARTICLE', 63, 5, 6, 0, 13, 3, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('많은 도움이 되었어요.', 32, 'ARTICLE', 64, 1, 2, 0, 24, 6, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('정말 유익한 정보네요!', 15, 'ARTICLE', 64, 3, 4, 0, 14, 9, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('좋은 글 감사합니다!', 16, 'ARTICLE', 64, 5, 6, 0, 18, 4, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('많은 도움이 되었어요.', 25, 'ARTICLE', 65, 1, 2, 0, 25, 1, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('공감합니다!', 16, 'ARTICLE', 65, 3, 4, 0, 15, 7, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('저도 같은 생각이에요.', 1, 'ARTICLE', 65, 5, 6, 0, 27, 1, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('질문 하나 드릴게요.', 31, 'ARTICLE', 66, 1, 2, 0, 2, 9, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('저도 같은 생각이에요.', 24, 'ARTICLE', 66, 3, 4, 0, 13, 0, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('많은 도움이 되었어요.', 4, 'ARTICLE', 66, 5, 6, 0, 30, 6, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('저도 같은 생각이에요.', 11, 'ARTICLE', 67, 1, 2, 0, 12, 2, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('좋은 글 감사합니다!', 3, 'ARTICLE', 67, 3, 4, 0, 7, 10, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('댓글 남기고 갑니다~', 24, 'ARTICLE', 67, 5, 6, 0, 16, 3, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('공감합니다!', 31, 'ARTICLE', 68, 1, 2, 0, 3, 0, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('공감합니다!', 37, 'ARTICLE', 68, 3, 4, 0, 18, 4, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('정말 유익한 정보네요!', 25, 'ARTICLE', 68, 5, 6, 0, 26, 10, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('질문 하나 드릴게요.', 34, 'ARTICLE', 69, 1, 2, 0, 3, 8, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('정말 유익한 정보네요!', 13, 'ARTICLE', 69, 3, 4, 0, 27, 5, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('궁금한 점이 있어요~', 25, 'ARTICLE', 69, 5, 6, 0, 13, 3, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('많은 도움이 되었어요.', 2, 'ARTICLE', 70, 1, 2, 0, 17, 3, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('댓글 남기고 갑니다~', 38, 'ARTICLE', 70, 3, 4, 0, 14, 10, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('질문 하나 드릴게요.', 29, 'ARTICLE', 70, 5, 6, 0, 29, 10, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('이 식단 참고할게요!', 5, 'ARTICLE', 71, 1, 2, 0, 11, 4, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('정말 유익한 정보네요!', 31, 'ARTICLE', 71, 3, 4, 0, 0, 7, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('정말 유익한 정보네요!', 16, 'ARTICLE', 71, 5, 6, 0, 3, 10, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('좋은 글 감사합니다!', 5, 'ARTICLE', 72, 1, 2, 0, 9, 9, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('좋은 글 감사합니다!', 20, 'ARTICLE', 72, 3, 4, 0, 2, 4, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('공감합니다!', 21, 'ARTICLE', 72, 5, 6, 0, 12, 6, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('댓글 남기고 갑니다~', 20, 'ARTICLE', 73, 1, 2, 0, 13, 4, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('댓글 남기고 갑니다~', 37, 'ARTICLE', 73, 3, 4, 0, 3, 0, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('댓글 남기고 갑니다~', 32, 'ARTICLE', 73, 5, 6, 0, 7, 4, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('저도 같은 생각이에요.', 4, 'ARTICLE', 74, 1, 2, 0, 1, 2, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('댓글 남기고 갑니다~', 23, 'ARTICLE', 74, 3, 4, 0, 13, 0, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('댓글 남기고 갑니다~', 1, 'ARTICLE', 74, 5, 6, 0, 24, 5, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('이 식단 참고할게요!', 12, 'ARTICLE', 75, 1, 2, 0, 23, 8, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('이 식단 참고할게요!', 41, 'ARTICLE', 75, 3, 4, 0, 26, 6, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('이 식단 참고할게요!', 21, 'ARTICLE', 75, 5, 6, 0, 3, 5, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('많은 도움이 되었어요.', 2, 'ARTICLE', 76, 1, 2, 0, 2, 1, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('공감합니다!', 30, 'ARTICLE', 76, 3, 4, 0, 16, 7, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('질문 하나 드릴게요.', 7, 'ARTICLE', 76, 5, 6, 0, 12, 8, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('공감합니다!', 22, 'ARTICLE', 77, 1, 2, 0, 5, 3, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('댓글 남기고 갑니다~', 36, 'ARTICLE', 77, 3, 4, 0, 6, 8, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('정말 유익한 정보네요!', 2, 'ARTICLE', 77, 5, 6, 0, 7, 2, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('궁금한 점이 있어요~', 15, 'ARTICLE', 78, 1, 2, 0, 4, 0, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('저도 같은 생각이에요.', 28, 'ARTICLE', 78, 3, 4, 0, 24, 5, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('좋은 글 감사합니다!', 26, 'ARTICLE', 78, 5, 6, 0, 6, 0, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('정말 유익한 정보네요!', 34, 'ARTICLE', 79, 1, 2, 0, 6, 2, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('공감합니다!', 19, 'ARTICLE', 79, 3, 4, 0, 8, 0, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('댓글 남기고 갑니다~', 38, 'ARTICLE', 79, 5, 6, 0, 12, 2, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('정말 유익한 정보네요!', 30, 'ARTICLE', 80, 1, 2, 0, 26, 6, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('공감합니다!', 38, 'ARTICLE', 80, 3, 4, 0, 13, 8, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('댓글 남기고 갑니다~', 1, 'ARTICLE', 80, 5, 6, 0, 11, 2, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('정말 유익한 정보네요!', 26, 'ARTICLE', 81, 1, 2, 0, 13, 9, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('궁금한 점이 있어요~', 5, 'ARTICLE', 81, 3, 4, 0, 12, 0, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('정말 유익한 정보네요!', 28, 'ARTICLE', 81, 5, 6, 0, 25, 2, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('이 식단 참고할게요!', 10, 'ARTICLE', 82, 1, 2, 0, 18, 2, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('이 식단 참고할게요!', 39, 'ARTICLE', 82, 3, 4, 0, 30, 0, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('댓글 남기고 갑니다~', 30, 'ARTICLE', 82, 5, 6, 0, 2, 1, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('공감합니다!', 8, 'ARTICLE', 83, 1, 2, 0, 28, 0, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('저도 같은 생각이에요.', 36, 'ARTICLE', 83, 3, 4, 0, 8, 7, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('공감합니다!', 2, 'ARTICLE', 83, 5, 6, 0, 22, 6, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('궁금한 점이 있어요~', 39, 'ARTICLE', 84, 1, 2, 0, 22, 7, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('질문 하나 드릴게요.', 7, 'ARTICLE', 84, 3, 4, 0, 11, 1, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('정말 유익한 정보네요!', 1, 'ARTICLE', 84, 5, 6, 0, 19, 3, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('좋은 글 감사합니다!', 9, 'ARTICLE', 85, 1, 2, 0, 29, 0, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('댓글 남기고 갑니다~', 36, 'ARTICLE', 85, 3, 4, 0, 29, 8, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('좋은 글 감사합니다!', 30, 'ARTICLE', 85, 5, 6, 0, 19, 7, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('저도 같은 생각이에요.', 12, 'ARTICLE', 86, 1, 2, 0, 1, 8, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('공감합니다!', 28, 'ARTICLE', 86, 3, 4, 0, 17, 4, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('궁금한 점이 있어요~', 7, 'ARTICLE', 86, 5, 6, 0, 7, 10, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('정말 유익한 정보네요!', 32, 'ARTICLE', 87, 1, 2, 0, 22, 6, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('공감합니다!', 27, 'ARTICLE', 87, 3, 4, 0, 20, 4, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('질문 하나 드릴게요.', 40, 'ARTICLE', 87, 5, 6, 0, 22, 9, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('공감합니다!', 29, 'ARTICLE', 88, 1, 2, 0, 26, 7, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('많은 도움이 되었어요.', 32, 'ARTICLE', 88, 3, 4, 0, 20, 8, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('이 식단 참고할게요!', 10, 'ARTICLE', 88, 5, 6, 0, 13, 2, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('이 식단 참고할게요!', 17, 'ARTICLE', 89, 1, 2, 0, 29, 4, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('궁금한 점이 있어요~', 25, 'ARTICLE', 89, 3, 4, 0, 1, 3, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('좋은 글 감사합니다!', 29, 'ARTICLE', 89, 5, 6, 0, 29, 9, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('좋은 글 감사합니다!', 6, 'ARTICLE', 90, 1, 2, 0, 2, 8, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('정말 유익한 정보네요!', 3, 'ARTICLE', 90, 3, 4, 0, 1, 10, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('저도 같은 생각이에요.', 31, 'ARTICLE', 90, 5, 6, 0, 21, 1, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('이 식단 참고할게요!', 9, 'ARTICLE', 91, 1, 2, 0, 9, 3, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('저도 같은 생각이에요.', 36, 'ARTICLE', 91, 3, 4, 0, 8, 10, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('많은 도움이 되었어요.', 7, 'ARTICLE', 91, 5, 6, 0, 21, 1, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('저도 같은 생각이에요.', 11, 'ARTICLE', 92, 1, 2, 0, 9, 5, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('좋은 글 감사합니다!', 15, 'ARTICLE', 92, 3, 4, 0, 3, 3, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('댓글 남기고 갑니다~', 28, 'ARTICLE', 92, 5, 6, 0, 28, 3, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('궁금한 점이 있어요~', 28, 'ARTICLE', 93, 1, 2, 0, 7, 3, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('댓글 남기고 갑니다~', 34, 'ARTICLE', 93, 3, 4, 0, 29, 8, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('많은 도움이 되었어요.', 3, 'ARTICLE', 93, 5, 6, 0, 10, 8, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('이 식단 참고할게요!', 32, 'ARTICLE', 94, 1, 2, 0, 11, 8, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('정말 유익한 정보네요!', 11, 'ARTICLE', 94, 3, 4, 0, 2, 6, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('좋은 글 감사합니다!', 5, 'ARTICLE', 94, 5, 6, 0, 18, 5, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('질문 하나 드릴게요.', 35, 'ARTICLE', 95, 1, 2, 0, 0, 1, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('좋은 글 감사합니다!', 23, 'ARTICLE', 95, 3, 4, 0, 12, 1, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('좋은 글 감사합니다!', 21, 'ARTICLE', 95, 5, 6, 0, 22, 4, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('저도 같은 생각이에요.', 10, 'ARTICLE', 96, 1, 2, 0, 0, 3, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('저도 같은 생각이에요.', 7, 'ARTICLE', 96, 3, 4, 0, 26, 3, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('이 식단 참고할게요!', 8, 'ARTICLE', 96, 5, 6, 0, 3, 0, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('궁금한 점이 있어요~', 24, 'ARTICLE', 97, 1, 2, 0, 11, 4, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('공감합니다!', 41, 'ARTICLE', 97, 3, 4, 0, 5, 3, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('저도 같은 생각이에요.', 26, 'ARTICLE', 97, 5, 6, 0, 4, 0, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('정말 유익한 정보네요!', 1, 'ARTICLE', 98, 1, 2, 0, 5, 1, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('댓글 남기고 갑니다~', 36, 'ARTICLE', 98, 3, 4, 0, 19, 9, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('좋은 글 감사합니다!', 40, 'ARTICLE', 98, 5, 6, 0, 9, 3, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('좋은 글 감사합니다!', 25, 'ARTICLE', 99, 1, 2, 0, 19, 5, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('궁금한 점이 있어요~', 2, 'ARTICLE', 99, 3, 4, 0, 24, 9, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('저도 같은 생각이에요.', 10, 'ARTICLE', 99, 5, 6, 0, 5, 7, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('댓글 남기고 갑니다~', 39, 'ARTICLE', 100, 1, 2, 0, 8, 1, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('저도 같은 생각이에요.', 41, 'ARTICLE', 100, 3, 4, 0, 23, 1, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('정말 유익한 정보네요!', 23, 'ARTICLE', 100, 5, 6, 0, 2, 8, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('저도 같은 생각이에요.', 17, 'ARTICLE', 101, 1, 2, 0, 21, 5, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('정말 유익한 정보네요!', 20, 'ARTICLE', 101, 3, 4, 0, 16, 9, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('공감합니다!', 15, 'ARTICLE', 101, 5, 6, 0, 18, 7, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('정말 유익한 정보네요!', 18, 'ARTICLE', 102, 1, 2, 0, 4, 5, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('이 식단 참고할게요!', 20, 'ARTICLE', 102, 3, 4, 0, 11, 9, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('댓글 남기고 갑니다~', 37, 'ARTICLE', 102, 5, 6, 0, 22, 7, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('질문 하나 드릴게요.', 37, 'ARTICLE', 103, 1, 2, 0, 8, 1, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('공감합니다!', 4, 'ARTICLE', 103, 3, 4, 0, 1, 10, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('궁금한 점이 있어요~', 3, 'ARTICLE', 103, 5, 6, 0, 8, 10, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('질문 하나 드릴게요.', 27, 'ARTICLE', 104, 1, 2, 0, 20, 10, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('저도 같은 생각이에요.', 21, 'ARTICLE', 104, 3, 4, 0, 24, 10, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('이 식단 참고할게요!', 20, 'ARTICLE', 104, 5, 6, 0, 8, 7, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('저도 같은 생각이에요.', 13, 'ARTICLE', 105, 1, 2, 0, 7, 1, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('댓글 남기고 갑니다~', 14, 'ARTICLE', 105, 3, 4, 0, 8, 7, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('정말 유익한 정보네요!', 6, 'ARTICLE', 105, 5, 6, 0, 24, 7, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('공감합니다!', 20, 'ARTICLE', 106, 1, 2, 0, 2, 9, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('정말 유익한 정보네요!', 32, 'ARTICLE', 106, 3, 4, 0, 14, 6, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('질문 하나 드릴게요.', 15, 'ARTICLE', 106, 5, 6, 0, 17, 5, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('댓글 남기고 갑니다~', 10, 'ARTICLE', 107, 1, 2, 0, 27, 1, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('정말 유익한 정보네요!', 36, 'ARTICLE', 107, 3, 4, 0, 1, 3, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('이 식단 참고할게요!', 37, 'ARTICLE', 107, 5, 6, 0, 22, 10, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('많은 도움이 되었어요.', 38, 'ARTICLE', 108, 1, 2, 0, 22, 10, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('공감합니다!', 41, 'ARTICLE', 108, 3, 4, 0, 3, 5, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('궁금한 점이 있어요~', 12, 'ARTICLE', 108, 5, 6, 0, 21, 4, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('저도 같은 생각이에요.', 15, 'ARTICLE', 109, 1, 2, 0, 26, 5, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('많은 도움이 되었어요.', 13, 'ARTICLE', 109, 3, 4, 0, 5, 1, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('댓글 남기고 갑니다~', 2, 'ARTICLE', 109, 5, 6, 0, 8, 5, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('댓글 남기고 갑니다~', 35, 'ARTICLE', 110, 1, 2, 0, 11, 8, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('많은 도움이 되었어요.', 32, 'ARTICLE', 110, 3, 4, 0, 10, 3, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('궁금한 점이 있어요~', 16, 'ARTICLE', 110, 5, 6, 0, 21, 9, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('댓글 남기고 갑니다~', 34, 'ARTICLE', 111, 1, 2, 0, 15, 5, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('정말 유익한 정보네요!', 10, 'ARTICLE', 111, 3, 4, 0, 10, 5, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('저도 같은 생각이에요.', 15, 'ARTICLE', 111, 5, 6, 0, 21, 5, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('좋은 글 감사합니다!', 36, 'ARTICLE', 112, 1, 2, 0, 11, 0, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('공감합니다!', 11, 'ARTICLE', 112, 3, 4, 0, 15, 7, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('궁금한 점이 있어요~', 34, 'ARTICLE', 112, 5, 6, 0, 19, 1, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('이 식단 참고할게요!', 27, 'ARTICLE', 113, 1, 2, 0, 15, 9, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('이 식단 참고할게요!', 6, 'ARTICLE', 113, 3, 4, 0, 11, 10, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('궁금한 점이 있어요~', 7, 'ARTICLE', 113, 5, 6, 0, 19, 2, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('궁금한 점이 있어요~', 18, 'ARTICLE', 114, 1, 2, 0, 19, 0, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('저도 같은 생각이에요.', 21, 'ARTICLE', 114, 3, 4, 0, 28, 10, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('댓글 남기고 갑니다~', 2, 'ARTICLE', 114, 5, 6, 0, 10, 2, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('질문 하나 드릴게요.', 11, 'ARTICLE', 115, 1, 2, 0, 1, 5, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('정말 유익한 정보네요!', 41, 'ARTICLE', 115, 3, 4, 0, 22, 2, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('좋은 글 감사합니다!', 10, 'ARTICLE', 115, 5, 6, 0, 15, 4, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('이 식단 참고할게요!', 5, 'ARTICLE', 116, 1, 2, 0, 26, 1, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('저도 같은 생각이에요.', 22, 'ARTICLE', 116, 3, 4, 0, 17, 2, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('많은 도움이 되었어요.', 38, 'ARTICLE', 116, 5, 6, 0, 19, 1, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('정말 유익한 정보네요!', 31, 'ARTICLE', 117, 1, 2, 0, 11, 5, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('이 식단 참고할게요!', 33, 'ARTICLE', 117, 3, 4, 0, 23, 7, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('저도 같은 생각이에요.', 20, 'ARTICLE', 117, 5, 6, 0, 14, 0, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('정말 유익한 정보네요!', 3, 'ARTICLE', 118, 1, 2, 0, 16, 1, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('이 식단 참고할게요!', 16, 'ARTICLE', 118, 3, 4, 0, 5, 3, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('궁금한 점이 있어요~', 40, 'ARTICLE', 118, 5, 6, 0, 29, 2, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('많은 도움이 되었어요.', 38, 'ARTICLE', 119, 1, 2, 0, 17, 5, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('정말 유익한 정보네요!', 37, 'ARTICLE', 119, 3, 4, 0, 28, 4, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('질문 하나 드릴게요.', 10, 'ARTICLE', 119, 5, 6, 0, 0, 2, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('저도 같은 생각이에요.', 39, 'ARTICLE', 120, 1, 2, 0, 23, 0, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('많은 도움이 되었어요.', 41, 'ARTICLE', 120, 3, 4, 0, 2, 4, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('저도 같은 생각이에요.', 24, 'ARTICLE', 120, 5, 6, 0, 15, 10, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('정말 유익한 정보네요!', 17, 'ARTICLE', 121, 1, 2, 0, 17, 10, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('댓글 남기고 갑니다~', 8, 'ARTICLE', 121, 3, 4, 0, 14, 8, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('좋은 글 감사합니다!', 11, 'ARTICLE', 121, 5, 6, 0, 20, 8, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('이 식단 참고할게요!', 34, 'ARTICLE', 122, 1, 2, 0, 23, 9, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('정말 유익한 정보네요!', 2, 'ARTICLE', 122, 3, 4, 0, 8, 0, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('좋은 글 감사합니다!', 16, 'ARTICLE', 122, 5, 6, 0, 24, 8, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('궁금한 점이 있어요~', 4, 'ARTICLE', 123, 1, 2, 0, 15, 4, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('많은 도움이 되었어요.', 41, 'ARTICLE', 123, 3, 4, 0, 9, 8, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('많은 도움이 되었어요.', 15, 'ARTICLE', 123, 5, 6, 0, 1, 1, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('이 식단 참고할게요!', 12, 'ARTICLE', 124, 1, 2, 0, 21, 8, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('이 식단 참고할게요!', 19, 'ARTICLE', 124, 3, 4, 0, 29, 8, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('질문 하나 드릴게요.', 27, 'ARTICLE', 124, 5, 6, 0, 13, 3, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('이 식단 참고할게요!', 20, 'ARTICLE', 125, 1, 2, 0, 0, 9, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('많은 도움이 되었어요.', 6, 'ARTICLE', 125, 3, 4, 0, 27, 3, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('댓글 남기고 갑니다~', 36, 'ARTICLE', 125, 5, 6, 0, 26, 4, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('정말 유익한 정보네요!', 33, 'ARTICLE', 126, 1, 2, 0, 17, 6, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('저도 같은 생각이에요.', 27, 'ARTICLE', 126, 3, 4, 0, 21, 2, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('궁금한 점이 있어요~', 3, 'ARTICLE', 126, 5, 6, 0, 25, 4, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('공감합니다!', 38, 'ARTICLE', 127, 1, 2, 0, 25, 5, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('이 식단 참고할게요!', 18, 'ARTICLE', 127, 3, 4, 0, 14, 10, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('좋은 글 감사합니다!', 5, 'ARTICLE', 127, 5, 6, 0, 27, 8, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('댓글 남기고 갑니다~', 6, 'ARTICLE', 128, 1, 2, 0, 12, 9, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('공감합니다!', 33, 'ARTICLE', 128, 3, 4, 0, 30, 6, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('정말 유익한 정보네요!', 12, 'ARTICLE', 128, 5, 6, 0, 30, 6, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('공감합니다!', 5, 'ARTICLE', 129, 1, 2, 0, 2, 0, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('많은 도움이 되었어요.', 21, 'ARTICLE', 129, 3, 4, 0, 13, 9, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('좋은 글 감사합니다!', 39, 'ARTICLE', 129, 5, 6, 0, 12, 8, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('많은 도움이 되었어요.', 21, 'ARTICLE', 130, 1, 2, 0, 14, 8, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('정말 유익한 정보네요!', 12, 'ARTICLE', 130, 3, 4, 0, 1, 1, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('정말 유익한 정보네요!', 35, 'ARTICLE', 130, 5, 6, 0, 10, 8, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('질문 하나 드릴게요.', 5, 'ARTICLE', 131, 1, 2, 0, 16, 6, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('많은 도움이 되었어요.', 39, 'ARTICLE', 131, 3, 4, 0, 9, 8, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('궁금한 점이 있어요~', 26, 'ARTICLE', 131, 5, 6, 0, 26, 10, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('좋은 글 감사합니다!', 35, 'ARTICLE', 132, 1, 2, 0, 9, 7, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('저도 같은 생각이에요.', 16, 'ARTICLE', 132, 3, 4, 0, 24, 9, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('이 식단 참고할게요!', 20, 'ARTICLE', 132, 5, 6, 0, 6, 8, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('궁금한 점이 있어요~', 19, 'ARTICLE', 133, 1, 2, 0, 23, 8, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('좋은 글 감사합니다!', 27, 'ARTICLE', 133, 3, 4, 0, 17, 7, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('공감합니다!', 37, 'ARTICLE', 133, 5, 6, 0, 29, 2, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('댓글 남기고 갑니다~', 40, 'ARTICLE', 134, 1, 2, 0, 26, 6, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('이 식단 참고할게요!', 23, 'ARTICLE', 134, 3, 4, 0, 2, 6, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('댓글 남기고 갑니다~', 16, 'ARTICLE', 134, 5, 6, 0, 6, 6, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('궁금한 점이 있어요~', 20, 'ARTICLE', 135, 1, 2, 0, 6, 6, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('좋은 글 감사합니다!', 9, 'ARTICLE', 135, 3, 4, 0, 29, 10, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('좋은 글 감사합니다!', 16, 'ARTICLE', 135, 5, 6, 0, 0, 8, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('이 식단 참고할게요!', 36, 'ARTICLE', 136, 1, 2, 0, 18, 8, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('궁금한 점이 있어요~', 12, 'ARTICLE', 136, 3, 4, 0, 21, 2, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('질문 하나 드릴게요.', 17, 'ARTICLE', 136, 5, 6, 0, 13, 5, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('정말 유익한 정보네요!', 30, 'ARTICLE', 137, 1, 2, 0, 19, 8, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('궁금한 점이 있어요~', 9, 'ARTICLE', 137, 3, 4, 0, 2, 3, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('정말 유익한 정보네요!', 39, 'ARTICLE', 137, 5, 6, 0, 28, 0, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('저도 같은 생각이에요.', 27, 'ARTICLE', 138, 1, 2, 0, 11, 6, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('질문 하나 드릴게요.', 15, 'ARTICLE', 138, 3, 4, 0, 22, 8, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('궁금한 점이 있어요~', 3, 'ARTICLE', 138, 5, 6, 0, 7, 0, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('저도 같은 생각이에요.', 7, 'ARTICLE', 139, 1, 2, 0, 22, 7, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('댓글 남기고 갑니다~', 23, 'ARTICLE', 139, 3, 4, 0, 28, 5, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('정말 유익한 정보네요!', 11, 'ARTICLE', 139, 5, 6, 0, 5, 10, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('좋은 글 감사합니다!', 25, 'ARTICLE', 140, 1, 2, 0, 16, 10, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('이 식단 참고할게요!', 12, 'ARTICLE', 140, 3, 4, 0, 16, 3, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('댓글 남기고 갑니다~', 20, 'ARTICLE', 140, 5, 6, 0, 28, 10, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('정말 유익한 정보네요!', 23, 'ARTICLE', 141, 1, 2, 0, 9, 3, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('많은 도움이 되었어요.', 16, 'ARTICLE', 141, 3, 4, 0, 8, 1, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('궁금한 점이 있어요~', 4, 'ARTICLE', 141, 5, 6, 0, 26, 5, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('정말 유익한 정보네요!', 29, 'ARTICLE', 142, 1, 2, 0, 27, 6, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('많은 도움이 되었어요.', 33, 'ARTICLE', 142, 3, 4, 0, 4, 1, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('저도 같은 생각이에요.', 28, 'ARTICLE', 142, 5, 6, 0, 21, 10, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('저도 같은 생각이에요.', 23, 'ARTICLE', 143, 1, 2, 0, 29, 4, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('공감합니다!', 37, 'ARTICLE', 143, 3, 4, 0, 5, 10, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('궁금한 점이 있어요~', 41, 'ARTICLE', 143, 5, 6, 0, 17, 10, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('질문 하나 드릴게요.', 28, 'ARTICLE', 144, 1, 2, 0, 5, 2, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('공감합니다!', 13, 'ARTICLE', 144, 3, 4, 0, 24, 8, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('좋은 글 감사합니다!', 24, 'ARTICLE', 144, 5, 6, 0, 2, 8, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('댓글 남기고 갑니다~', 36, 'ARTICLE', 145, 1, 2, 0, 24, 7, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('궁금한 점이 있어요~', 10, 'ARTICLE', 145, 3, 4, 0, 19, 1, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('댓글 남기고 갑니다~', 2, 'ARTICLE', 145, 5, 6, 0, 27, 1, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('공감합니다!', 40, 'ARTICLE', 146, 1, 2, 0, 0, 10, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('좋은 글 감사합니다!', 26, 'ARTICLE', 146, 3, 4, 0, 11, 5, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('정말 유익한 정보네요!', 3, 'ARTICLE', 146, 5, 6, 0, 11, 5, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('질문 하나 드릴게요.', 10, 'ARTICLE', 147, 1, 2, 0, 28, 10, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('많은 도움이 되었어요.', 37, 'ARTICLE', 147, 3, 4, 0, 23, 6, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('많은 도움이 되었어요.', 20, 'ARTICLE', 147, 5, 6, 0, 18, 10, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('궁금한 점이 있어요~', 23, 'ARTICLE', 148, 1, 2, 0, 23, 0, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('많은 도움이 되었어요.', 1, 'ARTICLE', 148, 3, 4, 0, 26, 8, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('저도 같은 생각이에요.', 28, 'ARTICLE', 148, 5, 6, 0, 27, 5, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('좋은 글 감사합니다!', 39, 'ARTICLE', 149, 1, 2, 0, 23, 4, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('정말 유익한 정보네요!', 2, 'ARTICLE', 149, 3, 4, 0, 20, 7, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('많은 도움이 되었어요.', 25, 'ARTICLE', 149, 5, 6, 0, 11, 4, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('질문 하나 드릴게요.', 31, 'ARTICLE', 150, 1, 2, 0, 27, 4, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('많은 도움이 되었어요.', 39, 'ARTICLE', 150, 3, 4, 0, 5, 9, '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
('좋은 글 감사합니다!', 23, 'ARTICLE', 150, 5, 6, 0, 23, 5, '2024-04-01 12:00:00', '2024-04-01 12:00:00');




-- ---------------------------------------------------------------------
-- `content_like` >>> ENUM 정보
-- ---------------------------------------------------------------------
CREATE TABLE like_type (
    type_code    VARCHAR(10) PRIMARY KEY,      -- 'LIKE', 'DISLIKE'
    description  VARCHAR(50) NOT NULL
);

INSERT INTO like_type VALUES
('LIKE', '좋아요'),
('DISLIKE', '싫어요');


CREATE TABLE content_type (
    type_code    VARCHAR(20) PRIMARY KEY,       -- 'ARTICLE', 'COMMENT'
    description  VARCHAR(50) NOT NULL
);

INSERT INTO content_type VALUES
('ARTICLE', '게시글'),
('COMMENT', '댓글'),
('MEAL', '식단');

-- ---------------------------------------------------------------------
-- Table `content_like` <<< (모든 좋아요/싫어요 통합 관리)
-- ---------------------------------------------------------------------
CREATE TABLE content_like (
    content_like_id  BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY, -- 고유 ID
    user_id          BIGINT UNSIGNED NOT NULL,                   -- 누가 눌렀는가
    content_type     VARCHAR(20) NOT NULL,                       -- 대상 종류 (게시글/댓글)
    content_id       INT NOT NULL,                               -- 대상 ID (글 ID 또는 댓글 ID)
    like_type        VARCHAR(10) NOT NULL,                       -- 'LIKE' 또는 'DISLIKE'
    created_at       DATETIME DEFAULT CURRENT_TIMESTAMP,         -- 눌린 시간

    -- 외래키 설정
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (content_type) REFERENCES content_type(type_code),
    FOREIGN KEY (like_type) REFERENCES like_type(type_code),

    -- 중복 방지: 한 사람이 하나의 컨텐츠에 하나의 반응만
    UNIQUE (user_id, content_type, content_id)
) ENGINE = InnoDB;




-- ---------------------------------------------------------------------
-- Table `food_info` <<< 음식 영양 정보
-- ---------------------------------------------------------------------
CREATE TABLE food_info (
    food_code VARCHAR(20) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    calories FLOAT NOT NULL,
    carbohydrates FLOAT,
    protein FLOAT,
    fat FLOAT,
    sugars FLOAT,
    sodium FLOAT
);
