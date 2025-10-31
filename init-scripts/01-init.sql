-- 01-init.sql

-- Create extensions
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- =============================================
-- Drop existing tables and sequences (for clean reset)
-- =============================================
DROP TABLE IF EXISTS menu_item_roles CASCADE;
DROP TABLE IF EXISTS menu_items CASCADE;
DROP TABLE IF EXISTS refresh_tokens CASCADE;
DROP TABLE IF EXISTS schools CASCADE;  -- Add schools table drop
DROP TABLE IF EXISTS users CASCADE;

DROP SEQUENCE IF EXISTS users_seq;
DROP SEQUENCE IF EXISTS refresh_tokens_seq;
DROP SEQUENCE IF EXISTS menu_items_seq;
DROP SEQUENCE IF EXISTS schools_seq;  -- Add schools sequence drop

-- =============================================
-- Create Sequences for Hibernate
-- =============================================

-- Create sequences that Hibernate expects
CREATE SEQUENCE users_seq START 1 INCREMENT 1;
CREATE SEQUENCE refresh_tokens_seq START 1 INCREMENT 1;
CREATE SEQUENCE menu_items_seq START 1 INCREMENT 1;
CREATE SEQUENCE schools_seq START 1 INCREMENT 1;  -- Add schools sequence

-- =============================================
-- Schools Table (Add this new table)
-- =============================================

CREATE TABLE schools (
    id BIGINT PRIMARY KEY DEFAULT nextval('schools_seq'),
    name VARCHAR(255) NOT NULL,
    code VARCHAR(50) UNIQUE NOT NULL,
    owner_id BIGINT,  -- For owner users who can have multiple schools
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (owner_id) REFERENCES users(id) ON DELETE SET NULL
);

-- =============================================
-- Users and Authentication Tables
-- =============================================

-- Create users table (Updated with school_id)
CREATE TABLE users (
    id BIGINT PRIMARY KEY DEFAULT nextval('users_seq'),
    user_id UUID UNIQUE NOT NULL DEFAULT uuid_generate_v4(),
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    phone_number VARCHAR(20) NOT NULL,
    role VARCHAR(50) NOT NULL CHECK (role IN ('STUDENT', 'TEACHER', 'ADMIN', 'PARENT', 'SUPERVISOR', 'FINANCE_TEAM', 'CANTEEN_OPERATOR', 'OWNER')),  -- Added OWNER role
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING' CHECK (status IN ('PENDING', 'ACTIVE', 'INACTIVE', 'SUSPENDED')),
    is_active BOOLEAN DEFAULT true,
    email_verified BOOLEAN DEFAULT false,
    school_id BIGINT,  -- For non-owner users (teachers, students, etc.)
    last_login TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (school_id) REFERENCES schools(id) ON DELETE SET NULL
);

-- Now update the schools table to reference users (was created above)
-- Note: We already created schools table with owner_id reference

-- Create refresh_tokens table - EXPLICITLY define as BIGINT
CREATE TABLE refresh_tokens (
    id BIGINT PRIMARY KEY DEFAULT nextval('refresh_tokens_seq'), -- Explicit BIGINT
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    token VARCHAR(500) UNIQUE NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    is_revoked BOOLEAN DEFAULT false,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

-- =============================================
-- Menu System Tables
-- =============================================

-- Create menu_items table
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

-- Create menu_item_roles table
CREATE TABLE menu_item_roles (
    menu_item_id BIGINT NOT NULL,
    role VARCHAR(50) NOT NULL,
    PRIMARY KEY (menu_item_id, role),
    FOREIGN KEY (menu_item_id) REFERENCES menu_items(id) ON DELETE CASCADE
);

-- =============================================
-- Indexes
-- =============================================

-- Schools indexes
CREATE INDEX idx_schools_owner_id ON schools(owner_id);
CREATE INDEX idx_schools_code ON schools(code);

-- Users indexes
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_role ON users(role);
CREATE INDEX idx_users_user_id ON users(user_id);
CREATE INDEX idx_users_status ON users(status);
CREATE INDEX idx_users_school_id ON users(school_id);  -- New index

-- Refresh tokens indexes
CREATE INDEX idx_refresh_tokens_token ON refresh_tokens(token);
CREATE INDEX idx_refresh_tokens_user_id ON refresh_tokens(user_id);
CREATE INDEX idx_refresh_tokens_expires_at ON refresh_tokens(expires_at);

-- Menu items indexes
CREATE INDEX idx_menu_items_parent_id ON menu_items(parent_id);
CREATE INDEX idx_menu_items_order_index ON menu_items(order_index);
CREATE INDEX idx_menu_items_route ON menu_items(route);
CREATE INDEX idx_menu_items_title_key ON menu_items(title_key);

-- Menu item roles indexes
CREATE INDEX idx_menu_item_roles_menu_item_id ON menu_item_roles(menu_item_id);
CREATE INDEX idx_menu_item_roles_role ON menu_item_roles(role);

-- =============================================
-- Initial Data
-- =============================================

-- First insert the admin user (without school_id initially)
INSERT INTO users (
    user_id,
    email,
    password_hash,
    first_name,
    last_name,
    phone_number,
    role,
    status,
    is_active,
    email_verified
) VALUES (
    uuid_generate_v4(),
    'admin@taali.com',
    '$2a$12$LQv3c1yqBWVHxkd0L6kZrOaGFrnsyknKZyFbYh6g.2Iuwm55qRNWS',
    'System',
    'Admin',
    '+989120658719',
    'OWNER',
    'ACTIVE',
    true,
    true
);

-- Now create a default school owned by the admin
INSERT INTO schools (name, code, owner_id)
SELECT 'Main School', 'MAIN001', id
FROM users
WHERE email = 'admin@taali.com';

-- Update the admin user to be associated with the school
UPDATE users
SET school_id = (SELECT id FROM schools WHERE code = 'MAIN001')
WHERE email = 'admin@taali.com';

-- Update sequences to current max values
SELECT setval('users_seq', (SELECT MAX(id) FROM users));
SELECT setval('schools_seq', (SELECT MAX(id) FROM schools));
SELECT setval('refresh_tokens_seq', 1);
SELECT setval('menu_items_seq', 1);

-- Insert default menu items
INSERT INTO menu_items (title_key, icon, route, path, order_index, required_permission) VALUES
('menu_dashboard', 'dashboard', '/dashboard', '/dashboard', 1, NULL),
('menu_school_management', 'school', NULL, NULL, 2, 'manage_schools'),
('menu_user_management', 'people', NULL, NULL, 3, 'manage_users'),
('menu_finance', 'attach_money', NULL, NULL, 4, 'manage_finance'),
('menu_teacher_management', 'person', NULL, NULL, 5, 'manage_teachers'),
('menu_class_management', 'class', NULL, NULL, 6, 'manage_classes'),
('menu_student_management', 'person', NULL, NULL, 7, 'manage_students'),
('menu_parent_management', 'family_restroom', NULL, NULL, 8, 'manage_parents'),
('menu_attendance', 'event_available', NULL, NULL, 9, 'manage_attendance'),
('menu_assignments', 'assignment', NULL, NULL, 10, 'manage_assignments'),
('menu_my_profile', 'person', '/profile', '/profile', 11, NULL)
ON CONFLICT (title_key) DO NOTHING;

-- Insert submenu items
INSERT INTO menu_items (title_key, icon, route, path, order_index, required_permission, parent_id)
SELECT 'menu_create_school', 'add', '/schools/create', '/schools/create', 1, 'manage_schools', id
FROM menu_items WHERE title_key = 'menu_school_management'
ON CONFLICT (title_key) DO NOTHING;

INSERT INTO menu_items (title_key, icon, route, path, order_index, required_permission, parent_id)
SELECT 'menu_list_schools', 'list', '/schools', '/schools', 2, 'manage_schools', id
FROM menu_items WHERE title_key = 'menu_school_management'
ON CONFLICT (title_key) DO NOTHING;

INSERT INTO menu_items (title_key, icon, route, path, order_index, required_permission, parent_id)
SELECT 'menu_create_user', 'person_add', '/users/create', '/users/create', 1, 'manage_users', id
FROM menu_items WHERE title_key = 'menu_user_management'
ON CONFLICT (title_key) DO NOTHING;

INSERT INTO menu_items (title_key, icon, route, path, order_index, required_permission, parent_id)
SELECT 'menu_list_users', 'people', '/users', '/users', 2, 'manage_users', id
FROM menu_items WHERE title_key = 'menu_user_management'
ON CONFLICT (title_key) DO NOTHING;

-- Assign roles to menu items
-- Owner has access to everything
INSERT INTO menu_item_roles (menu_item_id, role)
SELECT id, 'OWNER' FROM menu_items
ON CONFLICT (menu_item_id, role) DO NOTHING;

-- Admin has access to most management features
INSERT INTO menu_item_roles (menu_item_id, role)
SELECT id, 'ADMIN' FROM menu_items
WHERE title_key NOT IN ('menu_school_management', 'menu_create_school', 'menu_list_schools')  -- Owners manage schools
OR required_permission IS NULL
ON CONFLICT (menu_item_id, role) DO NOTHING;

-- Teacher specific menus
INSERT INTO menu_item_roles (menu_item_id, role)
SELECT id, 'TEACHER' FROM menu_items
WHERE title_key IN ('menu_dashboard', 'menu_my_profile', 'menu_attendance', 'menu_assignments')
OR required_permission IS NULL
ON CONFLICT (menu_item_id, role) DO NOTHING;

-- Student specific menus
INSERT INTO menu_item_roles (menu_item_id, role)
SELECT id, 'STUDENT' FROM menu_items
WHERE title_key IN ('menu_dashboard', 'menu_my_profile')
OR required_permission IS NULL
ON CONFLICT (menu_item_id, role) DO NOTHING;

-- Parent specific menus
INSERT INTO menu_item_roles (menu_item_id, role)
SELECT id, 'PARENT' FROM menu_items
WHERE title_key IN ('menu_dashboard', 'menu_my_profile')
OR required_permission IS NULL
ON CONFLICT (menu_item_id, role) DO NOTHING;