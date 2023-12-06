CREATE ROLE prog_admin WITH LOGIN PASSWORD '123456';

DO $$ 
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_database WHERE datname = 'wallet_management') THEN
        CREATE DATABASE wallet_management;
    END IF;
END $$;

\c wallet_management;

CREATE TABLE IF NOT EXISTS account (
    account_id INT PRIMARY KEY,
    account_name VARCHAR(50) NOT NULL,
    balance DECIMAL(10, 2) DEFAULT 0.00,
    last_update_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    password VARCHAR(20) NOT NULL,
    currency_id INT REFERENCES currency(currency_id),
    account_type VARCHAR(20) CHECK (account_type IN ('Banque', 'Espèce', 'Mobile Money'))
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
    currency_name varchar(200) CHECK (currency_name IN ('EURO', 'ARIARY')) NOT NULL,
    currency_code varchar(3) NOT NULL
);

INSERT INTO account (account_id, customer_name, balance, password, currency_id)
VALUES
    (1, 'John Doe', 0.00, 'password123', 1),
    (2, 'Alice Smith', 0.00, 'pass456', 2),
    (3, 'Bob Johnson', 0.00, 'secret', 3)
ON CONFLICT(account_id) DO NOTHING;

INSERT INTO currency (currency_id, currency_name, currency_code)
VALUES
    (1, 'US Dollar', 'USD'),
    (2, 'Euro', 'EUR'),
    (3, 'Ariary', 'AR')
ON CONFLICT(currency_id) DO NOTHING;

INSERT INTO "transaction" (transaction_id, account_id, amount, transaction_date, description)
VALUES
    (1, 1, 50.00, '2023-01-01', 'Deposit'),
    (2, 2, -20.00, '2023-01-02', 'Withdrawal'),
    (3, 1, 30.00, '2023-01-03', 'Deposit')
ON CONFLICT(transaction_id) DO NOTHING;