-- Insert default admin user (password: admin123)
INSERT INTO users (email, password, first_name, last_name, phone, role, email_verified, created_at, updated_at)
VALUES (
    'admin@taali.com',
    '$2a$12$LQv3c1yqBWVHxkd0L6kZrOaGFrnsyknKZyFbYh6g.2Iuwm55qRNWS',
    'System',
    'Admin',
    '+989120658719',
    'ADMIN',
    true,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
) ON CONFLICT (email) DO NOTHING;