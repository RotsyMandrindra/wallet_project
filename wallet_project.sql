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
    account_type VARCHAR(20) CHECK (account_type IN ('Bank', 'Espece', 'Mobile Money'))
);


CREATE TABLE IF NOT EXISTS "transaction"(
    transaction_id INT PRIMARY KEY,
    account_id INT REFERENCES account(account_id),
    amount DECIMAL(10, 2) DEFAULT 0.00,
    transaction_date TIMESTAMP,  
    description VARCHAR(200),
    transaction_type VARCHAR(10) CHECK (transaction_type IN ('debit', 'credit')),
    label VARCHAR(50) 
);


CREATE TABLE IF NOT EXISTS currency(
    currency_id INT PRIMARY KEY,
    currency_name varchar(200) CHECK (currency_name IN ('EURO', 'ARIARY')) NOT NULL,
    currency_code varchar(3) NOT NULL
);

INSERT INTO account (account_id, account_name, balance, password, currency_id, account_type)
VALUES
    (1, 'Bank account', 1000.00, 'motdepasse123', 1, 'Bank')
    ON CONFLICT(account_id) DO NOTHING;

INSERT INTO account (account_id, account_name, balance, password, currency_id, account_type)
VALUES
    (2, 'Bank account', 500.00, 'mdp456', 2, 'Espece')
    ON CONFLICT(account_id) DO NOTHING;

INSERT INTO account (account_id, account_name, balance, password, currency_id, account_type)
VALUES
    (3, 'Mobile Money Account', 200.00, 'secret789', 1, 'Mobile Money')
    ON CONFLICT(account_id) DO NOTHING;



INSERT INTO currency (currency_id, currency_name, currency_code)
VALUES
    (1, 'ARIARY', 'MGA'),
    (2, 'EURO', 'EUR')
ON CONFLICT(currency_id) DO NOTHING;

INSERT INTO "transaction" (transaction_id, account_id, amount, transaction_date, description, transaction_type, label)
VALUES
    (1, 1, 500.00, '2023-12-07 12:00:00', 'salary', 'credit', 'Initial deposit') ON CONFLICT(transaction_id) DO NOTHING;

INSERT INTO "transaction" (transaction_id, account_id, amount, transaction_date, description, transaction_type, label)
VALUES
    (2, 2, 100.00, '2023-12-08 14:30:00', 'shoes', 'debit', 'bank savings') ON CONFLICT(transaction_id) DO NOTHING;

INSERT INTO "transaction" (transaction_id, account_id, amount, transaction_date, description, transaction_type, label)
VALUES
    (3, 1, 200.00, '2023-12-10 10:45:00', 'Payment by card', 'debit', 'Buy online') ON CONFLICT(transaction_id) DO NOTHING;

 GRANT SELECT ON TABLE account TO mandrindra;

 GRANT SELECT ON TABLE "transaction" TO mandrindra;

 GRANT SELECT ON TABLE currency TO mandrindra;

