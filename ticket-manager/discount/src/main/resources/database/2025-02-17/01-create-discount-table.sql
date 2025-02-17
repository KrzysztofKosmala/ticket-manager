--liquibase formatted sql
--changeset kkosmala:1
CREATE TABLE discount (
                          id BIGSERIAL PRIMARY KEY,
                          code VARCHAR(50) UNIQUE NOT NULL,
                          discount_type VARCHAR(20) NOT NULL CHECK (discount_type IN ('PERCENTAGE', 'FIXED_AMOUNT')),
                          value DECIMAL(10,2) NOT NULL CHECK (value > 0),
                          min_order_value DECIMAL(10,2) DEFAULT 0,
                          max_discount DECIMAL(10,2),
                          valid_from TIMESTAMP NOT NULL,
                          valid_to TIMESTAMP NOT NULL,
                          usage_limit INT DEFAULT 1 CHECK (usage_limit > 0),
                          used_count INT DEFAULT 0 CHECK (used_count >= 0),
                          is_active BOOLEAN DEFAULT TRUE,
                          created_at TIMESTAMP DEFAULT NOW(),
                          updated_at TIMESTAMP DEFAULT NOW(),
                          user_id BIGINT
);