CREATE TABLE `member` (
                          `member_id` BIGINT NOT NULL AUTO_INCREMENT,
                          `login_id` VARCHAR(16) NOT NULL UNIQUE,
                          `password` VARCHAR(255) NOT NULL,
                          `name` VARCHAR(16) NOT NULL,
                          `hospital` VARCHAR(50) NULL,
                          `ward` VARCHAR(50) NULL,
                          `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                          `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                          `app_fcm_token` VARCHAR(255) NULL,
                          `watch_fcm_token` VARCHAR(255) NULL,
                          PRIMARY KEY (`member_id`)
);

CREATE TABLE `group` (
                         `group_id` BIGINT NOT NULL AUTO_INCREMENT,
                         `group_name` VARCHAR(50) NOT NULL,
                         `group_invite_link` VARCHAR(50) NULL,
                         `group_member_count` INT NULL,
                         `group_icon_type` INT NULL,
                         `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
                         `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                         PRIMARY KEY (`group_id`)
);

CREATE TABLE `personal_schedule` (
                                     `schedule_id` BIGINT NOT NULL AUTO_INCREMENT,
                                     `member_id` BIGINT NOT NULL,
                                     `is_public` BOOLEAN DEFAULT FALSE,
                                     `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
                                     `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                     PRIMARY KEY (`schedule_id`),
                                     FOREIGN KEY (`member_id`) REFERENCES `member`(`member_id`)
);

CREATE TABLE `group_schedule` (
                                  `work_schedule_detail_id` BIGINT NOT NULL AUTO_INCREMENT,
                                  `group_id` BIGINT NOT NULL,
                                  `schedule_month` VARCHAR(10) NULL,
                                  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
                                  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                  PRIMARY KEY (`work_schedule_detail_id`),
                                  FOREIGN KEY (`group_id`) REFERENCES `group`(`group_id`)
);

CREATE TABLE `group_participation` (
                                       `group_participation_id` BIGINT NOT NULL AUTO_INCREMENT,
                                       `member_id` BIGINT NOT NULL,
                                       `group_id` BIGINT NOT NULL,
                                       `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
                                       `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                       PRIMARY KEY (`group_participation_id`),
                                       FOREIGN KEY (`member_id`) REFERENCES `member`(`member_id`),
                                       FOREIGN KEY (`group_id`) REFERENCES `group`(`group_id`)
);

CREATE TABLE `friend` (
                          `friend_id` BIGINT NOT NULL AUTO_INCREMENT,
                          `member_id` BIGINT NOT NULL,
                          `friend_login_id` VARCHAR(255) NOT NULL,
                          `request_status` ENUM('PENDING', 'ACCEPTED') NOT NULL,
                          `is_favorite` BOOLEAN DEFAULT FALSE,
                          `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
                          `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                          PRIMARY KEY (`friend_id`),
                          FOREIGN KEY (`member_id`) REFERENCES `member`(`member_id`)
);

CREATE TABLE `work_schedule` (
                                 `work_schedule_id` BIGINT NOT NULL AUTO_INCREMENT,
                                 `member_id` BIGINT NOT NULL,
                                 `work_schedule_detail_id` BIGINT NULL,
                                 `work_date` DATETIME NULL,
                                 `work_type` ENUM('D', 'N', 'E', 'OF') NULL,
                                 `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
                                 `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                 PRIMARY KEY (`work_schedule_id`),
                                 FOREIGN KEY (`member_id`) REFERENCES `member`(`member_id`),
                                 FOREIGN KEY (`work_schedule_detail_id`) REFERENCES `group_schedule`(`work_schedule_detail_id`)
);

CREATE TABLE `schedule_detail` (
                                   `detail_id` BIGINT NOT NULL AUTO_INCREMENT,
                                   `schedule_id` BIGINT NOT NULL,
                                   `start_dt` DATETIME NOT NULL,
                                   `end_dt` DATETIME NULL,
                                   `schedule_title` VARCHAR(50) NULL,
                                   `schedule_color` VARCHAR(20) NULL,
                                   `is_time_set` BOOLEAN DEFAULT FALSE,
                                   `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
                                   `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                   PRIMARY KEY (`detail_id`),
                                   FOREIGN KEY (`schedule_id`) REFERENCES `personal_schedule`(`schedule_id`)
);




-- 인덱스 추가
CREATE INDEX idx_member_login_id ON member(login_id);
CREATE INDEX idx_group_name ON `group`(group_name);
CREATE INDEX idx_work_schedule_date ON work_schedule(work_date);


-- 근무스케줄
INSERT INTO member (login_id, password, name, hospital, ward, created_at, updated_at, app_fcm_token, watch_fcm_token)
VALUES ('testuser', 'testpassword', 'Test User', 'Test Hospital', 'General', NOW(), NOW(), NULL, NULL);

INSERT INTO work_schedule (member_id, work_date, work_type, created_at, updated_at)
VALUES 
(1, '2025-02-01 00:00:00', 'D', NOW(), NOW()),
(1, '2025-02-02 00:00:00', 'N', NOW(), NOW()),
(1, '2025-02-03 00:00:00', 'E', NOW(), NOW()),
(1, '2025-02-04 00:00:00', 'OF', NOW(), NOW()),
(1, '2025-02-05 00:00:00', 'D', NOW(), NOW()),
(1, '2025-02-06 00:00:00', 'N', NOW(), NOW()),
(1, '2025-02-07 00:00:00', 'E', NOW(), NOW()),
(1, '2025-02-08 00:00:00', 'OF', NOW(), NOW()),
(1, '2025-02-09 00:00:00', 'D', NOW(), NOW()),
(1, '2025-02-10 00:00:00', 'N', NOW(), NOW()),
(1, '2025-02-11 00:00:00', 'E', NOW(), NOW()),
(1, '2025-02-12 00:00:00', 'OF', NOW(), NOW()),
(1, '2025-02-13 00:00:00', 'D', NOW(), NOW()),
(1, '2025-02-14 00:00:00', 'N', NOW(), NOW()),
(1, '2025-02-15 00:00:00', 'E', NOW(), NOW()),
(1, '2025-02-16 00:00:00', 'OF', NOW(), NOW()),
(1, '2025-02-17 00:00:00', 'D', NOW(), NOW()),
(1, '2025-02-18 00:00:00', 'N', NOW(), NOW()),
(1, '2025-02-19 00:00:00', 'E', NOW(), NOW()),
(1, '2025-02-20 00:00:00', 'OF', NOW(), NOW()),
(1, '2025-02-21 00:00:00', 'D', NOW(), NOW()),
(1, '2025-02-22 00:00:00', 'N', NOW(), NOW()),
(1, '2025-02-23 00:00:00', 'E', NOW(), NOW()),
(1, '2025-02-24 00:00:00', 'OF', NOW(), NOW()),
(1, '2025-02-25 00:00:00', 'D', NOW(), NOW()),
(1, '2025-02-26 00:00:00', 'N', NOW(), NOW()),
(1, '2025-02-27 00:00:00', 'E', NOW(), NOW()),
(1, '2025-02-28 00:00:00', 'OF', NOW(), NOW());

