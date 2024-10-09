

CREATE TABLE cart_item (
                           id BIGSERIAL PRIMARY KEY,
                           ticket_id INTEGER NOT NULL,
                           cart_id INTEGER NOT NULL,
                           quantity INT,
                           ticket_price NUMERIC(19, 2) NOT NULL,
                           CONSTRAINT fk_cart_item_cart_id FOREIGN KEY (cart_id) REFERENCES cart(id)
);
