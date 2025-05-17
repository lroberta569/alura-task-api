CREATE TABLE task_option (
    id BIGINT NOT NULL AUTO_INCREMENT,
    is_correct BOOLEAN NOT NULL,
    option_text VARCHAR(80) NOT NULL,
    task_id BIGINT NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_task_option_task FOREIGN KEY (task_id) REFERENCES task(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

