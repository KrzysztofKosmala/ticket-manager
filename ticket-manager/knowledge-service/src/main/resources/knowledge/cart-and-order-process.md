# Koszyk i składanie zamówienia

## Koszyk

Koszyk przechowuje wybrane bilety, ich ilość i cenę. Użytkownik może dodawać bilety, zmieniać ich ilość oraz usuwać pozycje z koszyka.

Jeżeli użytkownik nie ma jeszcze koszyka, system może utworzyć nowy koszyk podczas dodawania pierwszego biletu.

## Złożenie zamówienia

Podczas składania zamówienia serwis zamówień pobiera zawartość koszyka i informacje o wybranych biletach. Następnie tworzy zamówienie oraz jego pozycje i uruchamia proces rezerwacji oraz płatności.

Po utworzeniu zamówienia wykorzystany koszyk jest czyszczony i usuwany. Dalszy postęp należy sprawdzać na podstawie statusu zamówienia.

## Dane użytkownika

Zawartość koszyka i zamówienia konkretnego użytkownika jest informacją prywatną i zmienną. Należy ją pobierać przez autoryzowane narzędzie, a nie z bazy wiedzy.
