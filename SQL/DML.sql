-- MEMBER
INSERT INTO Member (name, email, phone, dob, gender, target_weight)
VALUES
('Alice Johnson', 'alice@example.com', '555-1010', '1995-04-10', 'Female', 55.0),
('Bob Smith', 'bob@example.com', '555-2020', '1990-08-15', 'Male', 75.0),
('Charlie Lee', 'charlie@example.com', '555-3030', '1998-01-20', 'Male', 68.0),
('Diana Park', 'diana@example.com', '555-4040', '1993-12-01', 'Female', 60.0);

-- TRAINER
INSERT INTO Trainer (name, email, phone)
VALUES
('Trainer Amy', 'amy.trainer@example.com', '777-1111'),
('Trainer John', 'john.trainer@example.com', '777-2222'),
('Trainer Kevin', 'kevin.trainer@example.com', '777-3333');

-- PT SESSION (all sessions scheduled in the future for clean demo)
INSERT INTO PTSession (member_id, trainer_id, start_time, end_time, notes)
VALUES
(1, 1, NOW() + INTERVAL '1 day', NOW() + INTERVAL '1 day' + INTERVAL '1 hour', 'Leg workout'),
(2, 2, NOW() + INTERVAL '2 days', NOW() + INTERVAL '2 days' + INTERVAL '1 hour', 'Upper body'),
(3, 1, NOW() + INTERVAL '3 days', NOW() + INTERVAL '3 days' + INTERVAL '1 hour', 'Follow-up session'),
(1, 3, NOW() + INTERVAL '5 days', NOW() + INTERVAL '5 days' + INTERVAL '1 hour', 'Cardio program');


-- HEALTH METRIC (multiple entries to test “latest metric” in the VIEW)
-- Different recorded_at timestamps to avoid ties
INSERT INTO HealthMetric (member_id, weight, heart_rate, recorded_at)
VALUES
(1, 56.2, 70, '2025-01-01 10:00:00'),
(1, 55.8, 72, '2025-01-01 10:01:00'),

(2, 74.5, 68, '2025-01-01 11:00:00'),
(2, 74.0, 67, '2025-01-01 11:01:00'),

(3, 69.1, 75, '2025-01-01 12:00:00');

-- BILLING
INSERT INTO Billing (member_id, amount, status)
VALUES
(1, 120.00, 'paid'),
(2, 89.99, 'unpaid'),
(3, 150.50, 'unpaid'),
(4, 60.00, 'paid');

-- EQUIPMENT
INSERT INTO Equipment (name, room)
VALUES
('Treadmill A', 'Room 101'),
('Bench Press B', 'Room 102'),
('Stationary Bike C', 'Room 103');

-- MAINTENANCE LOG
-- Create one resolved + one open
-- Trigger will auto-set resolved_at
INSERT INTO MaintenanceLog (equipment_id, description, status)
VALUES
(1, 'Belt noise heard during usage', 'open'),
(2, 'Loose screws found', 'resolved'),
(3, 'Pedal resistance inconsistent', 'open');

-- To fire trigger (change open -> resolved)
UPDATE MaintenanceLog
SET status = 'resolved'
WHERE log_id = 1;
