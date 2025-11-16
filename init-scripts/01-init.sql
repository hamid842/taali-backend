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
DROP TABLE IF EXISTS school_type_relations CASCADE;
DROP TABLE IF EXISTS school_types CASCADE;
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
DROP SEQUENCE IF EXISTS school_types_seq;
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

-- =================================================
-- Schools Table
-- =================================================
CREATE TABLE schools (
    id BIGINT PRIMARY KEY DEFAULT nextval('schools_seq'),
    name VARCHAR(255) NOT NULL,
    code VARCHAR(50) UNIQUE NOT NULL,
    owner_id BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- =================================================
-- School Types Table
-- =================================================
CREATE TABLE school_types (
    id BIGINT PRIMARY KEY DEFAULT nextval('school_types_seq'),
    name VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- =================================================
-- School Type Relations (Many-to-Many)
-- =================================================
CREATE TABLE school_type_relations (
    id BIGINT PRIMARY KEY DEFAULT nextval('schools_seq'),
    school_id BIGINT NOT NULL,
    school_type_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (school_id) REFERENCES schools(id) ON DELETE CASCADE,
    FOREIGN KEY (school_type_id) REFERENCES school_types(id) ON DELETE CASCADE,
    UNIQUE (school_id, school_type_id)
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
    grade_level VARCHAR(100) NOT NULL,
    color VARCHAR(20),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
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
-- Student ↔ Parent many-to-many
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
CREATE INDEX idx_school_type_relations_school ON school_type_relations(school_id);
CREATE INDEX idx_school_type_relations_type ON school_type_relations(school_type_id);
CREATE INDEX idx_lessons_grade_level ON lessons(grade_level);

-- =================================================
-- Initial Data
-- =================================================

-- Insert School Types for Iran
INSERT INTO school_types (id, name) VALUES
(1, 'مهد کودک'),
(2, 'پیش دبستانی'),
(3, 'ابتدایی دوره اول'),
(4, 'ابتدایی دوره دوم'),
(5, 'متوسطه دوره اول'),
(6, 'متوسطه دوره دوم')
ON CONFLICT (id) DO NOTHING;

-- Insert default lessons for different grade levels
INSERT INTO lessons (id, name, name_en, grade_level, color) VALUES
-- Kindergarten
(1, 'ریاضی', 'Mathematics', 'مهد کودک', 'blue'),
(2, 'علوم', 'Science', 'مهد کودک', 'green'),
(3, 'هنر', 'Art', 'مهد کودک', 'pink'),
(4, 'ورزش', 'Physical Education', 'مهد کودک', 'orange'),

-- Preschool
(5, 'ریاضی', 'Mathematics', 'پیش دبستانی', 'blue'),
(6, 'علوم', 'Science', 'پیش دبستانی', 'green'),
(7, 'ادبیات', 'Literature', 'پیش دبستانی', 'purple'),
(8, 'هنر', 'Art', 'پیش دبستانی', 'pink'),
(9, 'ورزش', 'Physical Education', 'پیش دبستانی', 'orange'),

-- Primary First Period
(10, 'ریاضی', 'Mathematics', 'ابتدایی دوره اول', 'blue'),
(11, 'علوم', 'Science', 'ابتدایی دوره اول', 'green'),
(12, 'ادبیات', 'Literature', 'ابتدایی دوره اول', 'purple'),
(13, 'هدیه های آسمانی', 'Religion', 'ابتدایی دوره اول', 'indigo'),
(14, 'هنر', 'Art', 'ابتدایی دوره اول', 'pink'),
(15, 'ورزش', 'Physical Education', 'ابتدایی دوره اول', 'orange'),

-- Primary Second Period
(16, 'ریاضی', 'Mathematics', 'ابتدایی دوره دوم', 'blue'),
(17, 'علوم', 'Science', 'ابتدایی دوره دوم', 'green'),
(18, 'ادبیات', 'Literature', 'ابتدایی دوره دوم', 'purple'),
(19, 'هدیه های آسمانی', 'Religion', 'ابتدایی دوره دوم', 'indigo'),
(20, 'مطالعات اجتماعی', 'Social Studies', 'ابتدایی دوره دوم', 'amber'),
(21, 'هنر', 'Art', 'ابتدایی دوره دوم', 'pink'),
(22, 'ورزش', 'Physical Education', 'ابتدایی دوره دوم', 'orange'),

-- Secondary First Period
(23, 'ریاضی', 'Mathematics', 'متوسطه دوره اول', 'blue'),
(24, 'علوم', 'Science', 'متوسطه دوره اول', 'green'),
(25, 'ادبیات فارسی', 'Persian Literature', 'متوسطه دوره اول', 'purple'),
(26, 'عربی', 'Arabic', 'متوسطه دوره اول', 'red'),
(27, 'انگلیسی', 'English', 'متوسطه دوره اول', 'yellow'),
(28, 'مطالعات اجتماعی', 'Social Studies', 'متوسطه دوره اول', 'amber'),
(29, 'پیام های آسمانی', 'Religion', 'متوسطه دوره اول', 'indigo'),
(30, 'هنر', 'Art', 'متوسطه دوره اول', 'pink'),
(31, 'ورزش', 'Physical Education', 'متوسطه دوره اول', 'orange'),

-- Secondary Second Period
(32, 'ریاضی', 'Mathematics', 'متوسطه دوره دوم', 'blue'),
(33, 'فیزیک', 'Physics', 'متوسطه دوره دوم', 'green'),
(34, 'شیمی', 'Chemistry', 'متوسطه دوره دوم', 'teal'),
(35, 'ادبیات فارسی', 'Persian Literature', 'متوسطه دوره دوم', 'purple'),
(36, 'عربی', 'Arabic', 'متوسطه دوره دوم', 'red'),
(37, 'انگلیسی', 'English', 'متوسطه دوره دوم', 'yellow'),
(38, 'دین و زندگی', 'Religion', 'متوسطه دوره دوم', 'indigo'),
(39, 'هنر', 'Art', 'متوسطه دوره دوم', 'pink'),
(40, 'ورزش', 'Physical Education', 'متوسطه دوره دوم', 'orange')
ON CONFLICT (id) DO NOTHING;

-- OWNER/Admin
INSERT INTO users (id, user_id, email, password_hash, first_name, last_name, phone_number, role, status, is_active, email_verified)
VALUES (1, uuid_generate_v4(), 'admin@taali.com', '$2a$12$LQv3c1yqBWVHxkd0L6kZrOaGFrnsyknKZyFbYh6g.2Iuwm55qRNWS', 'System', 'Admin', '+989120658719', 'OWNER', 'ACTIVE', true, true)
ON CONFLICT (email) DO NOTHING;

-- Default school owned by admin
INSERT INTO schools (id, name, code, owner_id)
VALUES (1, 'مدرسه نمونه تهران', 'SCH001', 1)
ON CONFLICT (id) DO NOTHING;

-- Assign all school types to the default school
INSERT INTO school_type_relations (school_id, school_type_id)
SELECT 1, id FROM school_types
ON CONFLICT DO NOTHING;

-- Sample teacher user
INSERT INTO users (id, user_id, email, password_hash, first_name, last_name, phone_number, role, status, is_active, email_verified, school_id)
VALUES (2, uuid_generate_v4(), 'teacher@taali.com', '$2a$12$LQv3c1yqBWVHxkd0L6kZrOaGFrnsyknKZyFbYh6g.2Iuwm55qRNWS', 'معلم', 'نمونه', '+989123456789', 'TEACHER', 'ACTIVE', true, true, 1)
ON CONFLICT (email) DO NOTHING;

-- Sample teacher
INSERT INTO teachers (id, user_id, first_name, last_name, email, phone, subject_specialization, qualification, school_id)
VALUES (1, 2, 'معلم', 'نمونه', 'teacher@taali.com', '+989123456789', 'ریاضی', 'کارشناسی ارشد', 1)
ON CONFLICT (id) DO NOTHING;

-- Sample student user
INSERT INTO users (id, user_id, email, password_hash, first_name, last_name, phone_number, role, status, is_active, email_verified, school_id)
VALUES (3, uuid_generate_v4(), 'student@taali.com', '$2a$12$LQv3c1yqBWVHxkd0L6kZrOaGFrnsyknKZyFbYh6g.2Iuwm55qRNWS', 'دانش‌آموز', 'نمونه', '+989123456780', 'STUDENT', 'ACTIVE', true, true, 1)
ON CONFLICT (email) DO NOTHING;

-- Sample student
INSERT INTO students (id, user_id, first_name, last_name, student_id, id_number, birth_date, gender, school_id, emergency_contact, emergency_phone)
VALUES (1, 3, 'دانش‌آموز', 'نمونه', 'STU001', '1234567890', '2015-03-15', 'MALE', 1, 'پدر', '+989123456781')
ON CONFLICT (id) DO NOTHING;

-- Sample school class
INSERT INTO school_classes (id, name, grade_level, academic_year, capacity, school_id, main_teacher_id)
VALUES (1, 'کلاس اول الف', 'ابتدایی دوره اول', '1403-1404', 30, 1, 1)
ON CONFLICT (id) DO NOTHING;

-- Assign student to class
INSERT INTO class_students (class_id, student_id)
VALUES (1, 1)
ON CONFLICT DO NOTHING;

-- Assign teacher to class
INSERT INTO class_teachers (class_id, teacher_id)
VALUES (1, 1)
ON CONFLICT DO NOTHING;

-- Sample class schedules
INSERT INTO class_schedules (id, class_id, day_of_week, start_time, end_time, subject_name, teacher_id, room_number) VALUES
(1, 1, 'SUNDAY', '08:00', '08:45', 'ریاضی', 1, '۱۰۱'),
(2, 1, 'SUNDAY', '09:00', '09:45', 'علوم', 1, '۱۰۱'),
(3, 1, 'MONDAY', '08:00', '08:45', 'ادبیات', 1, '۱۰۱'),
(4, 1, 'MONDAY', '09:00', '09:45', 'هنر', 1, 'سالن ورزش'),
(5, 1, 'TUESDAY', '08:00', '08:45', 'ریاضی', 1, '۱۰۱'),
(6, 1, 'WEDNESDAY', '08:00', '08:45', 'هدیه های آسمانی', 1, '۱۰۱')
ON CONFLICT (id) DO NOTHING;

-- Reset sequences to proper values
SELECT setval('users_seq', COALESCE((SELECT MAX(id) FROM users), 1));
SELECT setval('schools_seq', COALESCE((SELECT MAX(id) FROM schools), 1));
SELECT setval('school_types_seq', COALESCE((SELECT MAX(id) FROM school_types), 1));
SELECT setval('teachers_seq', COALESCE((SELECT MAX(id) FROM teachers), 1));
SELECT setval('students_seq', COALESCE((SELECT MAX(id) FROM students), 1));
SELECT setval('school_classes_seq', COALESCE((SELECT MAX(id) FROM school_classes), 1));
SELECT setval('class_schedules_seq', COALESCE((SELECT MAX(id) FROM class_schedules), 1));
SELECT setval('lessons_seq', COALESCE((SELECT MAX(id) FROM lessons), 1));