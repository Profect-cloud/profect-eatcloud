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

-- 2) 주문 상태 코드
INSERT INTO p_order_status_codes (code, name, description)
VALUES ('PENDING', '결제 대기', '주문이 생성되었지만 결제가 완료되지 않은 상태'),
       ('PAID', '결제 완료', '결제가 완료된 상태'),
       ('CONFIRMED', '주문 확인', '매장에서 주문을 확인한 상태'),
       ('PREPARING', '조리 중', '음식을 준비하고 있는 상태'),
       ('READY', '준비 완료', '픽업 또는 배달 준비가 완료된 상태'),
       ('DELIVERING', '배달 중', '배달이 진행 중인 상태'),
       ('COMPLETED', '완료', '주문이 완료된 상태'),
       ('CANCELED', '취소', '주문이 취소된 상태'),
       ('REFUNDED', '환불', '환불이 완료된 상태');

-- 3) 주문 타입 코드
INSERT INTO p_order_type_codes (code, name, description)
VALUES ('DELIVERY', '배달', '배달 주문'),
       ('PICKUP', '픽업', '직접 픽업 주문'),
       ('DINE_IN', '매장 식사', '매장에서 식사하는 주문');

-- 4) Admin 계정 3개
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

