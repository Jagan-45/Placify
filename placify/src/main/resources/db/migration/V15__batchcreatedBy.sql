ALTER TABLE batches
ADD COLUMN created_by UUID;

-- Add foreign key constraint
ALTER TABLE batches
ADD CONSTRAINT fk_created_by FOREIGN KEY (created_by) REFERENCES users_table(user_id);

