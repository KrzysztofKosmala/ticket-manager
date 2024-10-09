-- insert sample data for category
INSERT INTO category (id, name, description, slug) VALUES (1, 'Spektakl', 'spektakl opis', 'spe');
INSERT INTO category (id, name, description, slug) VALUES (2, 'Wystawa', 'wystawa opis', 'wys');

-- insert sample data for event
INSERT INTO event (id, capacity, category_id, title, description, slug) VALUES (1, 100, 1, 'Dziady', 'Dramat Adama Mickiewicza jest uważany za jedno z największych dzieł polskiego i europejskiego romantyzmu', 'dz');
INSERT INTO event (id, capacity, category_id, title, description, slug) VALUES (2, 80, 1, 'Wujaszek Wania', ' Została wydana w 1898 roku i w ciągu kilku lat doczekała się licznych realizacji teatralnych, zarówno zagranicznych, jak i polskich', 'ww');
INSERT INTO event (id, capacity, category_id, title, description, slug) VALUES (3, 60, 1, 'Anioły w Ameryce', 'To przełomowa sztuka amerykańskiego dramatopisarza Tony’ego Kushnera, która zdobyła mnóstwo prestiżowych nagród, m.in. nagrodę Pulitzera, Tony Award i Drama Desk', 'aa');
INSERT INTO event (id, capacity, category_id, title, description, slug) VALUES (4, 100, 2, 'NO RISK NO FUN', 'Wrócicie do lektur Tyrmanda i kuszącego wdzięku lat 50., nauczycie się rozpoznawać warszawskie ptaki, przejdziecie seans terapeutyczny z pomocą sztuki, wyjdziecie z nowym warszawskim tatuażem i przejdziecie po księżycowym dywanie', 'nr');
INSERT INTO event (id, capacity, category_id, title, description, slug) VALUES (5, 150, 2, 'KURTYNA W GÓRĘ', 'Risk made in Warsaw to warszawska marka odzieżowa nierozerwalnie związana z lokalną kulturą. Tworzy nie tylko super wygodne ubrania, ale też specjalizuje się w publicystyce odzieżowej', 'kg');

-- insert sample data for occurence
INSERT INTO event_occurrence (id, event_id, date, time, is_common_pool) VALUES (1, 1, '2024-10-20', '15:00:00', true);
INSERT INTO event_occurrence (id, event_id, date, time, is_common_pool) VALUES (2, 1, '2024-10-20', '20:00:00', true);
INSERT INTO event_occurrence (id, event_id, date, time, is_common_pool) VALUES (3, 2, '2024-11-08', '16:00:00', true);
INSERT INTO event_occurrence (id, event_id, date, time, is_common_pool) VALUES (4, 2, '2024-11-09', '19:00:00', true);
INSERT INTO event_occurrence (id, event_id, date, time, is_common_pool) VALUES (5, 3, '2024-12-20', '17:00:00', false);
INSERT INTO event_occurrence (id, event_id, date, time, is_common_pool) VALUES (6, 4, '2024-11-09', '19:00:00', false);
INSERT INTO event_occurrence (id, event_id, date, time, is_common_pool) VALUES (7, 4, '2024-10-11', '14:00:00', false);
INSERT INTO event_occurrence (id, event_id, date, time, is_common_pool) VALUES (8, 5, '2024-11-18', '16:00:00', true);
INSERT INTO event_occurrence (id, event_id, date, time, is_common_pool) VALUES (9, 5, '2024-12-17', '20:00:00', true);

-- insert sample data for ticket - Dziady '2024-10-20', '15:00:00'
INSERT INTO ticket (id, event_occurrence_id, event_id, type, price, amount) VALUES (1, 1, 1, 'FULL_PRICE', 50, 60);
INSERT INTO ticket (id, event_occurrence_id, event_id, type, price, amount) VALUES (2, 1, 1, 'HALF_PRICE', 25, 40);
-- insert sample data for ticket - Dziady '2024-10-20', '20:00:00'
INSERT INTO ticket (id, event_occurrence_id, event_id, type, price, amount) VALUES (3, 2, 1, 'FULL_PRICE', 50, 60);
INSERT INTO ticket (id, event_occurrence_id, event_id, type, price, amount) VALUES (4, 2, 1, 'HALF_PRICE', 25, 40);

-- insert sample data for ticket - Wujaszek Wania '2024-11-08', '16:00:00'
INSERT INTO ticket (id, event_occurrence_id, event_id, type, price, amount) VALUES (5, 3, 2, 'FULL_PRICE', 40, 40);
INSERT INTO ticket (id, event_occurrence_id, event_id, type, price, amount) VALUES (6, 3, 2, 'HALF_PRICE', 20, 40);
-- insert sample data for ticket - Wujaszek Wania '2024-11-09', '19:00:00'
INSERT INTO ticket (id, event_occurrence_id, event_id, type, price, amount) VALUES (7, 4, 2, 'FULL_PRICE', 40, 40);
INSERT INTO ticket (id, event_occurrence_id, event_id, type, price, amount) VALUES (8, 4, 2, 'HALF_PRICE', 20, 40);

-- insert sample data for ticket - Anioły w Ameryce '2024-12-20', '17:00:00'
INSERT INTO ticket (id, event_occurrence_id, event_id, type, price, amount) VALUES (9, 5, 3, 'FULL_PRICE', 60, 20);
INSERT INTO ticket (id, event_occurrence_id, event_id, type, price, amount) VALUES (10, 5, 3, 'HALF_PRICE', 30, 40);

-- insert sample data for ticket - NO RISK NO FUN '2024-11-09', '19:00:00'
INSERT INTO ticket (id, event_occurrence_id, event_id, type, price, amount) VALUES (11, 6, 4, 'FULL_PRICE', 90, 50);
INSERT INTO ticket (id, event_occurrence_id, event_id, type, price, amount) VALUES (12, 6, 4, 'HALF_PRICE', 45, 50);
-- insert sample data for ticket - NO RISK NO FUN '2024-10-11', '14:00:00'
INSERT INTO ticket (id, event_occurrence_id, event_id, type, price, amount) VALUES (13, 7, 4, 'FULL_PRICE', 90, 50);
INSERT INTO ticket (id, event_occurrence_id, event_id, type, price, amount) VALUES (14, 7, 4, 'HALF_PRICE', 45, 50);

-- insert sample data for ticket - KURTYNA W GÓRĘ '2024-11-18', '16:00:00'
INSERT INTO ticket (id, event_occurrence_id, event_id, type, price, amount) VALUES (15, 8, 5, 'FULL_PRICE', 60, 75);
INSERT INTO ticket (id, event_occurrence_id, event_id, type, price, amount) VALUES (16, 8, 5, 'HALF_PRICE', 30, 75);
-- insert sample data for ticket - KURTYNA W GÓRĘ  '2024-12-17', '20:00:00'
INSERT INTO ticket (id, event_occurrence_id, event_id, type, price, amount) VALUES (17, 9, 5, 'FULL_PRICE', 60, 75);
INSERT INTO ticket (id, event_occurrence_id, event_id, type, price, amount) VALUES (18, 9, 5, 'HALF_PRICE', 30, 75);
