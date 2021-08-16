CREATE TABLE IF NOT EXISTS book_db (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255),
    author VARCHAR(255),
    genre VARCHAR(255),
    publisher VARCHAR(255),
    reader_id BIGINT 
);