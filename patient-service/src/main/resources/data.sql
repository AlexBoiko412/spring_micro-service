CREATE TABLE IF NOT EXISTS patient
(
    id              UUID PRIMARY KEY,
    name            VARCHAR(255)        NOT NULL,
    email           VARCHAR(255) UNIQUE NOT NULL,
    address         VARCHAR(255)        NOT NULL,
    date_of_birth   DATE                NOT NULL,
    registered_date DATE                NOT NULL
    );

INSERT INTO patient (id, name, email, address, date_of_birth, registered_date)
SELECT '550e8400-e29b-41d4-a716-446655440000', 'Michael Johnson', 'michael.johnson@example.com', '742 Evergreen Terrace, Springfield', '1980-06-15', '2023-12-15'
    WHERE NOT EXISTS (SELECT 1 FROM patient WHERE id = '550e8400-e29b-41d4-a716-446655440000');

INSERT INTO patient (id, name, email, address, date_of_birth, registered_date)
SELECT '550e8400-e29b-41d4-a716-446655440001', 'Emily Davis', 'emily.davis@example.com', '12 Baker Street, London', '1992-08-22', '2024-01-10'
    WHERE NOT EXISTS (SELECT 1 FROM patient WHERE id = '550e8400-e29b-41d4-a716-446655440001');

INSERT INTO patient (id, name, email, address, date_of_birth, registered_date)
SELECT '550e8400-e29b-41d4-a716-446655440002', 'Daniel Martinez', 'daniel.martinez@example.com', '1600 Pennsylvania Ave NW, Washington', '1985-11-30', '2024-02-05'
    WHERE NOT EXISTS (SELECT 1 FROM patient WHERE id = '550e8400-e29b-41d4-a716-446655440002');

INSERT INTO patient (id, name, email, address, date_of_birth, registered_date)
SELECT '550e8400-e29b-41d4-a716-446655440003', 'Sophia Wilson', 'sophia.wilson@example.com', '221B Baker Street, London', '1997-04-18', '2024-03-01'
    WHERE NOT EXISTS (SELECT 1 FROM patient WHERE id = '550e8400-e29b-41d4-a716-446655440003');

INSERT INTO patient (id, name, email, address, date_of_birth, registered_date)
SELECT '550e8400-e29b-41d4-a716-446655440004', 'William Brown', 'william.brown@example.com', '10 Downing Street, London', '1975-09-25', '2023-11-20'
    WHERE NOT EXISTS (SELECT 1 FROM patient WHERE id = '550e8400-e29b-41d4-a716-446655440004');

INSERT INTO patient (id, name, email, address, date_of_birth, registered_date)
SELECT '550e8400-e29b-41d4-a716-446655440005', 'Olivia Taylor', 'olivia.taylor@example.com', '500 Fifth Ave, New York', '1990-02-14', '2024-02-18'
    WHERE NOT EXISTS (SELECT 1 FROM patient WHERE id = '550e8400-e29b-41d4-a716-446655440005');

INSERT INTO patient (id, name, email, address, date_of_birth, registered_date)
SELECT '550e8400-e29b-41d4-a716-446655440006', 'James Anderson', 'james.anderson@example.com', '350 Fifth Ave, New York', '1983-07-09', '2023-10-12'
    WHERE NOT EXISTS (SELECT 1 FROM patient WHERE id = '550e8400-e29b-41d4-a716-446655440006');

INSERT INTO patient (id, name, email, address, date_of_birth, registered_date)
SELECT '550e8400-e29b-41d4-a716-446655440007', 'Isabella Thomas', 'isabella.thomas@example.com', '1 Infinite Loop, Cupertino', '1995-06-22', '2024-03-05'
    WHERE NOT EXISTS (SELECT 1 FROM patient WHERE id = '550e8400-e29b-41d4-a716-446655440007');

INSERT INTO patient (id, name, email, address, date_of_birth, registered_date)
SELECT '550e8400-e29b-41d4-a716-446655440008', 'Ethan Harris', 'ethan.harris@example.com', '1600 Amphitheatre Parkway, Mountain View', '1988-09-14', '2024-01-30'
    WHERE NOT EXISTS (SELECT 1 FROM patient WHERE id = '550e8400-e29b-41d4-a716-446655440008');

INSERT INTO patient (id, name, email, address, date_of_birth, registered_date)
SELECT '550e8400-e29b-41d4-a716-446655440009', 'Mia Clark', 'mia.clark@example.com', '350 Mission Street, San Francisco', '2000-12-03', '2024-02-22'
    WHERE NOT EXISTS (SELECT 1 FROM patient WHERE id = '550e8400-e29b-41d4-a716-446655440009');
