INSERT INTO users (email, role, password, name, surname, birthdate, registered_at)
VALUES ('ivanov_arkadiy@senla.ru', 'USER', '$2a$10$Smy9JptsypUyVBHOTMidTeh3pEZ9UA1uoGv6H5yLhqXXdLMkQsTKK', 'Arkadiy',
        'Ivanov', '1995-05-15', '2025-04-09 10:15:00+03'),
       ('petrova_anna@senla.ru', 'USER', '$2a$10$Smy9JptsypUyVBHOTMidTeh3pEZ9UA1uoGv6H5yLhqXXdLMkQsTKK', 'Anna',
        'Petrova', '1998-08-22', '2025-04-09 11:30:00+03'),
       ('sidorov_dmitry@senla.ru', 'USER', '$2a$10$Smy9JptsypUyVBHOTMidTeh3pEZ9UA1uoGv6H5yLhqXXdLMkQsTKK', 'Dmitry',
        'Sidorov', '1992-11-30', '2025-04-09 12:45:00+03'),
       ('smirnova_elena@senla.ru', 'USER', '$2a$10$Smy9JptsypUyVBHOTMidTeh3pEZ9UA1uoGv6H5yLhqXXdLMkQsTKK', 'Elena',
        'Smirnova', '1990-03-10', '2025-04-09 14:00:00+03'),
       ('kozlov_alexey@senla.ru', 'USER', '$2a$10$Smy9JptsypUyVBHOTMidTeh3pEZ9UA1uoGv6H5yLhqXXdLMkQsTKK', 'Alexey',
        'Kozlov', '2000-01-01', '2025-04-09 15:15:00+03'),
       ('nikolaeva_olga@senla.ru', 'USER', '$2a$10$Smy9JptsypUyVBHOTMidTeh3pEZ9UA1uoGv6H5yLhqXXdLMkQsTKK', 'Olga',
        'Nikolaeva', '1997-07-18', '2025-04-09 16:30:00+03'),
       ('fedorov_maxim@senla.ru', 'USER', '$2a$10$Smy9JptsypUyVBHOTMidTeh3pEZ9UA1uoGv6H5yLhqXXdLMkQsTKK', 'Maxim',
        'Fedorov', '2002-09-25', '2025-04-09 17:45:00+03'),
       ('morozova_ekaterina@senla.ru', 'USER', '$2a$10$Smy9JptsypUyVBHOTMidTeh3pEZ9UA1uoGv6H5yLhqXXdLMkQsTKK',
        'Ekaterina', 'Morozova', '1993-04-05', '2025-04-09 19:00:00+03'),
       ('volkov_andrey@senla.ru', 'USER', '$2a$10$Smy9JptsypUyVBHOTMidTeh3pEZ9UA1uoGv6H5yLhqXXdLMkQsTKK', 'Andrey',
        'Volkov', '2005-12-12', '2025-04-09 20:15:00+03'),
       ('orlova_maria@senla.ru', 'USER', '$2a$10$Smy9JptsypUyVBHOTMidTeh3pEZ9UA1uoGv6H5yLhqXXdLMkQsTKK', 'Maria',
        'Orlova', '2007-06-20', '2025-04-09 21:30:00+03'),
       ('admin@senla.ru', 'ADMIN', '$2a$10$zp4b4MEUJJPBESQka4E7tuHFYnGMAwYa2OqZxLo5LeNX66fLeKa4m', 'Admin', 'Senla',
        '1995-04-20', '2025-04-09 09:00:00+03');


INSERT INTO bank_cards (card_number, masked_number, card_holder, expiry_date, status, balance, user_id)
VALUES
-- Карты для пользователя 1 (Arkadiy Ivanov)
('1234567812345678', '**** **** **** 5678', 'ARKADIY IVANOV', '2025-12-31', 'ACTIVE', 1000.00, 1),
('8765432187654321', '**** **** **** 4321', 'ARKADIY IVANOV', '2024-11-30', 'ACTIVE', 2500.50, 1),

-- Карты для пользователя 2 (Anna Petrova)
('1111222233334444', '**** **** **** 4444', 'ANNA PETROVA', '2026-01-15', 'ACTIVE', 500.75, 2),
('5555666677778888', '**** **** **** 8888', 'ANNA PETROVA', '2023-10-20', 'BLOCKED', 0.00, 2),

-- Карты для пользователя 3 (Dmitry Sidorov)
('9999888877776666', '**** **** **** 6666', 'DMITRY SIDOROV', '2025-05-20', 'ACTIVE', 3000.00, 3),
('4444333322221111', '**** **** **** 1111', 'DMITRY SIDOROV', '2022-08-15', 'EXPIRED', 150.25, 3),

-- Карты для пользователя 4 (Elena Smirnova)
('2222333344445555', '**** **** **** 5555', 'ELENA SMIRNOVA', '2026-03-10', 'ACTIVE', 750.00, 4),

-- Карты для пользователя 5 (Alexey Kozlov)
('6666777788889999', '**** **** **** 9999', 'ALEXEY KOZLOV', '2025-07-25', 'ACTIVE', 1200.00, 5),

-- Карты для пользователя 6 (Olga Nikolaeva)
('1212343456567878', '**** **** **** 7878', 'OLGA NIKOLAEVA', '2024-12-05', 'BLOCKED', 50.00, 6),

-- Карты для пользователя 7 (Maxim Fedorov)
('3434565678789090', '**** **** **** 9090', 'MAXIM FEDOROV', '2026-02-28', 'ACTIVE', 2000.00, 7),

-- Карты для пользователя 8 (Ekaterina Morozova)
('5656787890901212', '**** **** **** 1212', 'EKATERINA MOROZOVA', '2025-09-15', 'ACTIVE', 850.50, 8),

-- Карты для пользователя 9 (Andrey Volkov)
('7878909012123434', '**** **** **** 3434', 'ANDREY VOLKOV', '2024-06-30', 'ACTIVE', 400.00, 9),

-- Карты для пользователя 10 (Maria Orlova)
('9090121234345656', '**** **** **** 5656', 'MARIA ORLOVA', '2026-04-10', 'ACTIVE', 1600.75, 10),

-- Карты для админа (Admin Senla)
('0000111122223333', '**** **** **** 3333', 'ADMIN SENLA', '2027-01-01', 'ACTIVE', 5000.00, 11);

SELECT setval('bank_cards_id_seq', (SELECT MAX(id) FROM bank_cards));