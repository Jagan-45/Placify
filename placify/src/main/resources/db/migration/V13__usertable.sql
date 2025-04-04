CREATE TABLE IF NOT EXISTS  users_table (
    user_id UUID PRIMARY KEY,
    username VARCHAR(255),
    password VARCHAR(255),
    mailid VARCHAR(255) NOT NULL UNIQUE,
    role TEXT NOT NULL,
    enabled BOOLEAN,
    year INT,
    dept_id INT,
    batch_id INT,
    CONSTRAINT fk_users_department FOREIGN KEY (dept_id) REFERENCES departments(dept_id),
    CONSTRAINT fk_users_batch FOREIGN KEY (batch_id) REFERENCES batches(batch_id)
);
