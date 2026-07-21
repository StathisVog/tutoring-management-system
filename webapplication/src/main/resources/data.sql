-- ============================================================================================
-- INITIAL SYSTEM ADMINISTRATOR PROVISIONING
--
-- Requirement: Admin accounts cannot be registered via the public API.
-- They must be created with direct database access.
-- This script provisions the default admin upon application startup.
-- ============================================================================================

INSERT IGNORE INTO users (username, email, password_hash, full_name, role, enabled, created_at)
VALUES (
    'admin',
    'admin@tutoringmanagementsystem.com',
    '$2a$10$QJzkcQq/nBVAygA4ZLz4FOmaV7PF8iimfEyLg2KAooxMYOrnWIKXq',
    'System Administrator',
    'ADMIN',
    true,
    NOW()
);