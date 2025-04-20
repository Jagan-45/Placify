ALTER TABLE tasks_scheduled
ADD COLUMN task_name VARCHAR(255);

ALTER TABLE contest_submissions
ADD COLUMN code TEXT;