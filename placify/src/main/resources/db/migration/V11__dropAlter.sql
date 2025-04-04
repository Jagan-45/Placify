DROP TABLE contest_ranks;

ALTER TABLE users_table
ADD CONSTRAINT unique_username UNIQUE (username);

CREATE TABLE verification_tokens (
    token_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    token_type VARCHAR(50) NOT NULL,
    token VARCHAR(255) NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    user_id UUID NOT NULL,
    CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES users_table(user_id) ON DELETE CASCADE
);

