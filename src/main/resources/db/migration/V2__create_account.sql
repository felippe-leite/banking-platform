CREATE TABLE account (
    id UUID PRIMARY KEY,
    customer_id UUID NOT NULL,
    branch VARCHAR(4) NOT NULL,
    number VARCHAR(6) NOT NULL,
    digit VARCHAR(1) NOT NULL,
    -- Sem escala declarada: rejeitar frações extras em vez de arredondar.
    balance NUMERIC NOT NULL DEFAULT 0.00,
    status VARCHAR(7) NOT NULL DEFAULT 'ACTIVE',
    CONSTRAINT fk_account_customer FOREIGN KEY (customer_id) REFERENCES customer (id),
    CONSTRAINT uk_account_number UNIQUE (branch, number, digit),
    CONSTRAINT ck_account_branch_format CHECK (branch ~ '^[0-9]{4}$'),
    CONSTRAINT ck_account_number_format CHECK (number ~ '^[0-9]{6}$'),
    CONSTRAINT ck_account_digit_format CHECK (digit ~ '^[0-9]$'),
    CONSTRAINT ck_account_balance CHECK (
        balance >= 0 AND balance < 'Infinity'::numeric AND balance = round(balance, 2)
    ),
    CONSTRAINT ck_account_status CHECK (status IN ('ACTIVE', 'BLOCKED', 'CLOSED')),
    CONSTRAINT ck_account_closed_balance CHECK (status <> 'CLOSED' OR balance = 0)
);

CREATE INDEX ix_account_customer_id ON account (customer_id);
