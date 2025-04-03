ALTER TABLE batches DROP CONSTRAINT fk_batch_task_scheduled;

ALTER TABLE batches DROP COLUMN associated_task;

CREATE TABLE task_scheduled_batches (
    task_scheduled_id UUID NOT NULL,
    batch_id INT NOT NULL,
    PRIMARY KEY (task_scheduled_id, batch_id),
    FOREIGN KEY (task_scheduled_id) REFERENCES tasks_scheduled(task_scheduled_id) ON DELETE CASCADE,
    FOREIGN KEY (batch_id) REFERENCES batches(batch_id) ON DELETE CASCADE
);
