-- ============================================================
-- Group 22 – Urban Mobility System
-- MS1 Schema + Sample Data

USE mobility_db;

-- ── Tables ──────────────────────────────────────────────────

CREATE TABLE IF NOT EXISTS station (
    id             INT AUTO_INCREMENT PRIMARY KEY,
    street_address VARCHAR(255) NOT NULL,
    gps_coordinates VARCHAR(100) NOT NULL,
    total_docks    INT NOT NULL
);

CREATE TABLE IF NOT EXISTS vehicle (
    id            INT AUTO_INCREMENT PRIMARY KEY,
    vin           VARCHAR(17) UNIQUE NOT NULL,
    battery_level INT CHECK (battery_level BETWEEN 0 AND 100),
    model_type    VARCHAR(50),
    station_id    INT NOT NULL,
    available BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (station_id) REFERENCES station(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS `user` (
    id             INT AUTO_INCREMENT PRIMARY KEY,
    email_address  VARCHAR(100) UNIQUE NOT NULL,
    payment_method VARCHAR(50)
);

CREATE TABLE IF NOT EXISTS rental (
    id         INT AUTO_INCREMENT PRIMARY KEY,
    user_id    INT NOT NULL,
    vehicle_id INT NOT NULL,
    start_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    end_time   TIMESTAMP NULL,
    FOREIGN KEY (user_id)    REFERENCES `user`(id),
    FOREIGN KEY (vehicle_id) REFERENCES vehicle(id),
    CHECK (end_time IS NULL OR end_time > start_time)
);

CREATE TABLE IF NOT EXISTS employee (
    id            INT AUTO_INCREMENT PRIMARY KEY,
    name          VARCHAR(100) NOT NULL,
    hire_date     DATE NOT NULL,
    supervisor_id INT,
    FOREIGN KEY (supervisor_id) REFERENCES employee(id)
);

CREATE TABLE IF NOT EXISTS technician (
    employee_id        INT PRIMARY KEY,
    toolkit_id         VARCHAR(50),
    certification_level VARCHAR(50),
    FOREIGN KEY (employee_id) REFERENCES employee(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS safety_officer (
    employee_id     INT PRIMARY KEY,
    radio_channel   VARCHAR(10),
    shift_schedule  VARCHAR(50),
    FOREIGN KEY (employee_id) REFERENCES employee(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS maintenance_log (
    id           INT AUTO_INCREMENT PRIMARY KEY,
    log_number   INT NOT NULL,
    repair_date  DATE NOT NULL,
    service_cost DECIMAL(10,2),
    vehicle_id   INT NOT NULL,
    technician_id INT NOT NULL,
    FOREIGN KEY (vehicle_id)    REFERENCES vehicle(id) ON DELETE CASCADE,
    FOREIGN KEY (technician_id) REFERENCES technician(employee_id),
    UNIQUE (vehicle_id, log_number)
);

-- ── Sample Data ──────────────────────────────────────────────

INSERT INTO station (street_address, gps_coordinates, total_docks) VALUES
('Mariahilfer Str. 1, Vienna',   '48.1975, 16.3399', 20),
('Stephansplatz 3, Vienna',      '48.2085, 16.3731', 15),
('Prater Hauptallee 5, Vienna',  '48.2167, 16.3956', 10);

INSERT INTO vehicle (vin, battery_level, model_type, station_id) VALUES
('1HGBH41JXMN109186', 85, 'E-Scooter',  1),
('2T1BURHE0JC037972', 60, 'E-Bike',     1),
('3VWFE21C04M000001', 45, 'E-Scooter',  2),
('4T1BF1FK5CU512345', 90, 'E-Moped',    2),
('5YJSA1DN1DFP14862', 30, 'E-Bike',     3);

INSERT INTO `user` (email_address, payment_method) VALUES
('alice@example.com',   'Credit Card'),
('bob@example.com',     'PayPal'),
('charlie@example.com', 'Credit Card');

INSERT INTO rental (user_id, vehicle_id, start_time, end_time) VALUES
(1, 1, '2026-04-01 08:00:00', '2026-04-01 09:00:00'),
(2, 3, '2026-04-02 10:00:00', '2026-04-02 10:45:00'),
(3, 2, '2026-04-03 14:00:00', '2026-04-03 15:30:00'),
(1, 4, '2026-04-04 07:30:00', '2026-04-04 08:00:00');

INSERT INTO employee (name, hire_date, supervisor_id) VALUES
('Manager Omar',    '2020-01-15', NULL),
('Tech Ali',        '2021-06-01', 1),
('Tech Sara',       '2022-03-10', 1),
('Officer Hamid',   '2021-09-20', 1);

INSERT INTO technician (employee_id, toolkit_id, certification_level) VALUES
(2, 'TK-001', 'Level 2'),
(3, 'TK-002', 'Level 1');

INSERT INTO safety_officer (employee_id, radio_channel, shift_schedule) VALUES
(4, 'CH-7', 'Morning');

INSERT INTO maintenance_log (log_number, repair_date, service_cost, vehicle_id, technician_id) VALUES
(1, '2026-03-10', 150.00, 1, 2),
(2, '2026-03-15', 200.00, 1, 2),
(1, '2026-03-20', 80.00,  2, 3),
(1, '2026-04-01', 320.00, 3, 2),
(1, '2026-04-05', 50.00,  4, 3);
