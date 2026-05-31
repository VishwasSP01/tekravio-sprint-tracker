CREATE TABLE clients (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    industry VARCHAR(255) NOT NULL,
    contact_email VARCHAR(255) NOT NULL,
    country VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    deleted BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE TABLE engineers (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    primary_stack VARCHAR(32) NOT NULL,
    experience_years INTEGER NOT NULL,
    available BOOLEAN NOT NULL
);

CREATE TABLE projects (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(2000) NOT NULL,
    status VARCHAR(32) NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE,
    client_id BIGINT NOT NULL,
    CONSTRAINT fk_project_client FOREIGN KEY (client_id) REFERENCES clients (id)
);

CREATE TABLE sprints (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    sprint_number INTEGER NOT NULL,
    goal VARCHAR(1000) NOT NULL,
    status VARCHAR(32) NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    project_id BIGINT NOT NULL,
    CONSTRAINT fk_sprint_project FOREIGN KEY (project_id) REFERENCES projects (id),
    CONSTRAINT uk_sprint_project_number UNIQUE (project_id, sprint_number)
);

CREATE TABLE tasks (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description VARCHAR(2000) NOT NULL,
    priority VARCHAR(32) NOT NULL,
    status VARCHAR(32) NOT NULL,
    estimated_hours DECIMAL(8, 2) NOT NULL,
    actual_hours DECIMAL(8, 2),
    sprint_id BIGINT NOT NULL,
    assigned_engineer_id BIGINT,
    CONSTRAINT fk_task_sprint FOREIGN KEY (sprint_id) REFERENCES sprints (id),
    CONSTRAINT fk_task_engineer FOREIGN KEY (assigned_engineer_id) REFERENCES engineers (id)
);

CREATE INDEX idx_project_client ON projects (client_id);
CREATE INDEX idx_sprint_project ON sprints (project_id);
CREATE INDEX idx_task_sprint ON tasks (sprint_id);
CREATE INDEX idx_task_engineer ON tasks (assigned_engineer_id);
CREATE INDEX idx_task_filters ON tasks (status, priority, sprint_id);
