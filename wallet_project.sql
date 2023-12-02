CREATE ROLE prog_admin WITH LOGIN PASSWORD '123456';

DO $$ 
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_database WHERE datname = 'wallet_management') THEN
        CREATE DATABASE wallet_management;
    END IF;
END $$;

