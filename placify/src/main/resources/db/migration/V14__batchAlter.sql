-- Step 1: Remove the default value dependency from batch_id column
ALTER TABLE batches
ALTER COLUMN batch_id DROP DEFAULT;

-- Step 2: Drop the old sequence (if it exists)
DROP SEQUENCE IF EXISTS batches_batch_id_seq;

-- Step 3: Create a new sequence for batch_id (if needed)
CREATE SEQUENCE batches_batch_id_seq
START WITH 1
INCREMENT BY 1;

-- Step 4: Set the new sequence as the default value for batch_id
ALTER TABLE batches
ALTER COLUMN batch_id SET DEFAULT nextval('batches_batch_id_seq');

-- Step 5: Set the sequence to the correct value (if necessary)
SELECT setval('batches_batch_id_seq', (SELECT MAX(batch_id) FROM batches));
