-- 01-init.sql
-- =================================================
-- Create extensions
-- =================================================
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- =================================================
-- Drop tables and sequences (for a clean reset)
-- =================================================
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
        'STUDENT', 'TEACHER', 'ADMIN', 'PARENT', 'SUPERVISOR',
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
-- Schools Table
-- =================================================
CREATE TABLE schools (
    id BIGINT PRIMARY KEY DEFAULT nextval('schools_seq'),
    name VARCHAR(255) NOT NULL,
    code VARCHAR(50) UNIQUE NOT NULL,
    owner_id BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (owner_id) REFERENCES users(id) ON DELETE SET NULL
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
    class_id BIGINT,
    emergency_contact VARCHAR(100),
    emergency_phone VARCHAR(20),
    medical_notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL,
    FOREIGN KEY (school_id) REFERENCES schools(id) ON DELETE SET NULL
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
-- Teachers Table
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

-- =================================================
-- Initial Data
-- =================================================
-- OWNER/Admin
INSERT INTO users (user_id, email, password_hash, first_name, last_name, phone_number, role, status, is_active, email_verified)
VALUES (uuid_generate_v4(), 'admin@taali.com', '$2a$12$LQv3c1yqBWVHxkd0L6kZrOaGFrnsyknKZyFbYh6g.2Iuwm55qRNWS', 'System', 'Admin', '+989120658719', 'OWNER', 'ACTIVE', true, true)
ON CONFLICT (email) DO NOTHING;

-- Default school owned by admin
INSERT INTO schools (name, code, owner_id)
SELECT 'Main School', 'MAIN001', id FROM users WHERE email='admin@taali.com';
