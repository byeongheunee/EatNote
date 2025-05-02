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


INSERT INTO users (
  email, password, name, nickname, gender, user_type, status
) VALUES (
  'test@example.com', 'encoded_password', '테스트유저', 'testuser', 'M', 2, 'ACTIVE'
);

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
(1, 1),
(1, 13),
(1, 21);





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
    meal_time       DATETIME DEFAULT CURRENT_TIMESTAMP,   -- 식사 시각
    created_at      DATETIME DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);



-- ---------------------------------------------------------------------
-- Table `meal_feedback` <<< 트레이너 피드백
-- ---------------------------------------------------------------------
CREATE TABLE meal_feedback (
    feedback_id   BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    meal_id       BIGINT UNSIGNED NOT NULL,         -- 피드백 대상 식단
    trainer_id    BIGINT UNSIGNED NOT NULL,         -- 피드백을 남긴 트레이너
    content       TEXT,                             -- 피드백 내용
    created_at    DATETIME DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (meal_id) REFERENCES meal(meal_id) ON DELETE CASCADE,
    FOREIGN KEY (trainer_id) REFERENCES users(user_id) ON DELETE CASCADE
);





-- ---------------------------------------------------------------------
-- Table `notification` <<< 알림 기능
-- ---------------------------------------------------------------------
CREATE TABLE notification (
    notification_id  BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    user_id          BIGINT UNSIGNED NOT NULL,         -- 알림 받을 사용자 (주로 트레이너, 회원도 알림 받음)
    type             VARCHAR(50),                      -- 예: 'NEW_MEAL'
    content          VARCHAR(255),                     -- 간단한 메시지
    related_meal_id  BIGINT UNSIGNED DEFAULT NULL,     -- 관련 식단 ID
    is_read          BOOLEAN DEFAULT FALSE,
    created_at       DATETIME DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (user_id) REFERENCES users(user_id),
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
  created_at   DATETIME DEFAULT CURRENT_TIMESTAMP,         -- 생성일
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



-- ---------------------------------------------------------------------
-- Table `comment`
-- ---------------------------------------------------------------------
CREATE TABLE comment (
  comment_id   INT AUTO_INCREMENT PRIMARY KEY,
  content      VARCHAR(1000) NOT NULL,                 -- 댓글 내용
  user_id      BIGINT UNSIGNED NOT NULL,               -- 작성자
  article_id   INT NOT NULL,                           -- 게시글 ID
  lft          INT NOT NULL,                           -- 트리 왼쪽값
  rgt          INT NOT NULL,                           -- 트리 오른쪽값
  depth        INT NOT NULL,                           -- 댓글 깊이
  created_at   DATETIME DEFAULT CURRENT_TIMESTAMP,     -- 작성일
  updated_at   DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, -- 수정일

  FOREIGN KEY (user_id) REFERENCES users(user_id)
    ON DELETE CASCADE
    ON UPDATE CASCADE,

  FOREIGN KEY (article_id) REFERENCES article(article_id)
    ON DELETE CASCADE
    ON UPDATE CASCADE
) ENGINE = InnoDB;


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
('COMMENT', '댓글');

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






