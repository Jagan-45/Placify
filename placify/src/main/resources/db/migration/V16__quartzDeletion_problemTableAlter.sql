ALTER TABLE problems ALTER COLUMN description TYPE TEXT;

DROP TABLE IF EXISTS qrtz_calendars CASCADE;
DROP TABLE IF EXISTS qrtz_job_details CASCADE;
DROP TABLE IF EXISTS qrtz_locks CASCADE;
DROP TABLE IF EXISTS qrtz_scheduler_state CASCADE;
DROP TABLE IF EXISTS qrtz_triggers CASCADE;
DROP TABLE IF EXISTS qrtz_cron_triggers CASCADE;
DROP TABLE IF EXISTS qrtz_fired_triggers CASCADE;
DROP TABLE IF EXISTS qrtz_blob_triggers CASCADE;
DROP TABLE IF EXISTS qrtz_simple_triggers CASCADE;
DROP TABLE IF EXISTS qrtz_simprop_triggers CASCADE;
DROP TABLE IF EXISTS qrtz_paused_trigger_grps CASCADE;

