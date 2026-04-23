-- Add username column to app_user table
ALTER TABLE app_user ADD COLUMN IF NOT EXISTS username VARCHAR(255) UNIQUE;

-- Add refresh_token and refresh_token_expiry columns
ALTER TABLE app_user ADD COLUMN IF NOT EXISTS refresh_token TEXT;
ALTER TABLE app_user ADD COLUMN IF NOT EXISTS refresh_token_expiry TIMESTAMPTZ;

-- Add role column for authorization
ALTER TABLE app_user ADD COLUMN IF NOT EXISTS role VARCHAR(50) DEFAULT 'USER';
