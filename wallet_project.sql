CREATE ROLE prog_admin WITH LOGIN PASSWORD '123456';

DO $$ 
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_database WHERE datname = 'wallet_management') THEN
        CREATE DATABASE wallet_management;
    END IF;
END $$;

\c wallet_management;

CREATE TABLE IF NOT EXISTS account(
    account_id INT PRIMARY KEY,
    customer_name varchar(200) NOT NULL,
    balance DECIMAL(10, 2) DEFAULT 0.00,
    password varchar(20) NOT NULL,
    currency_id INT REFERENCES currency(currency_id)
);

CREATE TABLE IF NOT EXISTS "transaction"(
    transaction_id INT PRIMARY KEY,
    account_id INT REFERENCES account(account_id),
    amount DECIMAL(10, 2) DEFAULT 0.00,
    transaction_date DATE,
    description varchar(200)
);

CREATE TABLE IF NOT EXISTS currency(
    currency_id INT PRIMARY KEY,
    currency_name varchar(200) NOT NULL,
    currency_code varchar(3) NOT NULL
);