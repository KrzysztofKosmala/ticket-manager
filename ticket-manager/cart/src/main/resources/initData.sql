-- insert sample data for cart
INSERT INTO cart (id, created) VALUES (1, '2024-10-06T20:38:40');
-- insert sample data for cart_item
INSERT INTO cart_item (id, product_id, cart_id, quantity, product_price) VALUES (1, 1, 1, 2, 50.0);
INSERT INTO cart_item (id, product_id, cart_id, quantity, product_price) VALUES (2, 3, 1, 1, 50.0);
