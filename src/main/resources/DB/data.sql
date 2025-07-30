-- docker-entrypoint-initdb.d/02-data.sql

-- 1) 시간 데이터(TimeData) 샘플 6개
INSERT INTO p_time
(p_time_id, created_at, created_by, updated_at, updated_by, deleted_at, deleted_by)
VALUES ('00000000-0000-0000-0000-000000000001',
        now(), 'system',
        now(), 'system',
        NULL, NULL),
       ('00000000-0000-0000-0000-000000000002',
        now(), 'system',
        now(), 'system',
        NULL, NULL),
       ('00000000-0000-0000-0000-000000000003',
        now(), 'system',
        now(), 'system',
        NULL, NULL),
       ('00000000-0000-0000-0000-000000000004',
        now(), 'system',
        now(), 'system',
        NULL, NULL),
       ('00000000-0000-0000-0000-000000000005',
        now(), 'system',
        now(), 'system',
        NULL, NULL),
       ('00000000-0000-0000-0000-000000000006',
        now(), 'system',
        now(), 'system',
        NULL, NULL);

-- 2) Admin 계정 3개
INSERT INTO p_admins (id, email, name, password, phone_number, position, p_time_id)
VALUES ('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'admin1@example.com', 'Admin One',
        '$2a$10$KqNntwd5aFUOPTj1gj62r.8BtmaUeUiae0H7r6Dj8tOlX9HuPgbNS',
        '010-1234-5678', 'CEO',
        '00000000-0000-0000-0000-000000000001'),
       ('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 'admin2@example.com', 'Admin Two',
        '$2a$10$KqNntwd5aFUOPTj1gj62r.8BtmaUeUiae0H7r6Dj8tOlX9HuPgbNS',
        '010-2345-6789', 'CTO',
        '00000000-0000-0000-0000-000000000002'),
       ('cccccccc-cccc-cccc-cccc-cccccccccccc', 'admin3@example.com', 'Admin Three',
        '$2a$10$KqNntwd5aFUOPTj1gj62r.8BtmaUeUiae0H7r6Dj8tOlX9HuPgbNS',
        '010-3456-7890', 'CFO',
        '00000000-0000-0000-0000-000000000003');

