-- Install required PostgreSQL extensions for UUID generation and ltree
-- This migration must run first as other migrations depend on UUID generation

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS ltree;
