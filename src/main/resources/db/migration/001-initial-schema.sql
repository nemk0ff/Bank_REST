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

-- DROP TABLE users CASCADE;