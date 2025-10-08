CREATE TABLE groups (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    code VARCHAR(255) NOT NULL UNIQUE,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    max_capacity INTEGER NOT NULL,
    description VARCHAR(500)
);

CREATE TABLE group_students (
    group_id BIGINT NOT NULL REFERENCES groups(id) ON DELETE CASCADE,
    student_id BIGINT NOT NULL REFERENCES students(id) ON DELETE CASCADE,
    PRIMARY KEY (group_id, student_id)
);

CREATE INDEX idx_group_students_student_id ON group_students(student_id);
CREATE INDEX idx_group_students_group_id ON group_students(group_id);
