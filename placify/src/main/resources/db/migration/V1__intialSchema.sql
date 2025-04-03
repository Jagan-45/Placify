-- 1. Parent Tables with no dependencies
CREATE TABLE departments (
    dept_id INT PRIMARY KEY,
    dept_name VARCHAR(255) NOT NULL
);

CREATE TABLE batches (
    batch_id SERIAL PRIMARY KEY,
    batch_name VARCHAR(255) NOT NULL
);

CREATE TABLE languages (
    language_id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    judge0_id INT NOT NULL UNIQUE
);

-- 2. Users table (depends on departments and batches)
CREATE TABLE users_table (
    user_id UUID PRIMARY KEY,
    username VARCHAR(255),
    password VARCHAR(255),
    mailid VARCHAR(255) NOT NULL UNIQUE,
    role TEXT NOT NULL,
    enabled BOOLEAN,
    year INT,
    dept_id INT,
    batch_id INT,
    CONSTRAINT fk_users_department FOREIGN KEY (dept_id) REFERENCES departments(dept_id),
    CONSTRAINT fk_users_batch FOREIGN KEY (batch_id) REFERENCES batches(batch_id)
);

-- 3. Problems table (depends on users_table)
CREATE TABLE problems (
    problem_id UUID PRIMARY KEY,
    problem_name VARCHAR(255) NOT NULL,
    problem_slug VARCHAR(255) NOT NULL,
    description VARCHAR(1000) NOT NULL,
    correct_code VARCHAR(3500),
    points INT NOT NULL,
    constrains TEXT,
    input_fields TEXT,
    output_field TEXT,
    visible BOOLEAN,
    created_by UUID NOT NULL,
    CONSTRAINT fk_problems_users FOREIGN KEY (created_by) REFERENCES users_table(user_id)
);

-- 4. Testcases table (depends on problems)
CREATE TABLE testcases (
    tc_id UUID PRIMARY KEY,
    tc_name VARCHAR(255),
    explanation VARCHAR(500),
    sample BOOLEAN,
    problem_id UUID NOT NULL,
    CONSTRAINT fk_testcases_problem FOREIGN KEY (problem_id) REFERENCES problems(problem_id) ON DELETE CASCADE,
    CONSTRAINT unique_testcases UNIQUE (tc_name, problem_id)
);

-- 5. Tasks table (depends on users_table)
CREATE TABLE tasks (
    id UUID PRIMARY KEY,
    completed BOOLEAN NOT NULL DEFAULT FALSE,
    created_at DATE NOT NULL,
    assigned_at DATE,
    completed_at DATE,
    assigned_to UUID,
    assigned_by UUID,
    CONSTRAINT fk_tasks_assigned_to FOREIGN KEY (assigned_to) REFERENCES users_table(user_id),
    CONSTRAINT fk_tasks_assigned_by FOREIGN KEY (assigned_by) REFERENCES users_table(user_id)
);

-- 6. Problem Links table (depends on tasks)
CREATE TABLE problem_links (
    id UUID PRIMARY KEY,
    link VARCHAR(255) NOT NULL,
    solved BOOLEAN,
    attempted BOOLEAN,
    ac_rate DOUBLE PRECISION,
    difficulty VARCHAR(255),
    task_id UUID NOT NULL,
    CONSTRAINT fk_problem_links_task FOREIGN KEY (task_id) REFERENCES tasks(id) ON DELETE CASCADE
);

-- 7. Contests table (depends on users_table)
CREATE TABLE contests (
    contest_id UUID PRIMARY KEY,
    contest_name VARCHAR(255) NOT NULL,
    create_date TIMESTAMP NOT NULL,
    start_time TIMESTAMP NOT NULL,
    end_time TIMESTAMP NOT NULL,
    status TEXT,
    created_by UUID NOT NULL,
    CONSTRAINT fk_contests_created_by FOREIGN KEY (created_by) REFERENCES users_table(user_id)
);

-- 8. Submissions table (depends on users_table, problems, and contests)
CREATE TABLE submissions (
    submission_id UUID PRIMARY KEY,
    code VARCHAR(4500) NOT NULL,
    status VARCHAR(255) NOT NULL,
    runtime FLOAT,
    submission_time TIMESTAMP NOT NULL,
    user_id UUID NOT NULL,
    problem_id UUID NOT NULL,
    contest_id UUID,
    CONSTRAINT fk_submissions_user FOREIGN KEY (user_id) REFERENCES users_table(user_id),
    CONSTRAINT fk_submissions_problem FOREIGN KEY (problem_id) REFERENCES problems(problem_id),
    CONSTRAINT fk_submissions_contest FOREIGN KEY (contest_id) REFERENCES contests(contest_id)
);

-- 9. Contest Submissions table (depends on users_table, problems, and contests)
CREATE TABLE contest_submissions (
    contest_submission_id UUID PRIMARY KEY,
    contest_submission_time FLOAT NOT NULL,
    user_id UUID NOT NULL,
    problem_id UUID NOT NULL,
    contest_id UUID NOT NULL,
    CONSTRAINT fk_contest_submissions_user FOREIGN KEY (user_id) REFERENCES users_table(user_id),
    CONSTRAINT fk_contest_submissions_problem FOREIGN KEY (problem_id) REFERENCES problems(problem_id),
    CONSTRAINT fk_contest_submissions_contest FOREIGN KEY (contest_id) REFERENCES contests(contest_id),
    CONSTRAINT unique_contest_submission UNIQUE (user_id, problem_id, contest_id)
);

-- 10. Contest Ranks table (depends on users_table)
CREATE TABLE contest_ranks (
    contest_rank_id UUID PRIMARY KEY,
    rank INT NOT NULL,
    user_id UUID NOT NULL,
    CONSTRAINT fk_contest_ranks_user FOREIGN KEY (user_id) REFERENCES users_table(user_id)
);

-- 11. Leaderboard table (depends on users_table)
CREATE TABLE leaderboard (
    id UUID PRIMARY KEY,
    overall_rating INT DEFAULT 0,
    contest_rating INT DEFAULT 0,
    task_streak INT DEFAULT 0,
    level TEXT NOT NULL,
    user_id UUID NOT NULL,
    CONSTRAINT fk_leaderboard_user FOREIGN KEY (user_id) REFERENCES users_table(user_id)
);

-- 12. Join Table: contest_user (depends on contests and users_table)
CREATE TABLE contest_user (
    contest_id UUID NOT NULL,
    user_id UUID NOT NULL,
    PRIMARY KEY (contest_id, user_id),
    CONSTRAINT fk_contest_user_contest FOREIGN KEY (contest_id) REFERENCES contests(contest_id),
    CONSTRAINT fk_contest_user_user FOREIGN KEY (user_id) REFERENCES users_table(user_id)
);

-- 13. Join Table: contest_problem (depends on contests and problems)
CREATE TABLE contest_problem (
    contest_id UUID NOT NULL,
    problem_id UUID NOT NULL,
    PRIMARY KEY (contest_id, problem_id),
    CONSTRAINT fk_contest_problem_contest FOREIGN KEY (contest_id) REFERENCES contests(contest_id),
    CONSTRAINT fk_contest_problem_problem FOREIGN KEY (problem_id) REFERENCES problems(problem_id)
);
