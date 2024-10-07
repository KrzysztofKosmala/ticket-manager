--liquibase formatted sql
--changeset kkosmala:3

insert into payment(id, name, type, default_payment, note)
values (1, 'Przelew bankowy', 'BANK_TRANSFER', true, 'Prosimy o dokonanie przelewu na konto:\n30 1030 1739 5825 1518 9904 4499\n w tytule proszę podać nr zamówienia');