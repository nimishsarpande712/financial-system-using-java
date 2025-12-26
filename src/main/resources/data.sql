-- Initialize test users
INSERT INTO users (id, username, email, balance, created_at, updated_at, version) VALUES
(1, 'john_doe', 'john.doe@example.com', 1000.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
(2, 'jane_smith', 'jane.smith@example.com', 2500.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
(3, 'bob_wilson', 'bob.wilson@example.com', 500.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0);

-- Set sequence to start after initial data
ALTER TABLE users ALTER COLUMN id RESTART WITH 4;