DROP TABLE IF EXISTS users;

CREATE TABLE IF NOT EXISTS users
(
    id            BIGSERIAL PRIMARY KEY,
    email         VARCHAR(255) NOT NULL UNIQUE,
    password      VARCHAR(255) NOT NULL,
    role          VARCHAR(10)  NOT NULL CHECK (role IN ('USER', 'ADMIN')),
    name          VARCHAR(255) NOT NULL,
    surname       VARCHAR(32)  NOT NULL,
    birthdate     DATE,
    registered_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS bank_cards
(
    id            BIGSERIAL PRIMARY KEY,
    card_number   VARCHAR(255) NOT NULL UNIQUE,
    masked_number VARCHAR(19)  NOT NULL,
    card_holder   VARCHAR(255) NOT NULL,
    expiry_date   DATE         NOT NULL,
    status        VARCHAR(10)  NOT NULL CHECK (status IN ('ACTIVE', 'BLOCKED', 'EXPIRED')),
    balance       DECIMAL(19,4) NOT NULL DEFAULT 0.0000,
    user_id       BIGINT       NOT NULL,
    created_at    TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_bank_cards_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE INDEX idx_bank_cards_user_id ON bank_cards (user_id);
CREATE INDEX idx_bank_cards_status ON bank_cards (status);
CREATE INDEX idx_bank_cards_expiry_date ON bank_cards (expiry_date);

-- DROP TABLE users CASCADE;
-- DROP TABLE bank_cards CASCADE;