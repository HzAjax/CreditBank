CREATE TABLE IF NOT EXISTS client (
    client_id UUID PRIMARY KEY,
    last_name VARCHAR(255),
    first_name VARCHAR(255),
    middle_name VARCHAR(255),
    birth_date DATE,
    email VARCHAR(255),
    gender VARCHAR(32),
    marital_status VARCHAR(32),
    dependent_amount INT,
    passport_id JSONB,
    employment_id JSONB,
    account_number VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS credit (
    credit_id UUID PRIMARY KEY,
    amount NUMERIC(19, 2),
    term INT,
    monthly_payment NUMERIC(19, 2),
    rate NUMERIC(5, 2),
    psk NUMERIC(6, 4),
    payment_schedule JSONB,
    insurance_enabled BOOLEAN,
    salary_client BOOLEAN,
    credit_status VARCHAR(32)
);

CREATE TABLE IF NOT EXISTS statement (
    statement_id UUID PRIMARY KEY,
    client_id UUID REFERENCES client(client_id),
    credit_id UUID REFERENCES credit(credit_id) ON DELETE CASCADE,
    application_status VARCHAR(32),
    creation_date TIMESTAMP,
    applied_offer JSONB,
    sign_date TIMESTAMP,
    ses_code VARCHAR(255),
    status_history JSONB
);
