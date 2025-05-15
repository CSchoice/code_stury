# 데이터베이스 스키마 설계

## 기본 엔티티 테이블

### 1. User (사용자) 테이블
```sql
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    nickname VARCHAR(50) NOT NULL,
    tier_baekjoon VARCHAR(20),
    level_programmers INT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

### 2. Problem (문제) 테이블
```sql
CREATE TABLE problems (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    site VARCHAR(50) NOT NULL,        -- 백준, 프로그래머스, 코드트리 등
    title VARCHAR(255) NOT NULL,
    category VARCHAR(100),            -- DP, 그래프, 정렬 등
    level VARCHAR(50),                -- 난이도
    url VARCHAR(500) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

### 3. ReviewRequest (코드 리뷰 요청) 테이블
```sql
CREATE TABLE review_requests (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    language VARCHAR(50) NOT NULL,    -- 프로그래밍 언어
    code TEXT NOT NULL,               -- 리뷰 요청 코드
    requested_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',  -- PENDING, COMPLETED, FAILED 등
    FOREIGN KEY (user_id) REFERENCES users(id)
);
```

### 4. Quiz (퀴즈) 테이블
```sql
CREATE TABLE quizzes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    source_doc TEXT,                  -- 원본 문서 (텍스트 또는 PDF 경로)
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)
);
```

### 5. Question (문제) 테이블
```sql
CREATE TABLE questions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    quiz_id BIGINT NOT NULL,
    type VARCHAR(50) NOT NULL,        -- 객관식, 주관식 등
    prompt TEXT NOT NULL,             -- 문제 내용
    options JSON,                     -- 객관식 옵션 (JSON 형태로 저장)
    answer TEXT NOT NULL,             -- 정답
    FOREIGN KEY (quiz_id) REFERENCES quizzes(id)
);
```

## 사용자 상태 관리 테이블

### 1. UserSession (사용자 세션) 테이블
```sql
CREATE TABLE user_sessions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    session_token VARCHAR(255) NOT NULL,
    login_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_activity_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ip_address VARCHAR(50),
    device_info VARCHAR(255),
    is_active BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (user_id) REFERENCES users(id),
    INDEX (session_token)
);
```

### 2. UserActivity (사용자 활동) 테이블
```sql
CREATE TABLE user_activities (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    activity_type VARCHAR(50) NOT NULL,  -- CODE_REVIEW, IDE_USAGE, PROBLEM_SOLVED, QUIZ_TAKEN 등
    activity_data JSON,                  -- 활동 관련 세부 데이터
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id),
    INDEX (user_id, activity_type)
);
```

### 3. UserProgress (사용자 학습 진행) 테이블
```sql
CREATE TABLE user_progress (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    category VARCHAR(50) NOT NULL,        -- ALGORITHM, GIT, SPRING_BOOT 등
    level INT DEFAULT 1,                  -- 현재 레벨
    experience_points INT DEFAULT 0,      -- 경험치
    completed_items INT DEFAULT 0,        -- 완료한 항목 수
    last_updated TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id),
    UNIQUE KEY (user_id, category)
);
```

### 4. UserPreference (사용자 설정) 테이블
```sql
CREATE TABLE user_preferences (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    theme VARCHAR(20) DEFAULT 'light',    -- light, dark 등
    language_preference VARCHAR(50) DEFAULT 'java',  -- 선호 프로그래밍 언어
    notification_enabled BOOLEAN DEFAULT TRUE,
    email_notification BOOLEAN DEFAULT TRUE,
    ide_settings JSON,                    -- IDE 관련 설정 (자동완성 등)
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id),
    UNIQUE KEY (user_id)
);
```

### 5. UserAchievement (사용자 업적) 테이블
```sql
CREATE TABLE user_achievements (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    achievement_id INT NOT NULL,
    achieved_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id),
    UNIQUE KEY (user_id, achievement_id)
);
```

### 6. Achievement (업적) 테이블
```sql
CREATE TABLE achievements (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT NOT NULL,
    category VARCHAR(50) NOT NULL,
    points INT DEFAULT 10,
    icon_url VARCHAR(255)
);
```

### 7. UserStatistics (사용자 통계) 테이블
```sql
CREATE TABLE user_statistics (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    problems_solved INT DEFAULT 0,
    code_reviews_requested INT DEFAULT 0,
    quizzes_taken INT DEFAULT 0,
    git_exercises_completed INT DEFAULT 0,
    optimization_exercises_completed INT DEFAULT 0,
    total_time_spent_minutes INT DEFAULT 0,
    last_updated TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id),
    UNIQUE KEY (user_id)
);
```

### 8. UserStudyPlan (사용자 학습 계획) 테이블
```sql
CREATE TABLE user_study_plans (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    start_date DATE,
    end_date DATE,
    status VARCHAR(20) DEFAULT 'ACTIVE',  -- ACTIVE, COMPLETED, ABANDONED
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)
);
```

### 9. StudyPlanItem (학습 계획 항목) 테이블
```sql
CREATE TABLE study_plan_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    plan_id BIGINT NOT NULL,
    title VARCHAR(255) NOT NULL,
    type VARCHAR(50) NOT NULL,            -- PROBLEM, QUIZ, GIT, OPTIMIZATION 등
    reference_id BIGINT,                  -- 관련 문제/퀴즈 ID
    order_index INT NOT NULL,
    is_completed BOOLEAN DEFAULT FALSE,
    completed_at TIMESTAMP NULL,
    FOREIGN KEY (plan_id) REFERENCES user_study_plans(id)
);
```

## 추가 기능 테이블

### 1. Git 연습 관련 테이블
```sql
CREATE TABLE git_practices (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    scenario_id INT NOT NULL,
    progress JSON,                    -- 진행 상황 저장
    completed BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)
);
```

### 2. Spring Boot 코드 최적화 연습 테이블
```sql
CREATE TABLE optimization_practices (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    problem_id INT NOT NULL,
    original_code TEXT,
    optimized_code TEXT,
    performance_gain FLOAT,           -- 성능 향상 비율
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)
);
```

### 3. OAuth2 인증 관련 테이블
```sql
CREATE TABLE oauth2_authorizations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    provider VARCHAR(20) NOT NULL,    -- GOOGLE 등
    provider_id VARCHAR(255) NOT NULL,
    access_token TEXT,
    refresh_token TEXT,
    expires_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id),
    UNIQUE KEY (provider, provider_id)
);
```

## 인덱스 설정
```sql
-- 성능 최적화를 위한 인덱스
CREATE INDEX idx_review_requests_user_id ON review_requests(user_id);
CREATE INDEX idx_review_requests_status ON review_requests(status);
CREATE INDEX idx_problems_site ON problems(site);
CREATE INDEX idx_problems_category ON problems(category);
CREATE INDEX idx_problems_level ON problems(level);
CREATE INDEX idx_quizzes_user_id ON quizzes(user_id);
CREATE INDEX idx_questions_quiz_id ON questions(quiz_id);
```

## ER 다이어그램

```
users ──────┐
            │
            ├─── review_requests
            │
            ├─── quizzes ─── questions
            │
            ├─── user_sessions
            │
            ├─── user_activities
            │
            ├─── user_progress
            │
            ├─── user_preferences
            │
            ├─── user_achievements ─── achievements
            │
            ├─── user_statistics
            │
            ├─── user_study_plans ─── study_plan_items
            │
            ├─── git_practices
            │
            ├─── optimization_practices
            │
            └─── oauth2_authorizations
```

## 마이그레이션 전략

1. 초기 스키마 생성 (V1)
   - 기본 엔티티 테이블 생성
   - 인덱스 설정

2. 사용자 상태 관리 테이블 추가 (V2)
   - 세션, 활동, 진행 상황 등 테이블 추가

3. 추가 기능 테이블 추가 (V3)
   - Git 연습, 코드 최적화 등 테이블 추가

4. 인증 관련 테이블 추가 (V4)
   - OAuth2 인증 관련 테이블 추가
