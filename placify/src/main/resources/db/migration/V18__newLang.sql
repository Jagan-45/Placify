CREATE TABLE languages (
    language_id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    judge0_id INT NOT NULL UNIQUE,
    is_archived BOOLEAN
);