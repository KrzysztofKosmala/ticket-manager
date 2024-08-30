

CREATE TABLE cart_item (
                           id BIGSERIAL PRIMARY KEY,
                           ticket_id INTEGER NOT NULL,
                            cart_id INTEGER NOT NULL,
                           quantity INT,
                           CONSTRAINT fk_cart_item_cart_id FOREIGN KEY (cart_id) REFERENCES cart(id)
);
