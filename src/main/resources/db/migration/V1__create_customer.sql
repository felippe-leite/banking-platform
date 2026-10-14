CREATE TABLE customer (
    id UUID PRIMARY KEY,
    name TEXT NOT NULL,
    cpf VARCHAR(11) NOT NULL,
    birth_date DATE NOT NULL,
    CONSTRAINT uk_customer_cpf UNIQUE (cpf),
    CONSTRAINT ck_customer_name_not_blank CHECK (name ~ '[^[:space:]]'),
    CONSTRAINT ck_customer_cpf_format CHECK (cpf ~ '^[0-9]{11}$')
);
