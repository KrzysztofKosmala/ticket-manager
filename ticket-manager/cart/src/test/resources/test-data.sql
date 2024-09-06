INSERT INTO cart (id, created) VALUES
                               (111,'2024-08-15'),
                               (222,'2024-08-16'),
                               (333,'2024-08-17');

-- Wstawianie przykładowych danych do tabeli 'cart_item'
INSERT INTO cart_item (id, cart_id, quantity, ticket_id, ticket_price) VALUES
                                                         (111 ,111, 2, 1, 10),
                                                         (222, 111, 1, 2, 20),
                                                         (333, 222, 1, 1, 10),
                                                         (444, 222, 1, 2, 20),
                                                         (555, 333, 2, 2, 20);

