-- PostgreSQL Initialization Script for People and Organizations Domain
-- This script sets up the database with proper extensions, schemas, and initial configuration

-- Enable required extensions
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS "pg_stat_statements";

-- Create application schema (optional, using public schema by default)
-- CREATE SCHEMA IF NOT EXISTS people_org;

-- Set timezone
SET timezone = 'UTC';

-- Create indexes for UUID generation performance
-- These will be created by JPA/Hibernate when tables are created, but included here for reference

-- Grant necessary permissions to the application user
-- Note: The user is already created by the POSTGRES_USER environment variable

-- Configure connection limits and performance settings
ALTER DATABASE people_and_organizations SET log_statement = 'all';
ALTER DATABASE people_and_organizations SET log_min_duration_statement = 1000;
ALTER DATABASE people_and_organizations SET shared_preload_libraries = 'pg_stat_statements';

-- Create custom types that might be used by the application
DO $$
BEGIN
    -- Create enum types if they don't exist
    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'gender_type') THEN
        CREATE TYPE gender_type AS ENUM ('MALE', 'FEMALE', 'OTHER', 'PREFER_NOT_TO_SAY');
    END IF;

    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'party_type') THEN
        CREATE TYPE party_type AS ENUM ('PERSON', 'ORGANIZATION');
    END IF;

    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'contact_mechanism_type') THEN
        CREATE TYPE contact_mechanism_type AS ENUM ('EMAIL_ADDRESS', 'TELECOM_NUMBER', 'POSTAL_ADDRESS');
    END IF;
END $$;

-- Performance optimization settings
-- Note: These are suggestions and should be tuned based on actual workload

-- Connection pooling recommendation (for application configuration)
-- COMMENT: Configure connection pool in application with:
-- - Maximum pool size: 10-20 connections
-- - Minimum idle connections: 2-5
-- - Connection timeout: 30 seconds
-- - Idle timeout: 10 minutes

-- Create a simple health check function for monitoring
CREATE OR REPLACE FUNCTION health_check()
RETURNS TABLE(status text, timestamp timestamptz) AS $$
BEGIN
    RETURN QUERY SELECT 'healthy'::text, now();
END;
$$ LANGUAGE plpgsql;

-- Create audit trail table for tracking changes (optional)
CREATE TABLE IF NOT EXISTS audit_log (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    table_name VARCHAR(100) NOT NULL,
    operation VARCHAR(10) NOT NULL, -- INSERT, UPDATE, DELETE
    old_values JSONB,
    new_values JSONB,
    changed_by VARCHAR(100),
    changed_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- Create index on audit log for performance
CREATE INDEX IF NOT EXISTS idx_audit_log_table_name ON audit_log(table_name);
CREATE INDEX IF NOT EXISTS idx_audit_log_changed_at ON audit_log(changed_at);

-- Insert initial configuration data if needed
-- This is where you would add reference data, lookup tables, etc.

-- Log successful initialization
INSERT INTO audit_log (table_name, operation, new_values, changed_by)
VALUES ('database', 'INIT', '{"message": "Database initialized successfully"}', 'system');

-- Display initialization complete message
DO $$
BEGIN
    RAISE NOTICE 'People and Organizations Domain database initialization completed successfully';
    RAISE NOTICE 'Database: %, User: %, Time: %', current_database(), current_user, now();
END $$;
