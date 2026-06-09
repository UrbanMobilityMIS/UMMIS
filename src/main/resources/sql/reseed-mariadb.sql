SET FOREIGN_KEY_CHECKS = 0;

TRUNCATE TABLE maintenance_log;
TRUNCATE TABLE technician;
TRUNCATE TABLE safety_officer;
TRUNCATE TABLE rental;
TRUNCATE TABLE vehicle;
TRUNCATE TABLE `user`;
TRUNCATE TABLE employee;
TRUNCATE TABLE station;

SET FOREIGN_KEY_CHECKS = 1;

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
