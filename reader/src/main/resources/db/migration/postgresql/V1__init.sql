CREATE TABLE IF NOT EXISTS reader_db (
    id BIGSERIAL PRIMARY KEY,
    first_name VARCHAR(255),
    last_name VARCHAR(255),
    dob DATE,
    address VARCHAR(255),
    phone VARCHAR(255)
);