CREATE TABLE tasks_scheduled (
    task_scheduled_id UUID PRIMARY KEY,
    from_date DATE NOT NULL,
    to_date DATE NOT NULL,
    schedule_time TIME NOT NULL,
    cron_expression TEXT NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    job_key VARCHAR(255),
    trigger_key VARCHAR(255),
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);
ALTER TABLE tasks
ADD COLUMN associated_task UUID NOT NULL,
ADD CONSTRAINT fk_associated_task
    FOREIGN KEY (associated_task)
    REFERENCES tasks_scheduled(task_scheduled_id);

