-- Wstawienie przykładowych Eventów
INSERT INTO category (id, name, description, slug)
VALUES
    (4, 'Concert', 'Live concert event.', 'concert-a'),
    (5, 'Workshop', 'Interactive workshop.', 'workshop-b');

-- Wstawienie przykładowych Eventów
INSERT INTO event (id, capacity, title, description, slug, category_id)
VALUES
    (1, 100, 'Concert A', 'Live concert event.', 'concert-a', 4),
    (2, 50, 'Workshop B', 'Interactive workshop.', 'workshop-b', 5);

-- Wstawienie przykładowych Event Occurrences
INSERT INTO event_occurrence (id, event_id, date, time, space_left)
VALUES
    (1, 1, '2024-08-19', '18:00:00', 100),
    (2, 1, '2024-08-26', '18:00:00', 100),
    (3, 2, '2024-09-02', '14:00:00', 50);

-- Wstawienie przykładowych Ticketów
INSERT INTO admin_ticket (id,event_occurrence_id, event_id, type, price, amount)
VALUES
    (1, 1, 1, 'FULL_PRICE', 49.99, 50),
    (2, 1, 1, 'HALF_PRICE', 99.99, 30),
    (3, 2, 1, 'FULL_PRICE', 49.99, 50),
    (4, 2, 1, 'HALF_PRICE', 99.99, 30),
    (5, 3, 2, 'FULL_PRICE', 49.99, 50),
    (6, 3, 2, 'HALF_PRICE', 99.99, 30)
