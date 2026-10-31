# Statusy zamówienia i płatności

## Status zamówienia

Status zamówienia opisuje etap realizacji całego zamówienia:

- `CREATED` - zamówienie zostało utworzone.
- `RESERVED` - bilety zostały zarezerwowane.
- `PAID` - płatność za zamówienie została potwierdzona.
- `CANCELLING` - trwa wycofywanie rezerwacji.
- `CANCELED` - zamówienie zostało anulowane.
- `COMPLETED` - realizacja zamówienia została zakończona.
- `WAITING_FOR_DELIVERY` - zamówienie oczekuje na dostarczenie.
- `REFUND` - zamówienie zostało oznaczone jako zwrócone.

## Status płatności

Status płatności opisuje wyłącznie operację płatniczą:

- `PENDING` - płatność oczekuje na rozstrzygnięcie.
- `PAID` - płatność została zaakceptowana.
- `REJECTED` - płatność została odrzucona.

Status płatności i status zamówienia nie są tym samym. Aktualny status konkretnego zamówienia lub płatności należy pobierać przez autoryzowane narzędzie.
