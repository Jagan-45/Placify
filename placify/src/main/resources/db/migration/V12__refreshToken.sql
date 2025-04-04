CREATE TABLE refresh_token (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    token VARCHAR(255) NOT NULL,
    refresh_token_expiry_time TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    logged_in BOOLEAN NOT NULL,
    user_id UUID NOT NULL UNIQUE,
    CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES users_table(user_id) ON DELETE CASCADE
);