-- ============================================================================
-- SkyBook AI — Seed Data
-- MySQL 8.x
-- Lab passwords (TRAINING ONLY — change if exposing beyond isolated lab):
--   admin     / Admin@123
--   jdoe      / Customer@123
--   asmith    / Customer@123
--   mchen     / Customer@123
--   lwong     / Customer@123
-- ============================================================================

USE skybook;

SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE audit_exports;
TRUNCATE TABLE chat_history;
TRUNCATE TABLE audit_logs;
TRUNCATE TABLE bookings;
TRUNCATE TABLE refresh_tokens;
TRUNCATE TABLE flights;
TRUNCATE TABLE airports;
TRUNCATE TABLE airlines;
TRUNCATE TABLE users;
TRUNCATE TABLE roles;
SET FOREIGN_KEY_CHECKS = 1;

-- ---------------------------------------------------------------------------
-- Roles
-- ---------------------------------------------------------------------------
INSERT INTO roles (id, name, description) VALUES
('11111111-1111-1111-1111-111111111001', 'CUSTOMER', 'Standard traveler — search, book, chat'),
('11111111-1111-1111-1111-111111111002', 'ADMIN',    'Administrator — audit, exports, user oversight');

-- ---------------------------------------------------------------------------
-- Users (BCrypt hashes for Admin@123 / Customer@123)
-- ---------------------------------------------------------------------------
INSERT INTO users (id, username, email, password_hash, first_name, last_name, phone, role_id, status) VALUES
('22222222-2222-2222-2222-222222222001', 'admin',  'admin@skybook.lab',
 '$2b$10$GyDbgujjq1V9WpMyHTmoTuKICUnU2MLK53Jc1dRWH3JSpvijoSGk2',
 'Sky', 'Admin', '+1-555-0100', '11111111-1111-1111-1111-111111111002', 'ACTIVE'),
('22222222-2222-2222-2222-222222222002', 'jdoe',   'jane.doe@example.com',
 '$2b$10$XzIbTCiJw70nNhTj6b9/TuA14oPvoPltW2OwWA0MfaMDfesXBNFBa',
 'Jane', 'Doe', '+1-555-0101', '11111111-1111-1111-1111-111111111001', 'ACTIVE'),
('22222222-2222-2222-2222-222222222003', 'asmith', 'alex.smith@example.com',
 '$2b$10$XzIbTCiJw70nNhTj6b9/TuA14oPvoPltW2OwWA0MfaMDfesXBNFBa',
 'Alex', 'Smith', '+1-555-0102', '11111111-1111-1111-1111-111111111001', 'ACTIVE'),
('22222222-2222-2222-2222-222222222004', 'mchen',  'maya.chen@example.com',
 '$2b$10$XzIbTCiJw70nNhTj6b9/TuA14oPvoPltW2OwWA0MfaMDfesXBNFBa',
 'Maya', 'Chen', '+1-555-0103', '11111111-1111-1111-1111-111111111001', 'ACTIVE'),
('22222222-2222-2222-2222-222222222005', 'lwong',  'liam.wong@example.com',
 '$2b$10$XzIbTCiJw70nNhTj6b9/TuA14oPvoPltW2OwWA0MfaMDfesXBNFBa',
 'Liam', 'Wong', '+1-555-0104', '11111111-1111-1111-1111-111111111001', 'ACTIVE');

-- ---------------------------------------------------------------------------
-- Airlines
-- ---------------------------------------------------------------------------
INSERT INTO airlines (id, code, name, country, logo_url, active) VALUES
('33333333-3333-3333-3333-333333333001', 'SA', 'SkyBook Airways', 'United States', NULL, 1),
('33333333-3333-3333-3333-333333333002', 'UA', 'United Horizon',  'United States', NULL, 1),
('33333333-3333-3333-3333-333333333003', 'DL', 'Delta Crest',     'United States', NULL, 1),
('33333333-3333-3333-3333-333333333004', 'BA', 'British Atlantic','United Kingdom', NULL, 1),
('33333333-3333-3333-3333-333333333005', 'EK', 'Emirates Trail',  'United Arab Emirates', NULL, 1),
('33333333-3333-3333-3333-333333333006', 'SQ', 'Singapore Arc',   'Singapore', NULL, 1),
('33333333-3333-3333-3333-333333333007', 'AF', 'Air France Blue', 'France', NULL, 1),
('33333333-3333-3333-3333-333333333008', 'JL', 'Japan Link',      'Japan', NULL, 1);

-- ---------------------------------------------------------------------------
-- Airports
-- ---------------------------------------------------------------------------
INSERT INTO airports (id, iata_code, icao_code, name, city, country, timezone, latitude, longitude) VALUES
('44444444-4444-4444-4444-444444444001', 'JFK', 'KJFK', 'John F. Kennedy International', 'New York', 'United States', 'America/New_York', 40.6413000, -73.7781000),
('44444444-4444-4444-4444-444444444002', 'LAX', 'KLAX', 'Los Angeles International', 'Los Angeles', 'United States', 'America/Los_Angeles', 33.9425000, -118.4081000),
('44444444-4444-4444-4444-444444444003', 'ORD', 'KORD', 'O''Hare International', 'Chicago', 'United States', 'America/Chicago', 41.9742000, -87.9073000),
('44444444-4444-4444-4444-444444444004', 'SFO', 'KSFO', 'San Francisco International', 'San Francisco', 'United States', 'America/Los_Angeles', 37.6213000, -122.3790000),
('44444444-4444-4444-4444-444444444005', 'MIA', 'KMIA', 'Miami International', 'Miami', 'United States', 'America/New_York', 25.7959000, -80.2870000),
('44444444-4444-4444-4444-444444444006', 'SEA', 'KSEA', 'Seattle-Tacoma International', 'Seattle', 'United States', 'America/Los_Angeles', 47.4502000, -122.3088000),
('44444444-4444-4444-4444-444444444007', 'LHR', 'EGLL', 'London Heathrow', 'London', 'United Kingdom', 'Europe/London', 51.4700000, -0.4543000),
('44444444-4444-4444-4444-444444444008', 'CDG', 'LFPG', 'Paris Charles de Gaulle', 'Paris', 'France', 'Europe/Paris', 49.0097000, 2.5479000),
('44444444-4444-4444-4444-444444444009', 'DXB', 'OMDB', 'Dubai International', 'Dubai', 'United Arab Emirates', 'Asia/Dubai', 25.2532000, 55.3657000),
('44444444-4444-4444-4444-444444444010', 'SIN', 'WSSS', 'Singapore Changi', 'Singapore', 'Singapore', 'Asia/Singapore', 1.3644000, 103.9915000),
('44444444-4444-4444-4444-444444444011', 'NRT', 'RJAA', 'Narita International', 'Tokyo', 'Japan', 'Asia/Tokyo', 35.7720000, 140.3929000),
('44444444-4444-4444-4444-444444444012', 'DEL', 'VIDP', 'Indira Gandhi International', 'New Delhi', 'India', 'Asia/Kolkata', 28.5562000, 77.1000000),
('44444444-4444-4444-4444-444444444013', 'BOM', 'VABB', 'Chhatrapati Shivaji Maharaj International', 'Mumbai', 'India', 'Asia/Kolkata', 19.0896000, 72.8656000),
('44444444-4444-4444-4444-444444444014', 'SYD', 'YSSY', 'Sydney Kingsford Smith', 'Sydney', 'Australia', 'Australia/Sydney', -33.9399000, 151.1753000),
('44444444-4444-4444-4444-444444444015', 'YYZ', 'CYYZ', 'Toronto Pearson International', 'Toronto', 'Canada', 'America/Toronto', 43.6777000, -79.6248000);

-- ---------------------------------------------------------------------------
-- Flights (relative dates from CURRENT_DATE for lab longevity)
-- ---------------------------------------------------------------------------
INSERT INTO flights (
    id, flight_number, airline_id, source_airport_id, dest_airport_id,
    departure_time, arrival_time, duration_minutes, aircraft_type, cabin_class,
    base_price, currency, total_seats, available_seats, status, baggage_allowance_kg
) VALUES
-- Domestic US
('55555555-5555-5555-5555-555555555001', 'SA101', '33333333-3333-3333-3333-333333333001',
 '44444444-4444-4444-4444-444444444001', '44444444-4444-4444-4444-444444444002',
 DATE_ADD(CONCAT(CURRENT_DATE, ' 08:00:00'), INTERVAL 3 DAY),
 DATE_ADD(CONCAT(CURRENT_DATE, ' 11:30:00'), INTERVAL 3 DAY),
 210, 'A321', 'ECONOMY', 249.00, 'USD', 180, 142, 'SCHEDULED', 23),
('55555555-5555-5555-5555-555555555002', 'UA220', '33333333-3333-3333-3333-333333333002',
 '44444444-4444-4444-4444-444444444001', '44444444-4444-4444-4444-444444444002',
 DATE_ADD(CONCAT(CURRENT_DATE, ' 14:15:00'), INTERVAL 3 DAY),
 DATE_ADD(CONCAT(CURRENT_DATE, ' 17:45:00'), INTERVAL 3 DAY),
 210, 'B737', 'ECONOMY', 279.00, 'USD', 162, 98, 'SCHEDULED', 23),
('55555555-5555-5555-5555-555555555003', 'DL415', '33333333-3333-3333-3333-333333333003',
 '44444444-4444-4444-4444-444444444002', '44444444-4444-4444-4444-444444444001',
 DATE_ADD(CONCAT(CURRENT_DATE, ' 09:30:00'), INTERVAL 4 DAY),
 DATE_ADD(CONCAT(CURRENT_DATE, ' 18:00:00'), INTERVAL 4 DAY),
 330, 'A220', 'ECONOMY', 265.00, 'USD', 130, 110, 'SCHEDULED', 23),
('55555555-5555-5555-5555-555555555004', 'SA305', '33333333-3333-3333-3333-333333333001',
 '44444444-4444-4444-4444-444444444003', '44444444-4444-4444-4444-444444444005',
 DATE_ADD(CONCAT(CURRENT_DATE, ' 07:00:00'), INTERVAL 5 DAY),
 DATE_ADD(CONCAT(CURRENT_DATE, ' 11:10:00'), INTERVAL 5 DAY),
 250, 'A320', 'ECONOMY', 189.00, 'USD', 150, 120, 'SCHEDULED', 23),
('55555555-5555-5555-5555-555555555005', 'UA880', '33333333-3333-3333-3333-333333333002',
 '44444444-4444-4444-4444-444444444004', '44444444-4444-4444-4444-444444444006',
 DATE_ADD(CONCAT(CURRENT_DATE, ' 16:40:00'), INTERVAL 2 DAY),
 DATE_ADD(CONCAT(CURRENT_DATE, ' 19:05:00'), INTERVAL 2 DAY),
 145, 'B737', 'ECONOMY', 159.00, 'USD', 162, 55, 'SCHEDULED', 23),
('55555555-5555-5555-5555-555555555006', 'DL702', '33333333-3333-3333-3333-333333333003',
 '44444444-4444-4444-4444-444444444001', '44444444-4444-4444-4444-444444444015',
 DATE_ADD(CONCAT(CURRENT_DATE, ' 10:00:00'), INTERVAL 6 DAY),
 DATE_ADD(CONCAT(CURRENT_DATE, ' 11:45:00'), INTERVAL 6 DAY),
 105, 'A321', 'ECONOMY', 199.00, 'USD', 180, 160, 'SCHEDULED', 23),
-- Transatlantic
('55555555-5555-5555-5555-555555555007', 'BA178', '33333333-3333-3333-3333-333333333004',
 '44444444-4444-4444-4444-444444444001', '44444444-4444-4444-4444-444444444007',
 DATE_ADD(CONCAT(CURRENT_DATE, ' 19:30:00'), INTERVAL 7 DAY),
 DATE_ADD(CONCAT(CURRENT_DATE, ' 07:15:00'), INTERVAL 8 DAY),
 405, 'B777', 'ECONOMY', 620.00, 'USD', 300, 210, 'SCHEDULED', 23),
('55555555-5555-5555-5555-555555555008', 'BA179', '33333333-3333-3333-3333-333333333004',
 '44444444-4444-4444-4444-444444444007', '44444444-4444-4444-4444-444444444001',
 DATE_ADD(CONCAT(CURRENT_DATE, ' 11:00:00'), INTERVAL 14 DAY),
 DATE_ADD(CONCAT(CURRENT_DATE, ' 14:20:00'), INTERVAL 14 DAY),
 440, 'B777', 'ECONOMY', 640.00, 'USD', 300, 245, 'SCHEDULED', 23),
('55555555-5555-5555-5555-555555555009', 'AF006', '33333333-3333-3333-3333-333333333007',
 '44444444-4444-4444-4444-444444444001', '44444444-4444-4444-4444-444444444008',
 DATE_ADD(CONCAT(CURRENT_DATE, ' 18:00:00'), INTERVAL 8 DAY),
 DATE_ADD(CONCAT(CURRENT_DATE, ' 07:30:00'), INTERVAL 9 DAY),
 450, 'A350', 'BUSINESS', 1850.00, 'USD', 48, 22, 'SCHEDULED', 32),
('55555555-5555-5555-5555-555555555010', 'SA501', '33333333-3333-3333-3333-333333333001',
 '44444444-4444-4444-4444-444444444002', '44444444-4444-4444-4444-444444444007',
 DATE_ADD(CONCAT(CURRENT_DATE, ' 15:00:00'), INTERVAL 10 DAY),
 DATE_ADD(CONCAT(CURRENT_DATE, ' 10:30:00'), INTERVAL 11 DAY),
 630, 'A350', 'PREMIUM_ECONOMY', 890.00, 'USD', 80, 61, 'SCHEDULED', 25),
-- Middle East / Asia
('55555555-5555-5555-5555-555555555011', 'EK202', '33333333-3333-3333-3333-333333333005',
 '44444444-4444-4444-4444-444444444001', '44444444-4444-4444-4444-444444444009',
 DATE_ADD(CONCAT(CURRENT_DATE, ' 22:00:00'), INTERVAL 5 DAY),
 DATE_ADD(CONCAT(CURRENT_DATE, ' 19:30:00'), INTERVAL 6 DAY),
 750, 'A380', 'ECONOMY', 980.00, 'USD', 400, 310, 'SCHEDULED', 30),
('55555555-5555-5555-5555-555555555012', 'SQ26',  '33333333-3333-3333-3333-333333333006',
 '44444444-4444-4444-4444-444444444004', '44444444-4444-4444-4444-444444444010',
 DATE_ADD(CONCAT(CURRENT_DATE, ' 23:55:00'), INTERVAL 4 DAY),
 DATE_ADD(CONCAT(CURRENT_DATE, ' 07:40:00'), INTERVAL 6 DAY),
 1005, 'A350', 'ECONOMY', 1120.00, 'USD', 280, 200, 'SCHEDULED', 30),
('55555555-5555-5555-5555-555555555013', 'JL61',  '33333333-3333-3333-3333-333333333008',
 '44444444-4444-4444-4444-444444444004', '44444444-4444-4444-4444-444444444011',
 DATE_ADD(CONCAT(CURRENT_DATE, ' 12:30:00'), INTERVAL 9 DAY),
 DATE_ADD(CONCAT(CURRENT_DATE, ' 16:00:00'), INTERVAL 10 DAY),
 630, 'B787', 'ECONOMY', 860.00, 'USD', 220, 175, 'SCHEDULED', 23),
('55555555-5555-5555-5555-555555555014', 'SA910', '33333333-3333-3333-3333-333333333001',
 '44444444-4444-4444-4444-444444444012', '44444444-4444-4444-4444-444444444001',
 DATE_ADD(CONCAT(CURRENT_DATE, ' 01:15:00'), INTERVAL 11 DAY),
 DATE_ADD(CONCAT(CURRENT_DATE, ' 07:45:00'), INTERVAL 11 DAY),
 870, 'B787', 'ECONOMY', 780.00, 'USD', 240, 190, 'SCHEDULED', 30),
('55555555-5555-5555-5555-555555555015', 'EK508', '33333333-3333-3333-3333-333333333005',
 '44444444-4444-4444-4444-444444444013', '44444444-4444-4444-4444-444444444009',
 DATE_ADD(CONCAT(CURRENT_DATE, ' 04:25:00'), INTERVAL 3 DAY),
 DATE_ADD(CONCAT(CURRENT_DATE, ' 06:10:00'), INTERVAL 3 DAY),
 195, 'B777', 'ECONOMY', 320.00, 'USD', 300, 260, 'SCHEDULED', 30),
('55555555-5555-5555-5555-555555555016', 'SQ221', '33333333-3333-3333-3333-333333333006',
 '44444444-4444-4444-4444-444444444010', '44444444-4444-4444-4444-444444444014',
 DATE_ADD(CONCAT(CURRENT_DATE, ' 20:10:00'), INTERVAL 12 DAY),
 DATE_ADD(CONCAT(CURRENT_DATE, ' 06:05:00'), INTERVAL 13 DAY),
 475, 'A380', 'ECONOMY', 540.00, 'USD', 400, 350, 'SCHEDULED', 30),
-- More domestic / short haul variety for filters
('55555555-5555-5555-5555-555555555017', 'SA118', '33333333-3333-3333-3333-333333333001',
 '44444444-4444-4444-4444-444444444001', '44444444-4444-4444-4444-444444444005',
 DATE_ADD(CONCAT(CURRENT_DATE, ' 06:30:00'), INTERVAL 1 DAY),
 DATE_ADD(CONCAT(CURRENT_DATE, ' 09:45:00'), INTERVAL 1 DAY),
 195, 'A321', 'ECONOMY', 179.00, 'USD', 180, 12, 'SCHEDULED', 23),
('55555555-5555-5555-5555-555555555018', 'UA445', '33333333-3333-3333-3333-333333333002',
 '44444444-4444-4444-4444-444444444003', '44444444-4444-4444-4444-444444444004',
 DATE_ADD(CONCAT(CURRENT_DATE, ' 13:20:00'), INTERVAL 4 DAY),
 DATE_ADD(CONCAT(CURRENT_DATE, ' 15:50:00'), INTERVAL 4 DAY),
 270, 'B737', 'ECONOMY', 210.00, 'USD', 162, 140, 'SCHEDULED', 23),
('55555555-5555-5555-5555-555555555019', 'DL901', '33333333-3333-3333-3333-333333333003',
 '44444444-4444-4444-4444-444444444006', '44444444-4444-4444-4444-444444444002',
 DATE_ADD(CONCAT(CURRENT_DATE, ' 08:15:00'), INTERVAL 6 DAY),
 DATE_ADD(CONCAT(CURRENT_DATE, ' 11:00:00'), INTERVAL 6 DAY),
 165, 'A220', 'FIRST', 920.00, 'USD', 16, 8, 'SCHEDULED', 40),
('55555555-5555-5555-5555-555555555020', 'BA284', '33333333-3333-3333-3333-333333333004',
 '44444444-4444-4444-4444-444444444007', '44444444-4444-4444-4444-444444444008',
 DATE_ADD(CONCAT(CURRENT_DATE, ' 17:00:00'), INTERVAL 2 DAY),
 DATE_ADD(CONCAT(CURRENT_DATE, ' 19:15:00'), INTERVAL 2 DAY),
 75, 'A320', 'ECONOMY', 145.00, 'USD', 150, 90, 'SCHEDULED', 23);

-- ---------------------------------------------------------------------------
-- Bookings
-- ---------------------------------------------------------------------------
INSERT INTO bookings (
    id, booking_reference, user_id, flight_id, status, passenger_count,
    passengers_json, seats, total_amount, currency, contact_email, contact_phone
) VALUES
('66666666-6666-6666-6666-666666666001', 'SBK7A2M9',
 '22222222-2222-2222-2222-222222222002', '55555555-5555-5555-5555-555555555001',
 'CONFIRMED', 1,
 JSON_ARRAY(JSON_OBJECT('firstName','Jane','lastName','Doe','dob','1990-04-12','passport','US998877','seat','12A')),
 '12A', 249.00, 'USD', 'jane.doe@example.com', '+1-555-0101'),
('66666666-6666-6666-6666-666666666002', 'SBK3Q8KP',
 '22222222-2222-2222-2222-222222222003', '55555555-5555-5555-5555-555555555007',
 'CONFIRMED', 2,
 JSON_ARRAY(
   JSON_OBJECT('firstName','Alex','lastName','Smith','dob','1988-11-02','passport','US112233','seat','24C'),
   JSON_OBJECT('firstName','Sam','lastName','Smith','dob','1992-07-19','passport','US445566','seat','24D')
 ),
 '24C,24D', 1240.00, 'USD', 'alex.smith@example.com', '+1-555-0102'),
('66666666-6666-6666-6666-666666666003', 'SBK9T1XW',
 '22222222-2222-2222-2222-222222222004', '55555555-5555-5555-5555-555555555012',
 'CONFIRMED', 1,
 JSON_ARRAY(JSON_OBJECT('firstName','Maya','lastName','Chen','dob','1995-01-28','passport','US778899','seat','18F')),
 '18F', 1120.00, 'USD', 'maya.chen@example.com', '+1-555-0103'),
('66666666-6666-6666-6666-666666666004', 'SBK2H5RV',
 '22222222-2222-2222-2222-222222222005', '55555555-5555-5555-5555-555555555017',
 'CANCELLED', 1,
 JSON_ARRAY(JSON_OBJECT('firstName','Liam','lastName','Wong','dob','1991-09-05','passport','US334455','seat','07B')),
 '07B', 179.00, 'USD', 'liam.wong@example.com', '+1-555-0104'),
('66666666-6666-6666-6666-666666666005', 'SBK6N4JD',
 '22222222-2222-2222-2222-222222222002', '55555555-5555-5555-5555-555555555005',
 'CONFIRMED', 1,
 JSON_ARRAY(JSON_OBJECT('firstName','Jane','lastName','Doe','dob','1990-04-12','passport','US998877','seat','15C')),
 '15C', 159.00, 'USD', 'jane.doe@example.com', '+1-555-0101');

UPDATE bookings
SET cancelled_at = DATE_SUB(NOW(3), INTERVAL 2 DAY),
    cancellation_reason = 'Change of travel plans'
WHERE id = '66666666-6666-6666-6666-666666666004';

-- ---------------------------------------------------------------------------
-- Sample refresh token (revoked — illustrative)
-- ---------------------------------------------------------------------------
INSERT INTO refresh_tokens (id, user_id, token_hash, expires_at, revoked, revoked_at, user_agent, ip_address) VALUES
('77777777-7777-7777-7777-777777777001',
 '22222222-2222-2222-2222-222222222002',
 SHA2('seed-revoked-refresh-token-demo', 256),
 DATE_ADD(NOW(3), INTERVAL 7 DAY),
 1, DATE_SUB(NOW(3), INTERVAL 1 DAY),
 'Mozilla/5.0 (Macintosh; Intel Mac OS X 14_0) Chrome/120.0',
 '127.0.0.1');

-- ---------------------------------------------------------------------------
-- Audit logs
-- ---------------------------------------------------------------------------
INSERT INTO audit_logs (
    id, timestamp, username, user_id, role, ip_address, session_id,
    action, resource, http_method, response_status, browser, operating_system, execution_time_ms, details
) VALUES
('88888888-8888-8888-8888-888888888001', DATE_SUB(NOW(3), INTERVAL 5 DAY), 'jdoe',
 '22222222-2222-2222-2222-222222222002', 'CUSTOMER', '203.0.113.10', 'sess-jdoe-001',
 'USER_LOGIN', '/api/v1/auth/login', 'POST', 200, 'Chrome', 'macOS', 45,
 JSON_OBJECT('result','success')),
('88888888-8888-8888-8888-888888888002', DATE_SUB(NOW(3), INTERVAL 5 DAY), 'jdoe',
 '22222222-2222-2222-2222-222222222002', 'CUSTOMER', '203.0.113.10', 'sess-jdoe-001',
 'BOOKING_CREATED', '/api/v1/bookings', 'POST', 201, 'Chrome', 'macOS', 120,
 JSON_OBJECT('bookingReference','SBK7A2M9')),
('88888888-8888-8888-8888-888888888003', DATE_SUB(NOW(3), INTERVAL 4 DAY), 'asmith',
 '22222222-2222-2222-2222-222222222003', 'CUSTOMER', '198.51.100.22', 'sess-asmith-001',
 'USER_LOGIN', '/api/v1/auth/login', 'POST', 200, 'Firefox', 'Windows', 38,
 JSON_OBJECT('result','success')),
('88888888-8888-8888-8888-888888888004', DATE_SUB(NOW(3), INTERVAL 4 DAY), 'asmith',
 '22222222-2222-2222-2222-222222222003', 'CUSTOMER', '198.51.100.22', 'sess-asmith-001',
 'BOOKING_CREATED', '/api/v1/bookings', 'POST', 201, 'Firefox', 'Windows', 155,
 JSON_OBJECT('bookingReference','SBK3Q8KP')),
('88888888-8888-8888-8888-888888888005', DATE_SUB(NOW(3), INTERVAL 3 DAY), 'mchen',
 '22222222-2222-2222-2222-222222222004', 'CUSTOMER', '192.0.2.44', 'sess-mchen-001',
 'AI_QUERY', '/api/v1/chat', 'POST', 200, 'Safari', 'iOS', 890,
 JSON_OBJECT('intent','baggage')),
('88888888-8888-8888-8888-888888888006', DATE_SUB(NOW(3), INTERVAL 2 DAY), 'lwong',
 '22222222-2222-2222-2222-222222222005', 'CUSTOMER', '203.0.113.88', 'sess-lwong-001',
 'BOOKING_CANCELLED', '/api/v1/bookings/66666666-6666-6666-6666-666666666004/cancel', 'POST', 200, 'Chrome', 'Android', 95,
 JSON_OBJECT('bookingReference','SBK2H5RV')),
('88888888-8888-8888-8888-888888888007', DATE_SUB(NOW(3), INTERVAL 1 DAY), 'jdoe',
 '22222222-2222-2222-2222-222222222002', 'CUSTOMER', '203.0.113.10', 'sess-jdoe-002',
 'PROFILE_UPDATE', '/api/v1/users/me', 'PUT', 200, 'Chrome', 'macOS', 52,
 JSON_OBJECT('fields', JSON_ARRAY('phone'))),
('88888888-8888-8888-8888-888888888008', DATE_SUB(NOW(3), INTERVAL 12 HOUR), 'admin',
 '22222222-2222-2222-2222-222222222001', 'ADMIN', '10.0.0.5', 'sess-admin-001',
 'USER_LOGIN', '/api/v1/auth/login', 'POST', 200, 'Edge', 'Windows', 41,
 JSON_OBJECT('result','success')),
('88888888-8888-8888-8888-888888888009', DATE_SUB(NOW(3), INTERVAL 11 HOUR), 'admin',
 '22222222-2222-2222-2222-222222222001', 'ADMIN', '10.0.0.5', 'sess-admin-001',
 'EXPORT_REQUEST', '/api/v1/admin/audit/export/csv', 'GET', 200, 'Edge', 'Windows', 340,
 JSON_OBJECT('format','CSV','rowCount',8)),
('88888888-8888-8888-8888-888888888010', DATE_SUB(NOW(3), INTERVAL 6 HOUR), 'jdoe',
 '22222222-2222-2222-2222-222222222002', 'CUSTOMER', '203.0.113.10', 'sess-jdoe-003',
 'USER_LOGOUT', '/api/v1/auth/logout', 'POST', 200, 'Chrome', 'macOS', 22,
 JSON_OBJECT('result','success')),
('88888888-8888-8888-8888-888888888011', DATE_SUB(NOW(3), INTERVAL 30 DAY), 'admin',
 '22222222-2222-2222-2222-222222222001', 'ADMIN', '10.0.0.5', 'sess-admin-prev',
 'EXPORT_REQUEST', '/api/v1/admin/audit/export/monthly', 'GET', 200, 'Edge', 'Windows', 512,
 JSON_OBJECT('format','MONTHLY','month','previous')),
('88888888-8888-8888-8888-888888888012', DATE_SUB(NOW(3), INTERVAL 2 DAY), 'asmith',
 '22222222-2222-2222-2222-222222222003', 'CUSTOMER', '198.51.100.22', 'sess-asmith-002',
 'PASSWORD_CHANGE', '/api/v1/users/me/password', 'POST', 200, 'Firefox', 'Windows', 78,
 JSON_OBJECT('result','success'));

-- ---------------------------------------------------------------------------
-- Chat history
-- ---------------------------------------------------------------------------
INSERT INTO chat_history (id, session_id, user_id, role, content, intent, metadata_json) VALUES
('99999999-9999-9999-9999-999999999001', 'chat-mchen-001',
 '22222222-2222-2222-2222-222222222004', 'user',
 'What is the baggage allowance on international flights?', 'baggage', NULL),
('99999999-9999-9999-9999-999999999002', 'chat-mchen-001',
 '22222222-2222-2222-2222-222222222004', 'assistant',
 'For most SkyBook AI international economy bookings, the standard checked baggage allowance is 23–30 kg depending on airline and route. Premium cabins often include 32–40 kg. Always confirm on your booking confirmation.', 'baggage',
 JSON_OBJECT('source','faq')),
('99999999-9999-9999-9999-999999999003', 'chat-jdoe-001',
 '22222222-2222-2222-2222-222222222002', 'user',
 'Find flights from JFK to LAX next week under $300', 'flight_search', NULL),
('99999999-9999-9999-9999-999999999004', 'chat-jdoe-001',
 '22222222-2222-2222-2222-222222222002', 'assistant',
 'I can help with that. Try searching JFK → LAX with a max price of 300. SkyBook Airways SA101 is typically in that range for upcoming departures.', 'flight_search',
 JSON_OBJECT('suggestedRoute', JSON_OBJECT('from','JFK','to','LAX','maxPrice',300))),
('99999999-9999-9999-9999-999999999005', 'chat-asmith-001',
 '22222222-2222-2222-2222-222222222003', 'user',
 'What is your refund policy if I cancel 48 hours before departure?', 'refund', NULL),
('99999999-9999-9999-9999-999999999006', 'chat-asmith-001',
 '22222222-2222-2222-2222-222222222003', 'assistant',
 'Cancellations made at least 48 hours before departure are eligible for a partial refund minus a processing fee. Within 48 hours, fares are typically non-refundable except as required by local regulations. Check your fare rules on the booking page.', 'refund',
 JSON_OBJECT('source','policy'));

-- ---------------------------------------------------------------------------
-- Previous audit exports metadata
-- ---------------------------------------------------------------------------
INSERT INTO audit_exports (
    id, requested_by, export_type, date_from, date_to, filter_username, filter_action, file_name, row_count
) VALUES
('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaa0001',
 '22222222-2222-2222-2222-222222222001', 'CSV',
 DATE_SUB(CURRENT_DATE, INTERVAL 7 DAY), CURRENT_DATE, NULL, NULL,
 'audit_export_last7days.csv', 10),
('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaa0002',
 '22222222-2222-2222-2222-222222222001', 'MONTHLY',
 DATE_FORMAT(DATE_SUB(CURRENT_DATE, INTERVAL 1 MONTH), '%Y-%m-01'),
 LAST_DAY(DATE_SUB(CURRENT_DATE, INTERVAL 1 MONTH)),
 NULL, NULL,
 CONCAT('audit_monthly_', DATE_FORMAT(DATE_SUB(CURRENT_DATE, INTERVAL 1 MONTH), '%Y_%m'), '.xlsx'), 12),
('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaa0003',
 '22222222-2222-2222-2222-222222222001', 'EXCEL',
 DATE_SUB(CURRENT_DATE, INTERVAL 30 DAY), CURRENT_DATE, 'jdoe', NULL,
 'audit_export_jdoe.xlsx', 4);

-- ============================================================================
-- End of seed
-- ============================================================================
