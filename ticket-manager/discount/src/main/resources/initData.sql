INSERT INTO discount
(code, discount_type, value, min_order_value, max_discount, valid_from, valid_to, usage_limit, used_count, is_active, created_at, updated_at, user_id)
VALUES
    ('SUMMER20', 'PERCENTAGE', 20.00, 100.00, 50.00, '2025-06-01 00:00:00', '2025-08-31 23:59:59', 500, 10, true, NOW(), NOW(), 101),
    ('WELCOME10', 'PERCENTAGE', 10.00, 50.00, NULL, '2025-01-01 00:00:00', '2025-12-31 23:59:59', 1000, 100, true, NOW(), NOW(), NULL),
    ('FREESHIP', 'FIXED_AMOUNT', 50.00, 200.00, 50.00, '2025-03-01 00:00:00', '2025-03-31 23:59:59', 100, 5, true, NOW(), NOW(), 102),
    ('BLACKFRIDAY', 'PERCENTAGE', 30.00, 150.00, 100.00, '2025-11-25 00:00:00', '2025-11-30 23:59:59', 1000, 50, true, NOW(), NOW(), NULL),
    ('INFLUENCER50', 'FIXED_AMOUNT', 50.00, NULL, NULL, '2025-02-01 00:00:00', '2025-12-31 23:59:59', 500, 20, true, NOW(), NOW(), 103);
