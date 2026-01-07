-- Migration script to add enabled column to users table
-- Run this script on your PostgreSQL database

-- Add the enabled column with default value true
ALTER TABLE users ADD COLUMN IF NOT EXISTS enabled BOOLEAN NOT NULL DEFAULT true;

-- Update any existing users to have enabled = true
UPDATE users SET enabled = true WHERE enabled IS NULL;

-- Verify the column was added
SELECT column_name, data_type, is_nullable, column_default 
FROM information_schema.columns 
WHERE table_name = 'users' AND column_name = 'enabled';
