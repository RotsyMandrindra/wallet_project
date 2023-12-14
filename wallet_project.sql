CREATE ROLE prog_admin WITH LOGIN PASSWORD '123456';

CREATE DATABASE IF NOT EXISTS wallet_management;
\c wallet_management;

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE IF NOT EXISTS account (
    account_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    account_name VARCHAR(50) UNIQUE NOT NULL,
    balance DECIMAL(10, 2) DEFAULT 0.00,
    last_update_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    password VARCHAR(20) NOT NULL,
    currency_id UUID REFERENCES currency(currency_id),
    account_type VARCHAR(20) CHECK (account_type IN ('Bank', 'Espece', 'Mobile Money'))
);


CREATE TABLE IF NOT EXISTS "transaction" (
    transaction_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    account_id UUID REFERENCES account(account_id),
    amount DECIMAL(10, 2) DEFAULT 0.00,
    transaction_date TIMESTAMP,  
    description VARCHAR(200),
    transaction_type VARCHAR(10) CHECK (transaction_type IN ('debit', 'credit')),
    label VARCHAR(50),
    UNIQUE (account_id, transaction_date, description)
);


CREATE TABLE IF NOT EXISTS currency (
    currency_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    currency_name varchar(200) UNIQUE CHECK (currency_name IN ('EURO', 'ARIARY')) NOT NULL,
    currency_code varchar(3) NOT NULL
);

INSERT INTO account (account_name, balance, password, currency_id, account_type) VALUES
    ('John Doe', 1000.00, 'password123', (SELECT currency_id FROM currency WHERE currency_name = 'EURO'), 'Bank')
    ON CONFLICT (account_name) DO NOTHING;

INSERT INTO account (account_name, balance, password, currency_id, account_type) VALUES
    ('Jane Smith', 500.00, 'pass456', (SELECT currency_id FROM currency WHERE currency_name = 'ARIARY'), 'Espece')
    ON CONFLICT (account_name) DO NOTHING;

INSERT INTO account (account_name, balance, password, currency_id, account_type) VALUES
    ('Bob Johnson', 200.00, 'secure789', (SELECT currency_id FROM currency WHERE currency_name = 'EURO'), 'Mobile Money')
    ON CONFLICT (account_name) DO NOTHING;

INSERT INTO currency (currency_name, currency_code) VALUES 
    ('EURO', 'EUR')
    ON CONFLICT (currency_name) DO NOTHING;

INSERT INTO currency (currency_name, currency_code) VALUES 
    ('ARIARY', 'MGA')
    ON CONFLICT (currency_name) DO NOTHING;

INSERT INTO "transaction" (account_id, amount, transaction_date, description, transaction_type, label) VALUES
    ((SELECT account_id FROM account WHERE account_name = 'Bob Johnson'), 100.00, '2023-01-03 12:00:00', 'Transfer', 'debit', 'Friend Payment')
    ON CONFLICT (account_id, transaction_date, description) DO NOTHING;

INSERT INTO "transaction" (account_id, amount, transaction_date, description, transaction_type, label) VALUES
    ((SELECT account_id FROM account WHERE account_name = 'Bob Johnson'), 150.00, '2023-01-04 14:30:00', 'Purchase', 'debit', 'Online Shopping')
    ON CONFLICT (account_id, transaction_date, description) DO NOTHING;

INSERT INTO "transaction" (account_id, amount, transaction_date, description, transaction_type, label) VALUES
    ((SELECT account_id FROM account WHERE account_name = 'Bob Johnson'), 200.00, '2023-01-05 10:00:00', 'Refund', 'credit', 'Product Return')
    ON CONFLICT (account_id, transaction_date, description) DO NOTHING;

 GRANT SELECT ON TABLE account TO mandrindra;

 GRANT SELECT ON TABLE "transaction" TO mandrindra;

 GRANT SELECT ON TABLE currency TO mandrindra;

CREATE TABLE IF NOT EXISTS currencyValue (
    id_currency_value UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    id_currency UUID REFERENCES currency(currency_id),
    id_currency_destination UUID REFERENCES currency(currency_id),
    amount DECIMAL(10, 2) NOT NULL,
    date_effect TIMESTAMP NOT NULL
);

GRANT SELECT ON TABLE currencyValue TO mandrindra;

    CREATE TABLE transfer_history (
        id_transfer_history UUID PRIMARY KEY,
        debit_transaction_id UUID NOT NULL,
        credit_transaction_id UUID NOT NULL,
        transfer_date TIMESTAMP NOT NULL,
        amount DECIMAL(10, 2) DEFAULT 0.00
    );

GRANT SELECT ON TABLE transfer_history TO mandrindra;

CREATE OR REPLACE FUNCTION getAriaryBalance(account_id UUID, target_date TIMESTAMP) RETURNS DOUBLE PRECISION AS $$
DECLARE
    current_balance DOUBLE PRECISION := 0;
    exchange_rate DOUBLE PRECISION;
BEGIN
    SELECT COALESCE(SUM(CASE WHEN transaction_type = 'credit' THEN amount ELSE -amount END), 0)
    INTO current_balance
    FROM "transaction"
    WHERE account_id = account_id AND transaction_date <= target_date;

    SELECT amount
    INTO exchange_rate
    FROM CurrencyValue
    WHERE date_effect = (
        SELECT MAX(date_effect)
        FROM CurrencyValue
        WHERE date_effect <= target_date
    );
    current_balance := current_balance * exchange_rate;

    RETURN current_balance;
END;
$$ LANGUAGE plpgsql;

GRANT INSERT ON TABLE transfer_history TO mandrindra;

CREATE TABLE exchange_rate (
    date TIMESTAMP PRIMARY KEY,
    rate DOUBLE PRECISION NOT NULL
);

GRANT SELECT ON TABLE exchange_rate TO mandrindra;

INSERT INTO exchange_rate (date, rate) VALUES ('2023-12-05', 4600);

GRANT UPDATE ON TABLE exchange_rate TO mandrindra;

CREATE TABLE IF NOT EXISTS category (
    category_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(50) UNIQUE NOT NULL,
    transaction_type VARCHAR(10) CHECK (transaction_type IN ('debit', 'credit'))
);

INSERT INTO category (name, transaction_type)
VALUES 
    ('Restaurant', 'debit'),
    ('Phone and Multimedia', 'debit'),
    ('Salary', 'credit'),
    ('Loan', 'credit')
ON CONFLICT (name) DO NOTHING;

CREATE OR REPLACE FUNCTION calculate_balance(account_id UUID, start_date TIMESTAMP, end_date TIMESTAMP)
RETURNS TABLE (total_credit DECIMAL, total_debit DECIMAL) AS
$$
BEGIN
    SELECT
        COALESCE(SUM(CASE WHEN transaction_type = 'credit' THEN amount END), 0) AS total_credit,
        COALESCE(SUM(CASE WHEN transaction_type = 'debit' THEN amount END), 0) AS total_debit
    INTO
        total_credit,
        total_debit
    FROM
        transaction
    WHERE
        transaction.account_id = calculate_balance.account_id
        AND transaction.transaction_date BETWEEN start_date AND end_date;

    RETURN NEXT;
END;
$$ LANGUAGE PLPGSQL;


SELECT * FROM calculate_balance('efcf34e5-c7a2-427d-a402-f466b36453d1', '2023-12-08 17:33:57.331554', '2023-12-08 17:33:57.331554');

CREATE OR REPLACE FUNCTION calculate_category_balance(account_id_param UUID, start_date_param TIMESTAMP, end_date_param TIMESTAMP)
RETURNS TABLE (restaurant DECIMAL, phone_multimedia DECIMAL, salary DECIMAL, loan DECIMAL) AS
$$
BEGIN
    SELECT
        COALESCE(SUM(CASE WHEN c.name = 'Restaurant' THEN t.amount END), 0) AS restaurant,
        COALESCE(SUM(CASE WHEN c.name = 'Phone and Multimedia' THEN t.amount END), 0) AS phone_multimedia,
        COALESCE(SUM(CASE WHEN c.name = 'Salary' THEN t.amount END), 0) AS salary,
        COALESCE(SUM(CASE WHEN c.name = 'Loan' THEN t.amount END), 0) AS loan
    INTO
        restaurant,
        phone_multimedia,
        salary,
        loan
    FROM
        category c
    LEFT JOIN
        transaction t ON c.name = t.label AND t.account_id = account_id_param AND t.transaction_date BETWEEN start_date_param AND end_date_param
    GROUP BY
        c.name;

    RETURN NEXT;
END;
$$ LANGUAGE PLPGSQL;

GRANT SELECT ON TABLE category TO mandrindra;