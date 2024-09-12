--liquibase formatted sql
--changeset kkosmala:1
CREATE TABLE "order" (
                         id BIGSERIAL PRIMARY KEY,
                         place_date TIMESTAMP NOT NULL,
                         order_status VARCHAR(32) NOT NULL,
                         gross_value DECIMAL(6,2) NOT NULL
);

CREATE TABLE order_row (
                           id BIGSERIAL PRIMARY KEY,
                           order_id BIGINT NOT NULL,
                           product_id BIGINT NOT NULL,
                           quantity INT NOT NULL,
                           price DECIMAL(6,2) NOT NULL,
                           CONSTRAINT fk_order_row_order_id FOREIGN KEY (order_id) REFERENCES "order"(id)
);