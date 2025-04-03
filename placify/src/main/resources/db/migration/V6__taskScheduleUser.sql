ALTER TABLE tasks_scheduled
ADD COLUMN created_by UUID,
ADD CONSTRAINT fk_created_by
    FOREIGN KEY (created_by)
    REFERENCES users_table(user_id)
    ON DELETE SET NULL;

ALTER TABLE tasks_scheduled
ADD COLUMN repeat VARCHAR(50) NOT NULL;


ALTER TABLE batches
ADD COLUMN associated_task UUID;

ALTER TABLE batches
ADD CONSTRAINT fk_batch_task_scheduled
FOREIGN KEY (associated_task)
REFERENCES tasks_scheduled(task_scheduled_id)
ON DELETE CASCADE;