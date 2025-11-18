-- 01-init.sql
-- =================================================
-- Create extensions
-- =================================================
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- =================================================
-- Drop tables and sequences (for a clean reset)
-- =================================================
DROP TABLE IF EXISTS lessons CASCADE;
DROP TABLE IF EXISTS class_schedules CASCADE;
DROP TABLE IF EXISTS class_students CASCADE;
DROP TABLE IF EXISTS class_teachers CASCADE;
DROP TABLE IF EXISTS school_classes CASCADE;
DROP TABLE IF EXISTS student_parents CASCADE;
DROP TABLE IF EXISTS menu_item_roles CASCADE;
DROP TABLE IF EXISTS menu_items CASCADE;
DROP TABLE IF EXISTS refresh_tokens CASCADE;
DROP TABLE IF EXISTS user_settings CASCADE;
DROP TABLE IF EXISTS parents CASCADE;
DROP TABLE IF EXISTS students CASCADE;
DROP TABLE IF EXISTS teachers CASCADE;
DROP TABLE IF EXISTS canteens CASCADE;
DROP TABLE IF EXISTS schools CASCADE;
DROP TABLE IF EXISTS users CASCADE;

DROP SEQUENCE IF EXISTS users_seq;
DROP SEQUENCE IF EXISTS refresh_tokens_seq;
DROP SEQUENCE IF EXISTS menu_items_seq;
DROP SEQUENCE IF EXISTS schools_seq;
DROP SEQUENCE IF EXISTS students_seq;
DROP SEQUENCE IF EXISTS teachers_seq;
DROP SEQUENCE IF EXISTS parents_seq;
DROP SEQUENCE IF EXISTS canteens_seq;
DROP SEQUENCE IF EXISTS school_classes_seq;
DROP SEQUENCE IF EXISTS class_schedules_seq;
DROP SEQUENCE IF EXISTS lessons_seq;

-- =================================================
-- Create sequences
-- =================================================
CREATE SEQUENCE users_seq START 1 INCREMENT 1;
CREATE SEQUENCE refresh_tokens_seq START 1 INCREMENT 1;
CREATE SEQUENCE menu_items_seq START 1 INCREMENT 1;
CREATE SEQUENCE schools_seq START 1 INCREMENT 1;
CREATE SEQUENCE students_seq START 1 INCREMENT 1;
CREATE SEQUENCE teachers_seq START 1 INCREMENT 1;
CREATE SEQUENCE parents_seq START 1 INCREMENT 1;
CREATE SEQUENCE canteens_seq START 1 INCREMENT 1;
CREATE SEQUENCE school_types_seq START 1 INCREMENT 1;
CREATE SEQUENCE school_classes_seq START 1 INCREMENT 1;
CREATE SEQUENCE class_schedules_seq START 1 INCREMENT 1;
CREATE SEQUENCE lessons_seq START 1 INCREMENT 1;
CREATE SEQUENCE class_timestamps_seq START 1 INCREMENT 1;


-- =================================================
-- Schools Table
-- =================================================
CREATE TABLE schools (
    id BIGINT PRIMARY KEY DEFAULT nextval('schools_seq'),

    name VARCHAR(255) NOT NULL,
    code VARCHAR(50) UNIQUE NOT NULL,

    image VARCHAR(500),
    address TEXT,
    email VARCHAR(255),
    phone VARCHAR(20),

    owner_id BIGINT,

    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',

    school_type VARCHAR(20),
    shift_type VARCHAR(20),
    educational_level VARCHAR(50),
    isced_level VARCHAR(50),

    website VARCHAR(500),

    students_capacity INTEGER DEFAULT 0,
    established_year INTEGER,
    motto VARCHAR(500),

    total_classrooms INTEGER DEFAULT 0,
    total_labs INTEGER DEFAULT 0,

    has_transport_facility BOOLEAN DEFAULT false,
    has_hostel_facility BOOLEAN DEFAULT false,
    has_cafeteria BOOLEAN DEFAULT false,
    has_library BOOLEAN DEFAULT false,
    has_sports_facility BOOLEAN DEFAULT false,

    annual_tuition_fee NUMERIC(10,2),
    accreditation VARCHAR(255),

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (owner_id) REFERENCES users(id) ON DELETE SET NULL
);

-- =================================================
-- School Tags Table
-- =================================================
CREATE TABLE school_tags (
    school_id BIGINT NOT NULL,
    tag VARCHAR(255) NOT NULL,
    FOREIGN KEY (school_id) REFERENCES schools(id) ON DELETE CASCADE
);

-- =================================================
-- Users Table
-- =================================================
CREATE TABLE users (
    id BIGINT PRIMARY KEY DEFAULT nextval('users_seq'),
    user_id UUID UNIQUE NOT NULL DEFAULT uuid_generate_v4(),
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    phone_number VARCHAR(20) UNIQUE NOT NULL,
    role VARCHAR(50) NOT NULL CHECK (role IN (
        'STUDENT', 'TEACHER', 'SCHOOL_MANAGER', 'PARENT', 'SCHOOL_ADMIN',
        'FINANCE_TEAM', 'CANTEEN_OPERATOR', 'OWNER'
    )),
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING' CHECK (status IN (
        'PENDING', 'ACTIVE', 'INACTIVE', 'SUSPENDED'
    )),
    is_active BOOLEAN DEFAULT true,
    email_verified BOOLEAN DEFAULT false,
    school_id BIGINT,
    last_login TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (school_id) REFERENCES schools(id) ON DELETE SET NULL
);

-- =================================================
-- Teachers Table (must be created before school_classes due to FK constraint)
-- =================================================
CREATE TABLE teachers (
    id BIGINT PRIMARY KEY DEFAULT nextval('teachers_seq'),
    user_id BIGINT UNIQUE,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    email VARCHAR(255),
    phone VARCHAR(20),
    subject_specialization VARCHAR(255),
    qualification VARCHAR(255),
    school_id BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL,
    FOREIGN KEY (school_id) REFERENCES schools(id) ON DELETE SET NULL
);

-- =================================================
-- Students Table
-- =================================================
CREATE TABLE students (
    id BIGINT PRIMARY KEY DEFAULT nextval('students_seq'),
    user_id BIGINT UNIQUE,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    student_id VARCHAR(50) UNIQUE,
    id_number VARCHAR(50) UNIQUE,
    birth_date DATE,
    gender VARCHAR(10),
    school_id BIGINT,
    emergency_contact VARCHAR(100),
    emergency_phone VARCHAR(20),
    medical_notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL,
    FOREIGN KEY (school_id) REFERENCES schools(id) ON DELETE SET NULL
);

-- =================================================
-- School Classes Table
-- =================================================
CREATE TABLE school_classes (
    id BIGINT PRIMARY KEY DEFAULT nextval('school_classes_seq'),
    name VARCHAR(255) NOT NULL,
    grade_level VARCHAR(100),
    academic_year VARCHAR(20) NOT NULL,
    capacity INTEGER DEFAULT 30,
    is_active BOOLEAN DEFAULT true,
    school_id BIGINT NOT NULL,
    main_teacher_id BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (school_id) REFERENCES schools(id) ON DELETE CASCADE,
    FOREIGN KEY (main_teacher_id) REFERENCES teachers(id) ON DELETE SET NULL
);

-- =================================================
-- Class Students (Many-to-Many)
-- =================================================
CREATE TABLE class_students (
    class_id BIGINT NOT NULL,
    student_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (class_id, student_id),
    FOREIGN KEY (class_id) REFERENCES school_classes(id) ON DELETE CASCADE,
    FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE
);

-- =================================================
-- Class Teachers (Many-to-Many)
-- =================================================
CREATE TABLE class_teachers (
    class_id BIGINT NOT NULL,
    teacher_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (class_id, teacher_id),
    FOREIGN KEY (class_id) REFERENCES school_classes(id) ON DELETE CASCADE,
    FOREIGN KEY (teacher_id) REFERENCES teachers(id) ON DELETE CASCADE
);

-- =================================================
-- Lessons Table (for predefined subjects by grade level)
-- =================================================
CREATE TABLE lessons (
    id BIGINT PRIMARY KEY DEFAULT nextval('lessons_seq'),
    name VARCHAR(255) NOT NULL,
    name_en VARCHAR(255),
    grade_level VARCHAR(50) NOT NULL CHECK (grade_level IN ('PRE_PRIMARY', 'PRIMARY', 'LOWER_SECONDARY', 'UPPER_SECONDARY', 'TERTIARY')),
    color VARCHAR(20),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- =================================================
-- Class Timestamps Table
-- =================================================
CREATE TABLE class_timestamps (
    id BIGINT PRIMARY KEY DEFAULT nextval('class_timestamps_seq'),
    school_id BIGINT NOT NULL,
    name VARCHAR(255) NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    type VARCHAR(50) NOT NULL CHECK (type IN (
        'REGULAR', 'BREAK', 'LUNCH', 'PRAYER', 'OTHER'
    )),
    order_index INTEGER NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT true,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (school_id) REFERENCES schools(id) ON DELETE CASCADE
);

-- =================================================
-- Class Schedules Table
-- =================================================
CREATE TABLE class_schedules (
    id BIGINT PRIMARY KEY DEFAULT nextval('class_schedules_seq'),
    class_id BIGINT NOT NULL,
    day_of_week VARCHAR(20) NOT NULL CHECK (day_of_week IN (
        'SUNDAY', 'MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY', 'SATURDAY'
    )),
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    subject_name VARCHAR(255) NOT NULL,
    teacher_id BIGINT,
    room_number VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (class_id) REFERENCES school_classes(id) ON DELETE CASCADE,
    FOREIGN KEY (teacher_id) REFERENCES teachers(id) ON DELETE SET NULL
);

-- =================================================
-- Parents Table
-- =================================================
CREATE TABLE parents (
    id BIGINT PRIMARY KEY DEFAULT nextval('parents_seq'),
    user_id BIGINT UNIQUE,
    phone VARCHAR(20),
    occupation VARCHAR(255),
    address VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL
);

-- =================================================
-- Student ? Parent many-to-many
-- =================================================
CREATE TABLE student_parents (
    student_id BIGINT NOT NULL,
    parent_id BIGINT NOT NULL,
    PRIMARY KEY (student_id, parent_id),
    FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE,
    FOREIGN KEY (parent_id) REFERENCES parents(id) ON DELETE CASCADE
);

-- =================================================
-- Canteens Table
-- =================================================
CREATE TABLE canteens (
    id BIGINT PRIMARY KEY DEFAULT nextval('canteens_seq'),
    name VARCHAR(255) NOT NULL,
    location VARCHAR(255),
    school_id BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (school_id) REFERENCES schools(id) ON DELETE SET NULL
);

-- =================================================
-- User Settings
-- =================================================
CREATE TABLE user_settings (
    id BIGINT PRIMARY KEY DEFAULT nextval('users_seq'),
    user_id BIGINT UNIQUE NOT NULL,
    preferred_language VARCHAR(10) DEFAULT 'en',
    font_size VARCHAR(10) DEFAULT 'medium',
    theme VARCHAR(10) DEFAULT 'light',
    primary_color VARCHAR(20) DEFAULT 'blue',
    notifications_enabled BOOLEAN DEFAULT true,
    email_notifications BOOLEAN DEFAULT true,
    sms_notifications BOOLEAN DEFAULT false,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- =================================================
-- Refresh Tokens
-- =================================================
CREATE TABLE refresh_tokens (
    id BIGINT PRIMARY KEY DEFAULT nextval('refresh_tokens_seq'),
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    token VARCHAR(500) UNIQUE NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    is_revoked BOOLEAN DEFAULT false,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

-- =================================================
-- Menu Items
-- =================================================
CREATE TABLE menu_items (
    id BIGINT PRIMARY KEY DEFAULT nextval('menu_items_seq'),
    title_key VARCHAR(100) NOT NULL,
    icon VARCHAR(50),
    route VARCHAR(200),
    path VARCHAR(200),
    order_index INTEGER NOT NULL DEFAULT 0,
    required_permission VARCHAR(100),
    parent_id BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    FOREIGN KEY (parent_id) REFERENCES menu_items(id) ON DELETE CASCADE
);

CREATE TABLE menu_item_roles (
    menu_item_id BIGINT NOT NULL,
    role VARCHAR(50) NOT NULL,
    PRIMARY KEY (menu_item_id, role),
    FOREIGN KEY (menu_item_id) REFERENCES menu_items(id) ON DELETE CASCADE
);

-- =================================================
-- Indexes
-- =================================================
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_role ON users(role);
CREATE INDEX idx_users_school_id ON users(school_id);
CREATE INDEX idx_schools_code ON schools(code);
CREATE INDEX idx_schools_owner_id ON schools(owner_id);
CREATE INDEX idx_student_parents_student ON student_parents(student_id);
CREATE INDEX idx_student_parents_parent ON student_parents(parent_id);
CREATE INDEX idx_teachers_school ON teachers(school_id);
CREATE INDEX idx_students_school ON students(school_id);
CREATE INDEX idx_canteens_school ON canteens(school_id);
CREATE INDEX idx_school_classes_school ON school_classes(school_id);
CREATE INDEX idx_school_classes_main_teacher ON school_classes(main_teacher_id);
CREATE INDEX idx_school_classes_academic_year ON school_classes(academic_year);
CREATE INDEX idx_class_schedules_class ON class_schedules(class_id);
CREATE INDEX idx_class_schedules_day_time ON class_schedules(day_of_week, start_time);
CREATE INDEX idx_class_schedules_teacher ON class_schedules(teacher_id);
CREATE INDEX idx_class_students_class ON class_students(class_id);
CREATE INDEX idx_class_students_student ON class_students(student_id);
CREATE INDEX idx_class_teachers_class ON class_teachers(class_id);
CREATE INDEX idx_class_teachers_teacher ON class_teachers(teacher_id);
CREATE INDEX idx_lessons_grade_level ON lessons(grade_level);
CREATE INDEX idx_timestamps_school ON class_timestamps(school_id);
CREATE INDEX idx_timestamps_order ON class_timestamps(order_index);

-- =================================================
-- Initial Data
-- =================================================

-- =================================================
-- Insert default lessons using IscedLevel enum
-- Auto-generated IDs
-- =================================================
INSERT INTO lessons (name, name_en, grade_level, color) VALUES
-- PRE_PRIMARY
('ریاضی', 'Mathematics', 'PRE_PRIMARY', 'blue'),
('علوم', 'Science', 'PRE_PRIMARY', 'green'),
('هنر', 'Art', 'PRE_PRIMARY', 'pink'),
('ورزش', 'Physical Education', 'PRE_PRIMARY', 'orange'),
('ادبیات', 'Literature', 'PRE_PRIMARY', 'purple'),

-- PRIMARY
('ریاضی', 'Mathematics', 'PRIMARY', 'blue'),
('علوم', 'Science', 'PRIMARY', 'green'),
('ادبیات', 'Literature', 'PRIMARY', 'purple'),
('هدیه های آسمانی', 'Religion', 'PRIMARY', 'indigo'),
('هنر', 'Art', 'PRIMARY', 'pink'),
('ورزش', 'Physical Education', 'PRIMARY', 'orange'),
('مطالعات اجتماعی', 'Social Studies', 'PRIMARY', 'amber'),

-- LOWER_SECONDARY
('ریاضی', 'Mathematics', 'LOWER_SECONDARY', 'blue'),
('علوم', 'Science', 'LOWER_SECONDARY', 'green'),
('ادبیات فارسی', 'Persian Literature', 'LOWER_SECONDARY', 'purple'),
('عربی', 'Arabic', 'LOWER_SECONDARY', 'red'),
('انگلیسی', 'English', 'LOWER_SECONDARY', 'yellow'),
('مطالعات اجتماعی', 'Social Studies', 'LOWER_SECONDARY', 'amber'),
('پیام های آسمانی', 'Religion', 'LOWER_SECONDARY', 'indigo'),
('هنر', 'Art', 'LOWER_SECONDARY', 'pink'),
('ورزش', 'Physical Education', 'LOWER_SECONDARY', 'orange'),

-- UPPER_SECONDARY
('ریاضی', 'Mathematics', 'UPPER_SECONDARY', 'blue'),
('فیزیک', 'Physics', 'UPPER_SECONDARY', 'green'),
('شیمی', 'Chemistry', 'UPPER_SECONDARY', 'teal'),
('ادبیات فارسی', 'Persian Literature', 'UPPER_SECONDARY', 'purple'),
('عربی', 'Arabic', 'UPPER_SECONDARY', 'red'),
('انگلیسی', 'English', 'UPPER_SECONDARY', 'yellow'),
('دین و زندگی', 'Religion', 'UPPER_SECONDARY', 'indigo'),
('هنر', 'Art', 'UPPER_SECONDARY', 'pink'),
('ورزش', 'Physical Education', 'UPPER_SECONDARY', 'orange'),

-- TERTIARY
('ریاضی', 'Mathematics', 'TERTIARY', 'blue'),
('فیزیک', 'Physics', 'TERTIARY', 'green'),
('شیمی', 'Chemistry', 'TERTIARY', 'teal'),
('ادبیات', 'Literature', 'TERTIARY', 'purple'),
('تاریخ', 'History', 'TERTIARY', 'amber'),
('علوم اجتماعی', 'Social Sciences', 'TERTIARY', 'indigo'),
('اقتصاد', 'Economics', 'TERTIARY', 'yellow'),
('زبان انگلیسی', 'English Language', 'TERTIARY', 'pink'),
('هنر', 'Art', 'TERTIARY', 'red'),
('ورزش', 'Physical Education', 'TERTIARY', 'orange');

