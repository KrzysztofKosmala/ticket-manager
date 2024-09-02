-- Wstawienie przykładowych Kategorii
INSERT INTO category (id, name, description, slug)
VALUES
    (111, 'Concert', 'Live concert event.', 'concert-a'),
    (222, 'Workshop', 'Interactive workshop.', 'workshop-b');

-- Wstawienie przykładowych Eventów
INSERT INTO event (id, capacity, title, description, slug, category_id)
VALUES
    (111, 100, 'Concert A', 'Live concert event.', 'concert-a', 111),
    (222, 50, 'Workshop B', 'Interactive workshop.', 'workshop-b', 222);

-- Wstawienie przykładowych Event Occurrences
INSERT INTO event_occurrence (id, event_id, date, time, space_left)
VALUES
    (111, 111, '2024-08-19', '18:00:00', 100),
    (222, 111, '2024-08-26', '18:00:00', 100),
    (333, 222, '2024-09-02', '14:00:00', 50);

-- Wstawienie przykładowych Ticketów
INSERT INTO ticket (id,event_occurrence_id, event_id, type, price, amount)
VALUES
    (111, 111, 111, 'FULL_PRICE', 49.99, 50),
    (222, 111, 111, 'HALF_PRICE', 99.99, 30),
    (333, 222, 111, 'FULL_PRICE', 49.99, 50),
    (444, 222, 111, 'HALF_PRICE', 99.99, 30),
    (555, 333, 222, 'FULL_PRICE', 49.99, 50),
    (666, 333, 222, 'HALF_PRICE', 99.99, 30)
