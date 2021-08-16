CREATE TABLE reader_db (
    id BIGINT IDENTITY NOT NULL,
    first_name VARCHAR(255),
    last_name VARCHAR(255),
    dob DATE,
    address VARCHAR(255),
    phone VARCHAR(255)
);