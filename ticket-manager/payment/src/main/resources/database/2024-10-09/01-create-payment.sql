--liquibase formatted sql
--changeset kpawelec:1
create table payment(
    id SERIAL PRIMARY KEY,
    name varchar(64) not null,
    type varchar(32) not null,
    default_payment boolean default false,
    note text
);
insert into payment(id, name, type, default_payment, note)
values (1, 'Przelew bankowy', 'BANK_TRANSFER', false, 'Prosimy o dokonanie przelewu na konto:\n30 1030 1739 5825 1518 9904 4499\n w tytule proszę podać nr zamówienia');


insert into payment(id, name, type, default_payment, note)
values (2, 'Przelew online', 'P24_ONLINE', true, '');
