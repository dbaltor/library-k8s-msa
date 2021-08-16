CREATE TABLE book_db (
    id BIGINT IDENTITY NOT NULL,
    name VARCHAR(255),
    author VARCHAR(255),
    genre VARCHAR(255),
    publisher VARCHAR(255),
    reader_id BIGINT 
)
