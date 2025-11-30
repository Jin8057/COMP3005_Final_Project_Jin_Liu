DROP TABLE IF EXISTS MaintenanceLog CASCADE;
DROP TABLE IF EXISTS Equipment CASCADE;
DROP TABLE IF EXISTS Billing CASCADE;
DROP TABLE IF EXISTS PTSession CASCADE;
DROP TABLE IF EXISTS HealthMetric CASCADE;
DROP TABLE IF EXISTS Trainer CASCADE;
DROP TABLE IF EXISTS Member CASCADE;

CREATE TABLE Member (
    member_id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(150) UNIQUE NOT NULL,
    phone VARCHAR(20),
    dob DATE,
    gender VARCHAR(20),
    target_weight NUMERIC(5,2),
    created_at TIMESTAMP DEFAULT NOW()
);

CREATE TABLE Trainer (
    trainer_id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(150),
    phone VARCHAR(20)
);

CREATE TABLE PTSession (
    session_id SERIAL PRIMARY KEY,
    member_id INT NOT NULL REFERENCES Member(member_id) ON DELETE CASCADE,
    trainer_id INT NOT NULL REFERENCES Trainer(trainer_id) ON DELETE CASCADE,
    start_time TIMESTAMP NOT NULL,
    end_time TIMESTAMP NOT NULL,
    notes TEXT
);

CREATE TABLE HealthMetric (
    metric_id SERIAL PRIMARY KEY,
    member_id INT NOT NULL REFERENCES Member(member_id) ON DELETE CASCADE,
    weight NUMERIC(5,2),
    heart_rate INT,
    recorded_at TIMESTAMP DEFAULT NOW()
);


CREATE TABLE Billing (
    bill_id SERIAL PRIMARY KEY,
    member_id INT NOT NULL REFERENCES Member(member_id) ON DELETE CASCADE,
    amount NUMERIC(10,2) NOT NULL,
    status VARCHAR(20) DEFAULT 'unpaid',
    created_at TIMESTAMP DEFAULT NOW()
);

CREATE TABLE Equipment (
    equipment_id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    room VARCHAR(100)
);

CREATE TABLE MaintenanceLog (
    log_id SERIAL PRIMARY KEY,
    equipment_id INT NOT NULL REFERENCES Equipment(equipment_id) ON DELETE CASCADE,
    description TEXT NOT NULL,
    status VARCHAR(20) DEFAULT 'open',
    reported_at TIMESTAMP DEFAULT NOW(),
    resolved_at TIMESTAMP
);

-- VIEW: MEMBER + LATEST METRIC
CREATE OR REPLACE VIEW MemberLatestMetric AS
SELECT 
    m.member_id,
    m.name,
    m.target_weight,
    hm.weight,
    hm.heart_rate,
    hm.recorded_at AS latest_recorded_at
FROM Member m
LEFT JOIN HealthMetric hm
    ON hm.member_id = m.member_id
    AND hm.recorded_at = (
        SELECT MAX(recorded_at)
        FROM HealthMetric
        WHERE member_id = m.member_id
   );


-- TRIGGER: AUTO-SET resolved_at WHEN status = 'resolved'
CREATE OR REPLACE FUNCTION set_resolved_time()
RETURNS trigger 
LANGUAGE plpgsql
AS 
$$
BEGIN
    IF NEW.status = 'resolved' AND OLD.status <> 'resolved' THEN
        NEW.resolved_at = NOW();
    END IF;
    RETURN NEW;
END;
$$;

CREATE TRIGGER trg_set_resolved_time
BEFORE UPDATE ON MaintenanceLog
FOR EACH ROW
EXECUTE FUNCTION set_resolved_time();

-- INDEX: SPEED UP TRAINER LOOKUP (Member.name)
CREATE INDEX idx_member_name
ON Member(name);