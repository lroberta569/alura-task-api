CREATE TABLE Task (
    id bigint(20) NOT NULL AUTO_INCREMENT,
    statement varchar(255) NOT NULL,
    `order` int NOT NULL CHECK (`order` >= 1),
    course_id bigint(20) NOT NULL,
    createdAt datetime DEFAULT CURRENT_TIMESTAMP,
    type enum('OPEN_TEXT', 'SINGLE_CHOICE', 'MULTIPLE_CHOICE') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    task_type varchar(31) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT FK_Course_Task FOREIGN KEY (course_id) REFERENCES Course(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC;
