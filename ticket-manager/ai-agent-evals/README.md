# AI Agent Evals

Pierwszy, maly modul do sprawdzania zachowania uruchomionego agenta przez HTTP.
Zawiera dwa scenariusze JUnit: pamiec rozmowy oraz wyszukanie zamowienia przez tool.

## Scenariusz

1. Test losuje oznaczenie, np. `EVAL-a1b2c3d4`.
2. Wysyla POST `/api/v1/ai-agents/ask` z prosba o zapamietanie oznaczenia.
   Nie podaje conversationId, wiec agent tworzy nowa rozmowe.
3. Wysyla kolejne pytanie z otrzymanym conversationId, ale bez oznaczenia.
4. Sprawdza statusy, zachowanie conversationId oraz obecnosc oznaczenia w odpowiedzi.

Losowanie oznaczenia zapobiega odpowiedzi z wiedzy wyuczonej modelu. Test nie
wymaga dokladnego zdania, ale wymaga odtworzenia oznaczenia bez zmian.
Kazde uruchomienie tworzy osobna rozmowe; jej wiadomosci i runy pozostaja
w magazynie skonfigurowanym w agencie, co pozwala je obejrzec po tescie.

## Klasy

- `ConversationMemoryIT`: scenariusz, oczekiwania i diagnostyka wyniku.
- `OrderToolCallingIT`: POST do agenta i sprawdzenie wykonania toola w bazie przez JDBC.
- `support.AgentClient`: HTTP z JDK, JWT, timeout i zamiana JSON przez Jacksona.
  Male rekordy w kliencie opisuja kontrakt HTTP; modul nie importuje klas ai-agent.

Nie ma tu kontekstu Springa ani uruchamiania mikroserwisow. Eval trafia do
agenta dzialajacego pod podanym adresem. Jego konfiguracja decyduje, czy dalej
pracuje prawdziwy model i MCP, czy kontrolowane implementacje testowe.

## Uruchomienie

Wymagania: Java 19, Maven 3.9+, uruchomiony ai-agent z jego zaleznosciami oraz
wazny JWT uzytkownika. W obecnym profilu local sa to m.in. PostgreSQL ai,
Ollama, Keycloak i skonfigurowane serwery MCP. Test nie uruchamia ich za Ciebie.

W lokalnym, ignorowanym przez Git pliku
`src/test/resources/eval-local.properties` ustaw:

```properties
eval.base-url=http://localhost:8099
eval.request-timeout-seconds=300
eval.token=<JWT bez prefiksu Bearer>
```

W repozytorium znajduje sie bezpieczny wzor `eval-local.properties.example`.
Wlasciwy plik jest ignorowany, aby JWT nie trafil do commita.
Oba POST-y uzywaja tego samego tokenu, a wiec tego samego wlasciciela rozmowy.

Po uzupelnieniu pliku mozesz uruchomic `ConversationMemoryIT` bezposrednio
z IntelliJ albo wykonac z katalogu `ticket-manager/ticket-manager`:

```powershell
mvn -pl ai-agent-evals -Peval-local verify
```

`eval.base-url` to adres serwera (schemat, host, port). Klient dopina sciezke
`/api/v1/ai-agents/ask`. Timeout domyslnie wynosi 300 sekund na POST;
dwie tury moga lacznie trwac dluzej. Timeout klienta nie anuluje runa w agencie.

Brak pliku lub `eval.token`, HTTP 401/403, niedostepny serwis albo zla odpowiedz oznaczaja blad testu.
Nie pomijamy testu automatycznie i nie ponawiamy pytania po bledzie.

## Maven i raport

```shell
mvn -pl ai-agent-evals test
```

Ta komenda tylko kompiluje szkielet; obecnie nie uruchamia zadnych testow.
Surefire nie wybiera klas `*IT`. Profil Maven `eval-local` wlacza Failsafe,
ktory wykonuje scenariusz w fazie integration-test i sprawdza wynik w verify.
Jest to profil Maven, a nie profil Springa zmieniajacy beany agenta.

Wynik trafia do konsoli i `target/failsafe-reports/` (TXT oraz XML JUnit).
W konsoli i raporcie XML sa tez odpowiedzi, conversationId, oczekiwane oznaczenie
i czas dwoch tur. Sa to dane syntetycznej rozmowy, nie metryki tokenow ani kosztu.

## Co potwierdza pierwszy eval

Sprawdza podstawowa ciaglosc rozmowy obserwowana przez klienta HTTP. Nie dowodzi
jeszcze, jakie toole wywolano, jak zbudowano prompt ani czy RAG znalazl dobre
zrodlo. Z prawdziwym modelem wynik moze sie roznic miedzy uruchomieniami.

To punkt startowy do dluzszych rozmow i porownywania strategii kontekstu.
Obecny przypadek ma tylko dwie tury, wiec nie mierzy jeszcze kompresji historii.
Przy przyszlych porownaniach trzeba zapisywac model, ustawienia, tokeny wszystkich
wywolan (rowniez summary) i wykonywac kilka powtorzen na osobnych rozmowach.

## Wyszukanie zamowienia przez tool

`OrderToolCallingIT` wysyla jedno pytanie: "Wyszukaj moje zamowienie o numerze 1001."
Tworzy nowa rozmowe i czyta tylko jej wywolania, laczac `ai_agent_run` z
`ai_tool_invocation` po `run_id` oraz `conversation_id`. Nie wyszukuje ostatniego
rekordu calej tabeli. Odczyt jest synchroniczny po odpowiedzi HTTP: obecny recorder
zapisuje wywolanie przed zwroceniem wyniku toola.

Warunki: agent z `ai-agent.persistence.mode=jpa` oraz gateway z
`ai-tools-gateway.tools.orders.handler=fake`. Fake ma zamowienie 1001 ze statusem
PAID i kwota 199.99. To kontrolowane dane oczekiwane, ktorych nie podajemy modelowi.
Ustaw w lokalnym pliku te sama baze, do ktorej zapisuje agent:

```properties
eval.database.url=jdbc:postgresql://localhost:51/ai?connectTimeout=5&socketTimeout=15
eval.database.username=admin
eval.database.password=password
```

Uruchom sama klase w IntelliJ lub przez Maven:

```shell
mvn -pl ai-agent-evals -Peval-local -Dit.test=OrderToolCallingIT verify
```

Kryterium ustalone przed proba: zakonczone zadanie HTTP oraz co najmniej jedno
zapisane wywolanie `tm_my_orders_search` z liczbowym `request.filters.orderId=1001`,
statusem SUCCESS i wynikiem zawierajacym tylko zamowienie 1001, PAID, 199.99.
JSON porownujemy po polach. Inne wywolania lub poprawiona wczesniejsza pomylka
nie przekreslaja udanego wyszukania. Nie mierzymy tu minimalnej liczby wywolan.

Wynik MCP ma postac tablicy blokow content; pole text zawiera drugi JSON z danymi
zamowienia. Test jawnie odczytuje ten format uzywany przez Spring AI MCP 1.1.7.
Raport zawiera odpowiedz HTTP, runId, argumenty i wyniki tooli. Uzywaj danych fake.
JDBC wykonuje tylko SELECT; rozmowa i run pozostaja do obejrzenia w bazie.

### Uzasadnienie i ograniczenia

Yao i in. (2024), [tau-bench](https://arxiv.org/abs/2406.12045), oceniaja wykonanie
zadania przez porownanie koncowego stanu bazy z oczekiwanym stanem, a niezawodnosc
badaja przez wielokrotne proby. Tutaj stosujemy mniejsza adaptacje tej zasady:
tool jest read-only, wiec dowodem wykonania jest zapis audytowy jego argumentow
i wyniku. Nie jest to odtworzenie benchmarku ani pomiar jego metryki pass^k.

Test sprawdza polaczenie model -> MCP -> fake handler -> audyt oraz precyzyjny
filtr wyszukania. Szerokie wyszukanie wszystkich zamowien moze wystarczyc do
odpowiedzi uzytkownikowi, ale nie zaliczy tego testu precyzyjnego tool callingu.
Nie oceniamy semantycznej poprawnosci odpowiedzi koncowej ani autoryzacji danych.
Poprawnosc recordera sprawdzaja osobne testy w ai-agent; audyt nie jest niezaleznym
dowodem przyczynowym, ze model wykorzystal wynik do napisania odpowiedzi.

Jedno uruchomienie to jedna obserwacja. Przy porownywaniu wersji zachowaj model,
temperature, prompt, dane fake i zestaw tooli; zmieniaj jeden element. Powtarzaj
na nowych rozmowach, zachowuj wszystkie wyniki i raportuj liczbe sukcesow/liczbe
prob. Nie ponawiaj automatycznie az do sukcesu i nie wyciagaj wnioskow o ogolnej
niezawodnosci agenta z jednego zaliczonego testu.
