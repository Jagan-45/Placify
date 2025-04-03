ALTER TABLE tasks_scheduled
DROP COLUMN status;

ALTER TABLE tasks
DROP CONSTRAINT fk_associated_task;

ALTER TABLE tasks
ADD CONSTRAINT fk_associated_task
    FOREIGN KEY (associated_task)
    REFERENCES tasks_scheduled(task_scheduled_id)
    ON DELETE CASCADE;