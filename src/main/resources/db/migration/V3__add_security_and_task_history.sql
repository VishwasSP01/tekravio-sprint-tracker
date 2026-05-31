CREATE TABLE app_users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(32) NOT NULL,
    engineer_id BIGINT,
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT fk_user_engineer FOREIGN KEY (engineer_id) REFERENCES engineers (id) ON DELETE SET NULL,
    CONSTRAINT uk_user_engineer UNIQUE (engineer_id)
);

CREATE TABLE task_status_history (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    task_id BIGINT NOT NULL,
    previous_status VARCHAR(32) NOT NULL,
    new_status VARCHAR(32) NOT NULL,
    changed_at TIMESTAMP NOT NULL,
    changed_by VARCHAR(255) NOT NULL,
    CONSTRAINT fk_history_task FOREIGN KEY (task_id) REFERENCES tasks (id) ON DELETE CASCADE
);

CREATE INDEX idx_history_task_changed_at ON task_status_history (task_id, changed_at);
