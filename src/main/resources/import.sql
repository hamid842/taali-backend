-- First, generate a proper hash for "admin123" using your PasswordEncoder
-- Then run this:

INSERT INTO users (
    user_id,
    email,
    password_hash,
    first_name,
    last_name,
    phone_number,
    role,
    status,
    created_at,
    updated_at
) VALUES (
    uuid_generate_v4(),
    'admin@taali.com',
    '$2a$12$LQv3c1yqBWVHxkd0L6kZrOaGFrnsyknKZyFbYh6g.2Iuwm55qRNWS',
    'System',
    'Admin',
    '+989120658719',
    'ADMIN',
    'ACTIVE',
    true,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
) ON CONFLICT (email) DO NOTHING;