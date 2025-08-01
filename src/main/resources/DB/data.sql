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
INSERT INTO order_status_codes (code, display_name, sort_order, is_active, p_time_id)
VALUES ('PENDING', '결제 대기', 1, true, '00000000-0000-0000-0000-000000000001'),
       ('PAID', '결제 완료', 2, true, '00000000-0000-0000-0000-000000000002'),
       ('CONFIRMED', '주문 확인', 3, true, '00000000-0000-0000-0000-000000000003'),
       ('PREPARING', '조리 중', 4, true, '00000000-0000-0000-0000-000000000004'),
       ('READY', '준비 완료', 5, true, '00000000-0000-0000-0000-000000000005'),
       ('DELIVERING', '배달 중', 6, true, '00000000-0000-0000-0000-000000000006'),
       ('COMPLETED', '완료', 7, true, '00000000-0000-0000-0000-000000000001'),
       ('CANCELED', '취소', 8, true, '00000000-0000-0000-0000-000000000002'),
       ('REFUNDED', '환불', 9, true, '00000000-0000-0000-0000-000000000003');

-- 3) 주문 타입 코드
INSERT INTO order_type_codes (code, display_name, sort_order, is_active, p_time_id)
VALUES ('DELIVERY', '배달', 1, true, '00000000-0000-0000-0000-000000000001'),
       ('PICKUP', '픽업', 2, true, '00000000-0000-0000-0000-000000000002'),
       ('DINE_IN', '매장 식사', 3, true, '00000000-0000-0000-0000-000000000003');

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

-- 4-1) 결제 상태 코드
INSERT INTO payment_status_codes (code, display_name, sort_order, is_active, p_time_id)
VALUES ('PENDING', '결제 대기', 1, true, '00000000-0000-0000-0000-000000000001'),
       ('PAID', '결제 완료', 2, true, '00000000-0000-0000-0000-000000000002'),
       ('CANCELED', '결제 취소', 3, true, '00000000-0000-0000-0000-000000000003'),
       ('FAILED', '결제 실패', 4, true, '00000000-0000-0000-0000-000000000004'),
       ('REFUNDED', '환불 완료', 5, true, '00000000-0000-0000-0000-000000000005');

-- 4-2) 결제 방법 코드
INSERT INTO payment_method_codes (code, display_name, sort_order, is_active, p_time_id)
VALUES ('CARD', '카드', 1, true, '00000000-0000-0000-0000-000000000001'),
       ('VIRTUAL_ACCOUNT', '가상계좌', 2, true, '00000000-0000-0000-0000-000000000002'),
       ('TRANSFER', '계좌이체', 3, true, '00000000-0000-0000-0000-000000000003'),
       ('PHONE', '휴대폰', 4, true, '00000000-0000-0000-0000-000000000004'),
       ('GIFT_CERTIFICATE', '상품권', 5, true, '00000000-0000-0000-0000-000000000005'),
       ('POINT', '포인트', 6, true, '00000000-0000-0000-0000-000000000006');

-- 5) Customer 계정 10개
INSERT INTO p_customer (id, name, nickname, email, password, phone_number, points, p_time_id)
VALUES 
    ('11111111-1111-1111-1111-111111111111', '김철수', '철수', '1', '$2a$10$KqNntwd5aFUOPTj1gj62r.8BtmaUeUiae0H7r6Dj8tOlX9HuPgbNS', '010-1111-1111', 5000, '00000000-0000-0000-0000-000000000001'),
    ('22222222-2222-2222-2222-222222222222', '이영희', '영희', '2', '$2a$10$KqNntwd5aFUOPTj1gj62r.8BtmaUeUiae0H7r6Dj8tOlX9HuPgbNS', '010-2222-2222', 3000, '00000000-0000-0000-0000-000000000002'),
    ('33333333-3333-3333-3333-333333333333', '박민수', '민수', '3', '$2a$10$KqNntwd5aFUOPTj1gj62r.8BtmaUeUiae0H7r6Dj8tOlX9HuPgbNS', '010-3333-3333', 8000, '00000000-0000-0000-0000-000000000003'),
    ('44444444-4444-4444-4444-444444444444', '최지영', '지영', '4', '$2a$10$KqNntwd5aFUOPTj1gj62r.8BtmaUeUiae0H7r6Dj8tOlX9HuPgbNS', '010-4444-4444', 2000, '00000000-0000-0000-0000-000000000004'),
    ('55555555-5555-5555-5555-555555555555', '정현우', '현우', '5', '$2a$10$KqNntwd5aFUOPTj1gj62r.8BtmaUeUiae0H7r6Dj8tOlX9HuPgbNS', '010-5555-5555', 10000, '00000000-0000-0000-0000-000000000005'),
    ('66666666-6666-6666-6666-666666666666', '한소영', '소영', '6', '$2a$10$KqNntwd5aFUOPTj1gj62r.8BtmaUeUiae0H7r6Dj8tOlX9HuPgbNS', '010-6666-6666', 1500, '00000000-0000-0000-0000-000000000006'),
    ('77777777-7777-7777-7777-777777777777', '강동현', '동현', '7', '$2a$10$KqNntwd5aFUOPTj1gj62r.8BtmaUeUiae0H7r6Dj8tOlX9HuPgbNS', '010-7777-7777', 7000, '00000000-0000-0000-0000-000000000001'),
    ('88888888-8888-8888-8888-888888888888', '윤미래', '미래', '8', '$2a$10$KqNntwd5aFUOPTj1gj62r.8BtmaUeUiae0H7r6Dj8tOlX9HuPgbNS', '010-8888-8888', 4000, '00000000-0000-0000-0000-000000000002'),
    ('99999999-9999-9999-9999-999999999999', '임태호', '태호', '9', '$2a$10$KqNntwd5aFUOPTj1gj62r.8BtmaUeUiae0H7r6Dj8tOlX9HuPgbNS', '010-9999-9999', 6000, '00000000-0000-0000-0000-000000000003'),
    ('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', '송하나', '하나', '10', '$2a$10$KqNntwd5aFUOPTj1gj62r.8BtmaUeUiae0H7r6Dj8tOlX9HuPgbNS', '010-1010-1010', 9000, '00000000-0000-0000-0000-000000000004');

-- 6) Store 데이터 3개
INSERT INTO p_stores (store_id, store_name, store_address, phone_number, min_cost, description, open_time, close_time, p_time_id)
VALUES 
    ('550e8400-e29b-41d4-a716-446655440000', '맛있는 한식당', '서울시 강남구 테헤란로 123', '02-1234-5678', 0, '맛있는 한식', '09:00:00', '22:00:00', '00000000-0000-0000-0000-000000000001'),
    ('550e8400-e29b-41d4-a716-446655440001', '피자나라', '서울시 서초구 서초대로 456', '02-2345-6789', 0, '신선한 피자', '10:00:00', '23:00:00', '00000000-0000-0000-0000-000000000002'),
    ('550e8400-e29b-41d4-a716-446655440002', '중국집', '서울시 마포구 홍대로 789', '02-3456-7890', 0, '정통 중화요리', '11:00:00', '21:00:00', '00000000-0000-0000-0000-000000000003');

-- 7) Menu Category 데이터
INSERT INTO p_menu_category (code, display_name, sort_order, is_active, p_time_id)
VALUES 
    ('MAIN', '메인요리', 1, true, '00000000-0000-0000-0000-000000000001'),
    ('SIDE', '사이드', 2, true, '00000000-0000-0000-0000-000000000002'),
    ('DRINK', '음료', 3, true, '00000000-0000-0000-0000-000000000003');

-- 8) Menu 데이터 (한식당)
INSERT INTO p_menus (menu_id, store_id, menu_num, menu_name, menu_category_code, price, description, is_available, p_time_id)
VALUES 
    ('550e8400-e29b-41d4-a716-446655440001', '550e8400-e29b-41d4-a716-446655440000', 1, '후라이드치킨', 'MAIN', 18000, '바삭바삭한 후라이드치킨', true, '00000000-0000-0000-0000-000000000001'),
    ('550e8400-e29b-41d4-a716-446655440002', '550e8400-e29b-41d4-a716-446655440000', 2, '양념치킨', 'MAIN', 19000, '매콤달콤한 양념치킨', true, '00000000-0000-0000-0000-000000000002'),
    ('550e8400-e29b-41d4-a716-446655440003', '550e8400-e29b-41d4-a716-446655440000', 3, '콜라 1.25L', 'DRINK', 3000, '시원한 콜라', true, '00000000-0000-0000-0000-000000000003'),
    ('550e8400-e29b-41d4-a716-446655440004', '550e8400-e29b-41d4-a716-446655440000', 4, '김치찌개', 'MAIN', 8000, '진짜 맛있는 김치찌개', true, '00000000-0000-0000-0000-000000000001'),
    ('550e8400-e29b-41d4-a716-446655440005', '550e8400-e29b-41d4-a716-446655440000', 5, '제육볶음', 'MAIN', 12000, '매콤달콤한 제육볶음', true, '00000000-0000-0000-0000-000000000002'),
    ('550e8400-e29b-41d4-a716-446655440006', '550e8400-e29b-41d4-a716-446655440000', 6, '된장찌개', 'MAIN', 7000, '구수한 된장찌개', true, '00000000-0000-0000-0000-000000000003');



