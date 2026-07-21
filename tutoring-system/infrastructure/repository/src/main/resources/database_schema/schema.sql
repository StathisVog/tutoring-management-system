/* Users */
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    address VARCHAR(255),
    theme_color VARCHAR(30) DEFAULT 'theme-default',
    avatar_name VARCHAR(50) DEFAULT 'avatar-default.svg',
    role ENUM('STUDENT', 'TEACHER', 'ADMIN') NOT NULL,
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    security_question VARCHAR(255),
    security_answer_hash VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

/* Students */
CREATE TABLE IF NOT EXISTS students (
    id BIGINT PRIMARY KEY,
    age INT,
    school_class ENUM('A_GUMNASIOU', 'B_GUMNASIOU', 'C_GUMNASIOU', 'A_LUKEIOU', 'B_LUKEIOU', 'C_LUKEIOU') NOT NULL,
    parent_full_name VARCHAR(100),
    parent_tax_id VARCHAR(20),

    CONSTRAINT fk_student_user
        FOREIGN KEY (id) REFERENCES users(id)
            ON DELETE CASCADE
);

/* Teachers */
CREATE TABLE IF NOT EXISTS teachers (
    id BIGINT PRIMARY KEY,
    specialty VARCHAR(100),
    bio TEXT,

    CONSTRAINT fk_teacher_user
        FOREIGN KEY (id) REFERENCES users(id)
            ON DELETE CASCADE
);

/* Courses */
CREATE TABLE IF NOT EXISTS courses (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(100) NOT NULL,
    description TEXT,
    grade_level VARCHAR(40),
    active BOOLEAN NOT NULL DEFAULT TRUE
);

/* Teachers Many-to-Many Course */
CREATE TABLE IF NOT EXISTS teacher_course (
    teacher_id BIGINT,
    course_id BIGINT,

    PRIMARY KEY (teacher_id, course_id),

    FOREIGN KEY (teacher_id)
        REFERENCES teachers(id)
        ON DELETE CASCADE,

    FOREIGN KEY (course_id)
        REFERENCES courses(id)
        ON DELETE CASCADE
);

/* Teachers Many-to-Many Eligible Courses (Preferences) */
CREATE TABLE IF NOT EXISTS teacher_eligible_course (
    teacher_id BIGINT,
    course_id BIGINT,

    PRIMARY KEY (teacher_id, course_id),

    FOREIGN KEY (teacher_id)
        REFERENCES teachers(id)
        ON DELETE CASCADE,

    FOREIGN KEY (course_id)
        REFERENCES courses(id)
        ON DELETE CASCADE
);

/* Scheduled Slot */
CREATE TABLE IF NOT EXISTS scheduled_slots (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    course_id BIGINT NOT NULL,
    teacher_id BIGINT NOT NULL,
    day_of_week ENUM('MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY', 'SATURDAY', 'SUNDAY') NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    classroom VARCHAR(50),
    capacity INT NOT NULL CHECK (capacity > 0),

    FOREIGN KEY (course_id)
        REFERENCES courses(id)
        ON DELETE CASCADE,

    FOREIGN KEY (teacher_id)
        REFERENCES teachers(id)
        ON DELETE CASCADE,

    /* Prevent teacher double-booking */
    CONSTRAINT uq_teacher_schedule UNIQUE (teacher_id, day_of_week, start_time)
);

/* Enrollments */
CREATE TABLE IF NOT EXISTS enrollments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    student_id BIGINT NOT NULL,
    scheduled_slot_id BIGINT NOT NULL,
    enrollment_date DATE,
    status ENUM('PENDING_ENROLL', 'ACTIVE', 'PENDING_DROP', 'DROPPED') NOT NULL,

    FOREIGN KEY (student_id)
        REFERENCES students(id)
        ON DELETE CASCADE,

    FOREIGN KEY (scheduled_slot_id)
        REFERENCES scheduled_slots(id)
        ON DELETE CASCADE
);

/* Teacher Absences */
CREATE TABLE IF NOT EXISTS teacher_absences (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    teacher_id BIGINT NOT NULL,
    date DATE NOT NULL,
    reason VARCHAR(255),
    scheduled_slot_id BIGINT,

    FOREIGN KEY (teacher_id)
        REFERENCES teachers(id)
        ON DELETE CASCADE,

    FOREIGN KEY (scheduled_slot_id)
        REFERENCES scheduled_slots(id)
        ON DELETE CASCADE,

    CONSTRAINT uq_teacher_absence UNIQUE (teacher_id, date, scheduled_slot_id)
);

/* Lesson Activities */
CREATE TABLE IF NOT EXISTS lesson_activities (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    scheduled_slot_id BIGINT NOT NULL,
    date DATE NOT NULL,
    description TEXT,

    FOREIGN KEY (scheduled_slot_id)
        REFERENCES scheduled_slots(id)
        ON DELETE CASCADE
);

/* Tests */
CREATE TABLE IF NOT EXISTS tests (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    course_id BIGINT NOT NULL,
    teacher_id BIGINT NOT NULL,
    scheduled_slot_id BIGINT NOT NULL,
    date DATE NOT NULL,
    description VARCHAR(255),

    FOREIGN KEY (course_id)
        REFERENCES courses(id)
        ON DELETE CASCADE,

    FOREIGN KEY (teacher_id)
        REFERENCES teachers(id)
        ON DELETE CASCADE,

    FOREIGN KEY (scheduled_slot_id)
        REFERENCES scheduled_slots(id)
        ON DELETE CASCADE,

    /* Prevent assigning the exact same test to the same course on the same day */
    CONSTRAINT uq_test_course_date UNIQUE (course_id, date, scheduled_slot_id)
);

/* Test Results */
CREATE TABLE IF NOT EXISTS test_results (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    test_id BIGINT NOT NULL,
    student_id BIGINT NOT NULL,
    grade DECIMAL(4,2) CHECK (grade >= 0.00 AND grade <= 20.00),
    comments VARCHAR(500),

    FOREIGN KEY (test_id)
        REFERENCES tests(id)
        ON DELETE CASCADE,

    FOREIGN KEY (student_id)
        REFERENCES students(id)
        ON DELETE CASCADE,

    /* Prevent duplicate results per student per test */
    CONSTRAINT uq_test_result UNIQUE (test_id, student_id)
);

/* Indexes (Performance) */

/* Speeds up queries like: */

/* SELECT * FROM enrollments WHERE student_id = ?; */
CREATE INDEX idx_enrollment_student ON enrollments(student_id);

/* SELECT * FROM enrollments WHERE scheduled_slot_id = ?; */
CREATE INDEX idx_enrollment_slot ON enrollments(scheduled_slot_id);

/* SELECT * FROM enrollments WHERE status IN ('ACTIVE', 'PENDING_DROP') */
CREATE INDEX idx_enrollment_status ON enrollments(status);

/* SELECT * FROM test_results WHERE student_id = ?; */
CREATE INDEX idx_test_results_student ON test_results(student_id);

/* SELECT * FROM test_results WHERE test_id = ?; */
CREATE INDEX idx_test_results_test ON test_results(test_id);

/* SELECT * FROM tests WHERE teacher_id = ?; */
CREATE INDEX idx_test_teacher ON tests(teacher_id);

/* SELECT * FROM tests WHERE scheduled_slot_id = ?; */
CREATE INDEX idx_test_slot ON tests(scheduled_slot_id);