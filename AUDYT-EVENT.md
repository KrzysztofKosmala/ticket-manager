**Raport audytu mikroserwisu event — 12 września 2026**

**Ocena ogólna:** zakres mikroserwisu jest sensowny, ale obecna implementacja wymaga napraw przed obsługą rzeczywistej sprzedaży. Największe problemy dotyczą trwałości danych, wspólnej puli miejsc, uprawnień oraz niezawodności rezerwacji i wydawania biletów. Podział na kontrolery, serwisy i repozytoria jest czytelny. Złożoność zwiększają przede wszystkim powielone encje i procesy rozproszone, których stan nie jest zapisany w bazie.

Audyt obejmuje aktualny kod EVENT: administrację, katalog, terminy, bilety, QR, obrazy, wyszukiwanie dla AI, migracje, konfigurację i testy. Moduły AMQP, wspólne DTO, gateway i deklaracje kolejek w ORDER sprawdzono wyłącznie na styku z EVENT. Pozostałe mikroserwisy nie są przedmiotem tego raportu. Nie zmieniono kodu aplikacji ani istniejących zmian użytkownika.

To **audyt statyczny**, uzupełniony dokumentacją użytych mechanizmów. Scenariusze opisują skutki wynikające z kodu; nie są wynikami wykonanych testów. W dotychczasowej sesji Maven nie rozwiązał zależności w dostępnym środowisku, a Docker nie udostępniał działającego demona. Próba rozszerzenia uprawnień do weryfikacji Mavena została odrzucona przez automatyczną kontrolę z komunikatem o limicie użycia. Nie obchodzono tego ograniczenia. Nie uruchamiano aplikacji, testów integracyjnych ani operacji na działających bazach i brokerze. Zewnętrzne konfiguracje wdrożenia lub polityki RabbitMQ mogą zmieniać część zachowań operacyjnych; nie były dostępne do oceny.

Priorytety: **P0** — usunąć przed uruchomieniem na wartościowych danych; **P1** — naprawić przed rzeczywistą sprzedażą; **P2** — poprawić w następnych iteracjach. Uwagi projektowe poniżej są rekomendacjami, a nie automatycznie błędami.

**Najpierw naprawiłbym te problemy**

| ID | Priorytet | Problem | Praktyczny skutek |
| --- | --- | --- | --- |
| E01 | P0 | Kasowanie schematu przy starcie | Utrata wydarzeń, stanów biletów i informacji o ich użyciu |
| E02 | P1 | Administracja dostępna dla każdego zalogowanego | Klient może zmieniać lub usuwać wydarzenia |
| E03 | P1 | Wspólna pula jest liczona osobno dla taryf | Sprzedaż ponad pojemność sali |
| E04 | P1 | Pomylenie ID rodzaju i egzemplarza biletu | Puste wyniki albo przekazanie QR innych zamówień |
| E05 | P1 | Walidacja QR ufa danym z obrazka | Wielokrotne użycie biletu i brak dowodu autentyczności |
| E06 | P1 | Brak idempotencji i zapisanych rezerwacji | Podwójne odjęcie/oddanie miejsc lub wydanie biletów |
| E07 | P1 | Zapis DB i publikacja komunikatu nie tworzą niezawodnej całości | Sukces bez rezerwacji albo bilety bez potwierdzenia |
| E08 | P1 | Błędy infrastruktury są odrzucane bez odzyskania procesu | Zawieszone zamówienia i zablokowane miejsca |
| E09 | P1 | Tworzenie wydarzenia okazjonalnego nie zapisuje wydarzenia | Błąd zapisu terminów lub pozorny sukces |
| E10 | P1 | Usunięcie dzisiejszego wydarzenia pomija żądanie zwrotu | Usunięcie sprzedanych biletów bez rozpoczęcia rekompensaty |

**E01 — konfiguracja uruchomienia usuwa dane**

W [application.yml:29][config] ustawiono **spring.liquibase.drop-first: true**. Jednocześnie podstawowa konfiguracja aktywuje profil prod, uruchamia Hibernate z ddl-auto=update i ładuje initData.sql przy każdym starcie. Przy uruchomieniu z tymi ustawieniami Liquibase usuwa schemat przed migracjami. To rzeczywiste znaczenie tej opcji, potwierdzone w [dokumentacji Spring Boot](https://docs.spring.io/spring-boot/docs/3.0.13/reference/html/application-properties.html).

**Poprawa:** bezpieczne ustawienia bazowe, drop-first=false, dane demonstracyjne wyłącznie w jawnym profilu lokalnym oraz jeden właściciel schematu — Liquibase. Docelowo Hibernate powinien tylko sprawdzać zgodność przez validate.

Przejście na validate wymaga najpierw uporządkowania migracji. Przykładowo [AdminImage:24][image-model] wskazuje kolumnę desctiption, podczas gdy [migracja:7][image-migration] tworzy description. Encje używają też nazw sekwencji innych niż generowane przez SERIAL w migracjach. Nie wystarczy więc mechanicznie zmienić jednego ustawienia i uznać temat za zakończony.

**Warunek odbioru:** start na pustej bazie z samymi migracjami działa; ponowny start zachowuje istniejące wydarzenia i użyte bilety.

**E02 — zalogowanie jest traktowane jak prawo do administracji**

[SecurityConfig:31][security] kończy reguły na anyRequest().authenticated(). W module nie ma dodatkowych reguł ról na metodach. Obejmuje to tworzenie, edycję i usuwanie wydarzeń oraz wewnętrzny endpoint zużywający bilet przez skan QR. Samo umieszczenie ścieżki pod admin lub internal nie ogranicza uprawnień.

**Poprawa:** jawna rola administratora dla administracji, osobne uprawnienie kontrolera wejścia dla skanowania i uprawnienia usługowe dla integracji. Role trzeba faktycznie zmapować z JWT Keycloak. Ochrona musi działać również przy bezpośrednim wywołaniu EVENT. Nieużywane wyjątki dla logowania/rejestracji klientów i PasswordEncoder w konfiguracji EVENT warto usunąć.

**Warunek odbioru:** anonimowy użytkownik otrzymuje 401, zwykły klient 403 dla administracji, a uprawniony administrator może wykonać operację. Token usługi czytającej katalog nie pozwala zużywać biletów.

**E03 — wspólna pula miejsc nie jest wspólna**

[AdminEventMapper:71][pool] przypisuje pełną pojemność wydarzenia każdemu typowi biletu, kiedy isCommonTicketPool=true. [InternalTicketService:31][reservation] odejmuje miejsce tylko z wybranego typu i nie sprawdza wspólnego licznika terminu.

Przykład: pojemność 100, taryfa normalna i ulgowa. Powstaje 100 normalnych oraz 100 ulgowych. Rezerwacje mogą wykorzystać obie pule, czyli łącznie 200 miejsc. Wersjonowanie pojedynczego rekordu ticket nie zabezpiecza limitu obejmującego kilka rekordów. Wyszukiwanie dla AI dodatkowo sumuje te liczniki i pokazuje 200 dostępnych biletów.

**Poprawa:** dla wspólnej puli jeden atomowo zmieniany licznik miejsc konkretnego terminu; typ biletu określa cenę. Osobne limity taryf stosować tylko wtedy, gdy rzeczywiście oznaczają osobne pule. Reguła liczby miejsc musi być wspólna dla rezerwacji i odczytu dostępności.

**Warunek odbioru:** pojemność 1, rezerwacja normalnego, następnie ulgowego — druga rezerwacja jest odrzucona. Tak samo przy dwóch równoczesnych żądaniach.

**E04 — wydawanie biletów myli dwa rodzaje identyfikatorów**

[TicketListener:51][listener] przekazuje ID nowo utworzonych egzemplarzy do zapytania, które w [InternalConcreteTicketRepository:29][issued-query] filtruje gt.id, czyli ID rodzaju biletu.

Jeżeli nowy egzemplarz ma ID 100, zapytanie pobiera egzemplarze rodzaju 100. Może nie zwrócić nic albo zwrócić bilety innych zamówień, wraz z QR. Listener mimo tego publikuje komunikat przygotowania biletów.

**Poprawa:** filtrować po ct.id i odpowiednio nazwać metodę oraz parametr. Jeszcze prościej: zwrócić potrzebne DTO z operacji wydającej egzemplarze, jeżeli eliminuje to zbędne ponowne wyszukiwanie.

**Warunek odbioru:** dwa zamówienia, różne ID typów i egzemplarzy; odpowiedź zawiera dokładnie egzemplarze właściwego zamówienia. Liczba i tożsamość biletów muszą się zgadzać.

**E05 — ten sam QR można wykorzystać wielokrotnie**

[InternalConcreteTicketService:88][qr-check] sprawdza isUsed odczytane z QR. Obrazek zawsze zawiera pierwotne false. Metoda zapisuje true w bazie, lecz przy kolejnym skanie nie sprawdza tej wartości. Błąd występuje również przy zwykłym, sekwencyjnym skanowaniu.

Ponadto [QrGenerator:30][qr-generator] koduje zwykły JSON z identyfikatorami. Nie ma niezgadywalnego sekretu ani podpisu. Zgodność kilku ID z rekordem nie jest dowodem, że kod został wystawiony przez system. Walidacja nie przyjmuje też oczekiwanego terminu wejścia — porównuje dane biletu z nim samym.

**Poprawa:** QR zawiera losowy, niezgadywalny token egzemplarza. Status i dane wydarzenia pochodzą z bazy. Zużycie biletu wykonuje atomowa operacja warunkowa, która zmienia rekord tylko wtedy, gdy nie został użyty lub anulowany i należy do oczekiwanego terminu. Sukces oznacza dokładnie jeden zmieniony rekord.

**Warunek odbioru:** pierwszy skan przechodzi, drugi nie; spośród dwóch równoległych skanów tylko jeden przechodzi; kod dla innego terminu oraz wymyślony token są odrzucane.

**E06 — powtórzona wiadomość ponownie wykonuje operację biznesową**

[Rezerwacja i zwalnianie][reservation] bezwarunkowo zmieniają liczniki. [Wydawanie egzemplarzy:31][issue] zawsze tworzy kolejne rekordy. [Migracja order_row_id:3][order-row-migration] nie narzuca unikalności. W bazie EVENT nie ma zapisanego stanu rezerwacji przypisanego do zamówienia.

W rezultacie ponowione orderCreated odejmuje miejsca drugi raz, unbookOrder dodaje je drugi raz, a prepareConcreteTickets wystawia następne egzemplarze. Powtórne dostarczenie jest normalnym scenariuszem po awarii połączenia lub procesu, który konsumenci muszą obsługiwać. [RabbitMQ opisuje to wprost](https://www.rabbitmq.com/docs/reliability).

**Poprawa:** zapisana rezerwacja z orderId, pozycjami i statusem, dozwolone przejścia stanu oraz unikalność biznesowa wydanego egzemplarza. W obecnym kontrakcie, w którym jeden orderRow oznacza jedną sztukę, odpowiednim ograniczeniem jest unikalny order_row_id. Jeżeli kontrakt zmieni się na ilości, klucz musi uwzględniać numer sztuki. Obsłużone ID zdarzeń można zapisywać w tej samej transakcji.

**Warunek odbioru:** każde z trzech poleceń wykonane dwukrotnie daje taki sam końcowy stan jak wykonane raz, także po restarcie aplikacji.

**E07 — komunikat sukcesu może wyprzedzić zatwierdzenie rezerwacji**

[InternalTicketService:46][publish-before-commit] publikuje reservationCompleted przed zakończeniem transakcji. Przy błędzie commitu, np. konflikcie wersji, inne usługi mogą już przetwarzać sukces. Handler błędu może potem opublikować odmowę.

Przy wydawaniu egzemplarzy kolejność jest odwrotna: [TicketListener:50][listener] najpierw kończy transakcję tworzenia biletów, później wysyła wiadomość. Awaria pomiędzy tymi krokami pozostawia bilety bez potwierdzenia dalszego procesu. Podobny problem dotyczy zwrotów przy usuwaniu.

**Poprawa:** niewielka tabela outbox. Zmianę domenową i rekord wiadomości zapisać w jednej lokalnej transakcji. Osobny publisher odczytuje niewysłane wiadomości i oznacza je po potwierdzeniu przez broker. Konsument nadal musi być idempotentny, bo wysłanie można powtórzyć. Samo przeniesienie publikacji po commit nie zamyka okna awarii procesu.

**Warunek odbioru:** przerwanie procesu przed commitem nie publikuje sukcesu; przerwanie po commicie nie gubi wiadomości, która zostaje wysłana po wznowieniu.

**E08 — obsługa błędów nie rozróżnia braku miejsc od awarii technicznej**

[ReservationProcessExceptionHandler:26][reservation-error] zamienia każdy wyjątek na odmowę rezerwacji i odrzuca wejściową wiadomość bez ponowienia. [UnbookProcessExceptionHandler:26][unbook-error] oraz [TicketValidationExceptionHandler:25][issue-error] odrzucają komunikaty bez wznowienia operacji. Ostatni handler obsługuje wydawanie biletów, mimo nazwy sugerującej walidację QR.

Krótka awaria bazy może więc pozostawić opłacone zamówienie bez wydania biletów albo anulowane zamówienie z zajętymi miejscami. W sprawdzonej konfiguracji repozytorium nie ma DLQ ani jawnej strategii ponowień; nie oceniano polityk ustawionych poza kodem na brokerze.

**Poprawa:** brak miejsc traktować jako wynik biznesowy. Błędy przejściowe ponawiać z ograniczeniem prób i opóźnieniem. Po ich wyczerpaniu zachować wiadomość, powód i ID procesu w kolejce błędów, z możliwością kontrolowanego wznowienia. Ponowienia wdrażać razem z E06 i E07.

**Warunek odbioru:** awaria infrastruktury nie jest raportowana jako brak miejsc, a nieudana operacja pozostaje możliwa do odnalezienia i odzyskania.

**E09 — wydarzenie okazjonalne nie jest zapisywane**

W [AdminEventService:47][occasional] mapper tworzy obiekt wydarzenia, lecz metoda nie wywołuje save dla wydarzenia. Terminy dostają eventId=null. [Migracja event_occurrence:7][occurrence-migration] wymaga wartości niepustej, więc poprawne żądanie z terminami kończy się błędem zapisu. Puste listy mogą z kolei dać pozorny sukces bez utworzenia wydarzenia.

**Poprawa:** jedna transakcja obejmująca zapis wydarzenia, terminów i taryf; wspólna część procesu tworzenia regular/occasional i osobne, czytelne wyznaczanie terminów. Nie potrzeba do tego hierarchii strategii ani nowego frameworka.

**Warunek odbioru:** tworzenie regularne i okazjonalne zapisuje kompletny zestaw rekordów; błąd dowolnego elementu cofa całość.

**E10 — anulowanie terminu w dniu wydarzenia pomija żądanie zwrotu**

[AdminConcreteTicketService:34][cancel] wysyła żądanie zwrotu tylko przy date.isAfter(now). Gdy wydarzenie jest dzisiaj o 20:00, a administrator usuwa je o 10:00, warunek jest fałszywy. Egzemplarze są jednak bezwarunkowo usuwane w linii 60.

Osobno, [usunięcie jednego terminu:41][occurrence-delete] nie ma transakcji obejmującej cały proces. Usunięcie całego wydarzenia taką transakcję posiada. Zachowanie jest więc niespójne zależnie od endpointu.

**Poprawa:** zdefiniować anulowanie jako operację domenową, uwzględniającą datę, godzinę, strefę czasową i stan sprzedaży. Sprzedane bilety oznaczać jako anulowane, zachowując historię i identyfikatory potrzebne do rozliczenia. Polecenie rekompensaty zapisywać przez outbox. Twarde usuwanie zostawić dla niesprzedanych szkiców. EVENT powinien potwierdzić anulowanie swoich biletów; rzeczywiste wykonanie zwrotu pozostaje odpowiedzialnością systemu płatności.

**Pozostałe błędy i ryzyka do dopracowania**

**E11 — P2: niepełna walidacja i niejasna zmiana pojemności.** [DTO tworzenia:22][creation-dto] wymaga jedynie niepustej capacity, bez dodatniej wartości; lista tickets nie ma walidacji kaskadowej, a [DTO taryfy:7][ticket-dto] nie ogranicza ceny ani ilości. Dzień tygodnia jest tekstem, więc literówka może dać pusty harmonogram. W aktualizacji capacity jest przyjmowane, lecz [updateEvent:113][update] go nie zapisuje. Uzupełnić ograniczenia DTO i bazy, użyć DayOfWeek, odrzucać puste harmonogramy i zdefiniować, jak zmienia się pojemność po sprzedaży. Samo dodanie setCapacity nie rozwiąże zależności z pulami i istniejącymi rezerwacjami.

**E12 — P2: błędna paginacja.** [EventService:51][pagination] i [AdminEventService:163][admin-pagination] używają rozmiaru bieżącej strony jako totalElements. Dla 20 rekordów i rozmiaru 5 pierwsza strona informuje o 5 rekordach. Zwracać Page z rzeczywistym licznikiem i mapować przez page.map. [Zapytanie terminów:26][event-query] łączy dodatkowo kolekcję JOIN FETCH z Pageable, co w Hibernate prowadzi do ograniczania wyników w pamięci. Najpierw stronicować ID wydarzeń, potem pobierać ich terminy, albo użyć projekcji. Ograniczyć size i dodać stabilne sortowanie. Mechanizm paginacji kolekcji opisuje [dokumentacja Hibernate 6.1](https://docs.hibernate.org/orm/6.1/userguide/html_single/#hql-limit-offset); rzeczywistego kosztu SQL w tym audycie nie zmierzono.

**E13 — P2: obsługa obrazów ma kilka niespójnych kontraktów.** [Konfiguracja][image-config] generuje URL /api/v1/data/productImage/, a [kontroler][image-controller] obsługuje /api/v1/admin/images/data/productImage/. Zapis i odczyt używają również różnych katalogów. [Upload:38][upload] nadaje deterministyczną nazwę, więc drugi plakat o tej samej nazwie może nadpisać pierwszy; strumień wyjściowy nie jest zamykany w try-with-resources. Po usunięciu obrazu baza ustawia image_id=NULL, lecz [mapper:70][image-map] dereferencjonuje obraz bez sprawdzenia null, a [lista administratora:19][admin-list] używa wewnętrznego złączenia i pomija takie wydarzenie. Wystarczy jeden konfigurowany storage, losowe klucze, sprawdzanie formatu, spójna ścieżka odczytu i jawna obsługa braku obrazu. Potrzebny test upload → pobranie → przypisanie → usunięcie.

**E14 — P2: wyszukiwanie dla AI nie dotrzymuje offsetu i sortowania.** [EventAiSearchService:31][ai-search] oblicza page=offset/limit. Dla limit=10 i offset=15 wynik zaczyna się od pozycji 10, a dla offset=5 od 0. Parametr sort z DTO jest ignorowany; [zapytanie:36][ai-query] zawsze sortuje po dacie, godzinie i tytule. Wdrożyć prawdziwy offset lub zmienić kontrakt na page/size, a sortowanie ograniczyć do jawnej listy pól. Dodać unikalny ID jako ostatnie kryterium kolejności. Ustalić też znaczenie minPrice: obecnie uwzględnia cenę wyprzedanej taryfy, nawet gdy filtr onlyAvailable pokazuje wydarzenie jako dostępne.

**E15 — P2: rozbieżne modele zapisu osłabiają wersjonowanie.** [AdminTicketRepository:34][price-update] aktualizuje cenę zapytaniem zbiorczym bez zwiększenia version, natomiast rezerwacja modyfikuje InternalTicket z ceną i wersją. Z mapowania i domyślnego zachowania Hibernate wynika scenariusz: rezerwacja odczytuje cenę 50, administrator zmienia ją na 60, flush rezerwacji zapisuje ponownie 50. Jest to **ryzyko wywnioskowane z konfiguracji, bez odtworzenia SQL w teście**. Ujednolicić model zapisu i protokół wersjonowania albo oddzielić atomową zmianę zapasu od ceny. Samo występowanie @Version w części encji nie wystarcza. [Hibernate dokumentuje zarówno pełne aktualizacje, jak i brak automatycznej zmiany wersji w bulk update](https://docs.hibernate.org/orm/6.1/userguide/html_single/#hql-update).

**E16 — P2: capacity-check nie sprawdza dostępności sprzedażowej.** [EventRepository:29][capacity] sprawdza tylko event.capacity > 0. Ta wartość nie maleje przy rezerwacjach, więc endpoint może nadal zwracać true po wyprzedaniu biletów. Jeśli ma sprawdzać fizyczną pojemność, nazwa i zastosowanie powinny to mówić. Jeśli ma odpowiadać, czy można kupić bilet, musi pytać o dostępność konkretnego terminu. Ostateczną decyzję zawsze musi podejmować atomowa rezerwacja, ponieważ odczyt dostępności może się zdezaktualizować.

**E17 — P2: logowanie nie ma zadeklarowanej skutecznej rotacji.** [logback-spring.xml:4][logging] używa FileAppender z rollingPolicy, choć rotację obsługuje RollingFileAppender. Ustawione komentarze o 30 dniach nie zapewniają retencji. Konfiguracja ma ponadto TRACE dla pl.ticket i DEBUG dla root, a saga loguje pełne komunikaty, w tym adresy klientów i dane biletów. Użyć działającej rotacji albo logowania do stdout, ustawić poziomy zależne od środowiska i logować głównie eventId, orderId, wynik oraz czas. Umieścić traceId w formacie. Rozróżnienie appenderów potwierdza [dokumentacja Logback](https://logback.qos.ch/manual/appenders-rolling.html).

**Czy podział mikroserwisu jest dobry?**

EVENT posiada katalog wydarzeń, konkretne terminy, taryfy, dostępność, sprzedane egzemplarze i kontrolę wejścia. Te elementy są silnie związane. **Zostawiłbym je teraz w jednym mikroserwisie.** Dzięki temu rezerwację, pulę i wydanie można chronić lokalną transakcją. Rozdzielenie ich na następne procesy zwiększyłoby liczbę awarii rozproszonych, zanim naprawiono obecne reguły.

Najbardziej uprościłbym wnętrze. Te same tabele mają po kilka encji: Event/AdminEvent/InternalEvent oraz Ticket/AdminTicket/InternalTicket. Customer Event odwołuje się nawet do AdminImage. Podział według odbiorcy API przeniknął do modelu zapisu, co doprowadziło już do różnych pól, fetchowania i wersjonowania.

Docelowo proponuję jeden model zapisu na tabelę, osobne DTO dla API oraz projekcje dla odczytów. Kontrolery administratora, klienta i integracji mogą nadal być osobne. Czytelny podział wewnętrzny to: katalog, terminy i taryfy, rezerwacje, wydane bilety, obrazy, integracje. Klasy i metody powinny nazywać operacje: reserve, release, issue, redeem, cancel. Publiczne setAmount nie powinno być jedynym miejscem wyrażenia reguł dostępności.

Warto usunąć puste klasy, nieużywane importy i komentarze opisujące oczywiste instrukcje. Przykładowo customer ImageService jest pusty, a nazwa TicketValidationExceptionHandler nie odpowiada jego roli w wydawaniu. Nie ma potrzeby dodawać interfejsu do każdego serwisu, rozbudowanego frameworka sagi ani osobnego systemu CQRS. Najpierw potrzebne są jednoznaczne reguły i jeden stan procesu.

**Czy komunikacja wygląda dobrze?**

Połączenie HTTP dla odczytów i RabbitMQ dla procesu zakupu jest uzasadnione. Problemem są gwarancje wykonania i odpowiedzialność za kontrakty.

| Wejście do EVENT | Odpowiedzialność EVENT | Oczekiwany wynik |
| --- | --- | --- |
| orderCreated | Rezerwacja miejsc | reservationCompleted albo biznesowe reservationRejected |
| unbookOrder | Zwolnienie istniejącej rezerwacji | orderUnbooked |
| prepareConcreteTickets | Jednorazowe wydanie egzemplarzy | preparedConcreteTickets |
| Anulowanie terminu przez administratora | Anulowanie własnych biletów i zapis potrzeby rekompensaty | Trwałe polecenie; obecna nazwa refoundPayment |
| HTTP katalogu / terminów / taryf | Odczyt danych domenowych | Stabilne DTO, poprawna paginacja |
| HTTP wyszukiwania dla AI | Filtrowanie katalogu | Ograniczona liczba wyników zgodnych z kontraktem |

ORDER powinien koordynować zakup, a EVENT posiadać stan swoich rezerwacji i biletów. W komunikatach warto rozróżnić polecenia od zdarzeń o wykonanej operacji. Wspólny, mutowalny OrderEvent używany do wielu etapów utrudnia ustalenie, które pola są obowiązkowe i co już się wydarzyło. Małe, typowane kontrakty z eventId, orderId, czasem i wersją schematu będą łatwiejsze do czytania. Zmiany istniejących nazw kolejek i DTO wymagają kompatybilnego wdrożenia po obu stronach.

[Wspólny publisher:18][publisher] uznaje samo convertAndSend za publikację zakończoną sukcesem. Brakuje jawnej obsługi publisher confirms i zwrotów wiadomości bez routingu. Włączyć te mechanizmy i powiązać je z outbox. Potwierdzenie brokera nie oznacza wykonania biznesowej operacji przez odbiorcę — są to różne poziomy potwierdzeń. [Dokumentacja RabbitMQ](https://www.rabbitmq.com/docs/confirms).

EVENT nie deklaruje też własnych kolejek wejściowych: jego [RabbitMqConfig][rabbit-config] przechowuje tylko properties, a kolejki tworzy [ORDER:77][order-queues]. Na świeżym brokerze start EVENT zależy więc od innej usługi. Kolejki powinien deklarować konsument albo osobny, jawny provisioning infrastruktury.

Na styku HTTP trzeba poprawić [gateway:24][gateway]: ścieżka tickets kieruje do usługi TICKET, choć bilety są w EVENT. Brakuje również tras do części API terminów, kategorii i obrazów. Weryfikować publiczne scenariusze przez gateway, nie tylko bezpośrednio na porcie 8081. Endpointy wewnętrzne powinny mieć jawny model uwierzytelniania usług; samo przekazywanie poprawnego JWT dowolnego klienta nie wyznacza odpowiedzialności.

**Co już jest dobrze i warto zachować**

- Kontrolery zwykle są krótkie, a przepływ kontroler → serwis → repozytorium można prześledzić bez znajomości dodatkowego frameworka.
- Zależności są wstrzykiwane przez konstruktor. Klasy można testować w izolacji.
- Ceny używają BigDecimal, co jest odpowiednim fundamentem do obliczeń pieniężnych.
- Rezerwacja wielu pozycji ma jedną lokalną transakcję. Trzeba zachować możliwość wycofania całej rezerwacji przy braku jednej pozycji.
- InternalTicket ma @Version. To właściwy kierunek dla kontroli współbieżności; wymaga spójności wszystkich zapisujących ścieżek i poprawnej obsługi commitu.
- [InternalTicketRepository:17][ticket-query] grupowo pobiera bilety wraz z potrzebnymi szczegółami.
- DTO i parametryzowane zapytania ograniczają sprzężenie z encjami. Wyszukiwanie dla AI ma projekcję w bazie i limit 50 rekordów.
- Clock w logice dat i stały zegar testowy umożliwiają deterministyczne sprawdzanie czasu.
- Są migracje Liquibase, testy parametryzowane i PostgreSQL Testcontainers. To sensowna baza do dalszego rozwoju, po naprawieniu izolacji testów.
- Weryfikacja JWT odbywa się także w EVENT, a nie wyłącznie na brzegu systemu.

**Testy wymagają najpierw naprawy izolacji**

[PrePost:13][test-base] uruchamia cały SpringBootTest, ale izoluje tylko PostgreSQL. [Profil testowy][test-config] nie wyłącza listenerów RabbitMQ ani rejestracji w Eurece i nie podmienia adresu brokera. Testy usuwania mogą więc publikować do lokalnych kolejek, a uruchomione listenery mogą odbierać rzeczywiste wiadomości. Same testy logiki powinny używać zamockowanego producenta i wyłączonych listenerów; testy komunikacji powinny dostać osobny RabbitMQ Testcontainer.

Fixture zawiera stałe INSERT, a [TicketReservationTest][reservation-test] ma dwie metody bez rollbacku testowego lub czyszczenia danych. Kolejne wykonanie fixture może kolidować z poprzednim. Dziedziczone initData.sql miesza dodatkowo dane demonstracyjne z testowymi. [Test usunięcia terminu:28][delete-test] wskazuje 111, podczas gdy fixture definiuje terminy 111111, 222222 i 333333. Są to rozbieżności wykryte w plikach, nie raport z wykonanego przebiegu testów.

Obecne testy obejmują m.in. regularne tworzenie wydarzeń, wybrane daty, pobieranie szczegółów oraz podstawowe odejmowanie i oddawanie miejsc. Nie znalazłem zabezpieczenia testami najważniejszych scenariuszy opisanych w E02–E10. Test wyszukiwania AI mockuje repozytorium, więc nie sprawdza rzeczywistego JPQL ani agregacji.

| Kolejność | Test, który warto dodać | Co zabezpiecza |
| --- | --- | --- |
| 1 | Migracje na pustej bazie z validate i ponowny start bez utraty danych | Trwałość i zgodność schematu |
| 2 | Macierz anonimowy / klient / admin / skaner / usługa | Granice uprawnień |
| 3 | Ostatnie miejsce: dwa typy taryf i dwa równoległe zamówienia | Wspólną pulę i współbieżność |
| 4 | Duplikaty rezerwacji, zwolnienia i wydania | Idempotencję |
| 5 | Awaria na ostatniej pozycji oraz przed/po commicie | Rollback i dostarczenie zdarzenia |
| 6 | Drugi skan QR, dwa równoległe skany, zły termin i token | Jednorazowe wejście |
| 7 | Wydanie dla dwóch zamówień z różnymi ID typów i egzemplarzy | Brak pomieszania QR |
| 8 | Anulowanie wydarzenia dzisiaj przed godziną rozpoczęcia | Żądanie rekompensaty i historię |
| 9 | Occasional, zagnieżdżona walidacja, paginacja, obrazy | Podstawowe kontrakty użytkowe |
| 10 | Zapytania AI oraz równoczesna zmiana ceny i rezerwacja | Poprawność odczytów i spójność zapisów |

**Konfiguracja, baza i utrzymanie**

[Parent POM][parent-pom] deklaruje Java 19 w properties, a konfiguracja pluginu kompilatora zawiera source/target 16. README wskazuje jeszcze inny minimalny poziom. Ujednolicić wymagany JDK i maven.compiler.release, przypiąć wersje pluginów oraz dodać Maven Wrapper. Parent umieszcza spring-boot-starter-test bez scope=test, więc zależności testowe dziedziczą się jak zwykłe zależności aplikacji. Boot 3.0.6 i ręcznie nadpisywane składniki, np. Spring Data, wymagają zaplanowanego przeglądu zgodności. Ten audyt nie obejmował skanowania CVE ani wyboru konkretnej wersji migracji.

Migracjom warto dodać zgodność typów ID z Long, jawne sekwencje, CHECK dla ilości/cen oraz brakujący klucz obcy event_occurrence.event_id. Unikalność rezerwacji i wydanego egzemplarza powinna chronić reguły biznesowe niezależnie od kodu Javy. Indeksy dobierać do rzeczywistych zapytań po terminie, wydarzeniu i zamówieniu, po sprawdzeniu planów wykonania. Obecny audyt nie uzasadnia dokładania indeksów w ciemno.

Do obsługi awarii przydadzą się liczniki odrzuconych rezerwacji, konfliktów wersji, ponowień, wiadomości w DLQ i wieku najstarszego rekordu outbox. Do diagnozy procesu potrzebne są orderId i eventId w logach. Dla danych biletowych warto przećwiczyć odtworzenie z kopii zapasowej, nie tylko utworzenie kopii.

**Proponowana kolejność pracy**

1. **Bezpieczne uruchamianie:** E01, zgodność migracji, izolacja testów oraz uprawnienia E02. Wynik: serwis można uruchamiać i testować bez kasowania danych i przypadkowej komunikacji z lokalnymi usługami.
2. **Poprawność pojedynczego zakupu:** wspólna pula E03, wydanie E04, QR E05, tworzenie E09 i anulowanie E10. Wynik: jedna poprawna operacja ma poprawny, sprawdzalny rezultat.
3. **Odporność na powtórzenia i awarie:** zapis rezerwacji, idempotencja E06, outbox E07, obsługa błędów E08, confirms i własna topologia kolejek. Wynik: restart lub powtórzone żądanie nie niszczy stanu sprzedaży.
4. **Prostszy kod i stabilne API:** stopniowo jedna encja zapisu na tabelę, pełna walidacja, paginacja, obrazy, wyszukiwanie oraz wspólne zasady błędów HTTP. Typowo: 400 dla błędnego wejścia, 404 dla brakującego zasobu, 409 dla konfliktu operacji; pusta lista wyników powinna być normalnym wynikiem wyszukiwania.
5. **Rozwój oparty na potrzebach:** wygasanie rezerwacji z uzgadnianiem statusu płatności, historia wejść i anulowań, raport dostępności per termin, kontrolowana zmiana pojemności, operacyjne wznowienie nieudanej operacji.

Najbardziej potrzebnym rozszerzeniem jest trwały model rezerwacji: kto zajął miejsca, dla jakiego zamówienia, do kiedy i w jakim stanie. Wygaśnięcie musi rozstrzygać wyścig z płatnością, więc nie może być ślepym zwiększaniem licznika. Rozbudowę AI, cache i dodatkowe usługi uzasadniałbym dopiero konkretnym zastosowaniem lub pomiarem. Obecne problemy można rozwiązać wewnątrz istniejącego EVENT.

[config]: C:/Users/19kos/Desktop/programming/Java/ticket-manager/ticket-manager/event/src/main/resources/application.yml:29
[image-model]: C:/Users/19kos/Desktop/programming/Java/ticket-manager/ticket-manager/event/src/main/java/pl/ticket/event/admin/image/model/AdminImage.java:24
[image-migration]: C:/Users/19kos/Desktop/programming/Java/ticket-manager/ticket-manager/event/src/main/resources/database/2024-11-24/01-create-image.sql:7
[security]: C:/Users/19kos/Desktop/programming/Java/ticket-manager/ticket-manager/event/src/main/java/pl/ticket/event/configuration/security/SecurityConfig.java:31
[pool]: C:/Users/19kos/Desktop/programming/Java/ticket-manager/ticket-manager/event/src/main/java/pl/ticket/event/admin/event/mapper/AdminEventMapper.java:71
[reservation]: C:/Users/19kos/Desktop/programming/Java/ticket-manager/ticket-manager/event/src/main/java/pl/ticket/event/internal/ticket/service/InternalTicketService.java:31
[listener]: C:/Users/19kos/Desktop/programming/Java/ticket-manager/ticket-manager/event/src/main/java/pl/ticket/event/internal/ticket/listener/TicketListener.java:51
[issued-query]: C:/Users/19kos/Desktop/programming/Java/ticket-manager/ticket-manager/event/src/main/java/pl/ticket/event/internal/ticket/repository/InternalConcreteTicketRepository.java:29
[qr-check]: C:/Users/19kos/Desktop/programming/Java/ticket-manager/ticket-manager/event/src/main/java/pl/ticket/event/internal/ticket/service/InternalConcreteTicketService.java:88
[qr-generator]: C:/Users/19kos/Desktop/programming/Java/ticket-manager/ticket-manager/event/src/main/java/pl/ticket/event/internal/ticket/service/QrGenerator.java:30
[issue]: C:/Users/19kos/Desktop/programming/Java/ticket-manager/ticket-manager/event/src/main/java/pl/ticket/event/internal/ticket/service/InternalConcreteTicketService.java:31
[order-row-migration]: C:/Users/19kos/Desktop/programming/Java/ticket-manager/ticket-manager/event/src/main/resources/database/2025-02-02/01-add-order-row-id-to-concrete-ticket.sql:3
[publish-before-commit]: C:/Users/19kos/Desktop/programming/Java/ticket-manager/ticket-manager/event/src/main/java/pl/ticket/event/internal/ticket/service/InternalTicketService.java:46
[reservation-error]: C:/Users/19kos/Desktop/programming/Java/ticket-manager/ticket-manager/event/src/main/java/pl/ticket/event/internal/ticket/exception/ReservationProcessExceptionHandler.java:26
[unbook-error]: C:/Users/19kos/Desktop/programming/Java/ticket-manager/ticket-manager/event/src/main/java/pl/ticket/event/internal/ticket/exception/UnbookProcessExceptionHandler.java:26
[issue-error]: C:/Users/19kos/Desktop/programming/Java/ticket-manager/ticket-manager/event/src/main/java/pl/ticket/event/internal/ticket/exception/TicketValidationExceptionHandler.java:25
[occasional]: C:/Users/19kos/Desktop/programming/Java/ticket-manager/ticket-manager/event/src/main/java/pl/ticket/event/admin/event/service/AdminEventService.java:47
[occurrence-migration]: C:/Users/19kos/Desktop/programming/Java/ticket-manager/ticket-manager/event/src/main/resources/database/2024-08-08/01-create-eventOccurance.sql:7
[cancel]: C:/Users/19kos/Desktop/programming/Java/ticket-manager/ticket-manager/event/src/main/java/pl/ticket/event/admin/ticket/service/AdminConcreteTicketService.java:34
[occurrence-delete]: C:/Users/19kos/Desktop/programming/Java/ticket-manager/ticket-manager/event/src/main/java/pl/ticket/event/admin/event_occurrence/service/AdminEventOccurrenceService.java:41
[creation-dto]: C:/Users/19kos/Desktop/programming/Java/ticket-manager/ticket-manager/event/src/main/java/pl/ticket/event/admin/event/dto/AdminEventCreationDto.java:22
[ticket-dto]: C:/Users/19kos/Desktop/programming/Java/ticket-manager/ticket-manager/event/src/main/java/pl/ticket/event/admin/ticket/dto/AdminTicketCreationDto.java:7
[update]: C:/Users/19kos/Desktop/programming/Java/ticket-manager/ticket-manager/event/src/main/java/pl/ticket/event/admin/event/service/AdminEventService.java:113
[pagination]: C:/Users/19kos/Desktop/programming/Java/ticket-manager/ticket-manager/event/src/main/java/pl/ticket/event/customer/event/service/EventService.java:51
[admin-pagination]: C:/Users/19kos/Desktop/programming/Java/ticket-manager/ticket-manager/event/src/main/java/pl/ticket/event/admin/event/service/AdminEventService.java:163
[event-query]: C:/Users/19kos/Desktop/programming/Java/ticket-manager/ticket-manager/event/src/main/java/pl/ticket/event/customer/event/repository/EventRepository.java:26
[image-config]: C:/Users/19kos/Desktop/programming/Java/ticket-manager/ticket-manager/event/src/main/resources/application.properties:1
[image-controller]: C:/Users/19kos/Desktop/programming/Java/ticket-manager/ticket-manager/event/src/main/java/pl/ticket/event/admin/image/controller/AdminImageController.java:19
[upload]: C:/Users/19kos/Desktop/programming/Java/ticket-manager/ticket-manager/event/src/main/java/pl/ticket/event/admin/image/service/AdminImageService.java:38
[image-map]: C:/Users/19kos/Desktop/programming/Java/ticket-manager/ticket-manager/event/src/main/java/pl/ticket/event/common/mapper/EventMapper.java:70
[admin-list]: C:/Users/19kos/Desktop/programming/Java/ticket-manager/ticket-manager/event/src/main/java/pl/ticket/event/admin/event/repository/AdminEventRepository.java:19
[ai-search]: C:/Users/19kos/Desktop/programming/Java/ticket-manager/ticket-manager/event/src/main/java/pl/ticket/event/internal/event/service/EventAiSearchService.java:31
[ai-query]: C:/Users/19kos/Desktop/programming/Java/ticket-manager/ticket-manager/event/src/main/java/pl/ticket/event/internal/event/repository/EventAiSearchRepository.java:36
[price-update]: C:/Users/19kos/Desktop/programming/Java/ticket-manager/ticket-manager/event/src/main/java/pl/ticket/event/admin/ticket/repository/AdminTicketRepository.java:34
[capacity]: C:/Users/19kos/Desktop/programming/Java/ticket-manager/ticket-manager/event/src/main/java/pl/ticket/event/customer/event/repository/EventRepository.java:29
[logging]: C:/Users/19kos/Desktop/programming/Java/ticket-manager/ticket-manager/event/src/main/resources/logback-spring.xml:4
[publisher]: C:/Users/19kos/Desktop/programming/Java/ticket-manager/ticket-manager/ampq/src/main/java/pl/ticket/amqp/RabbitMqMessageProducer.java:18
[rabbit-config]: C:/Users/19kos/Desktop/programming/Java/ticket-manager/ticket-manager/event/src/main/java/pl/ticket/event/configuration/rabbit/RabbitMqConfig.java:9
[order-queues]: C:/Users/19kos/Desktop/programming/Java/ticket-manager/ticket-manager/order/src/main/java/pl/ticket/configuration/rabbit/RabbitMqOrderConfig.java:77
[gateway]: C:/Users/19kos/Desktop/programming/Java/ticket-manager/ticket-manager/apigw/src/main/resources/application.yml:24
[ticket-query]: C:/Users/19kos/Desktop/programming/Java/ticket-manager/ticket-manager/event/src/main/java/pl/ticket/event/internal/ticket/repository/InternalTicketRepository.java:17
[test-base]: C:/Users/19kos/Desktop/programming/Java/ticket-manager/ticket-manager/event/src/test/java/pl/ticket/event/IT/PrePost.java:13
[test-config]: C:/Users/19kos/Desktop/programming/Java/ticket-manager/ticket-manager/event/src/main/resources/application-test.properties:1
[reservation-test]: C:/Users/19kos/Desktop/programming/Java/ticket-manager/ticket-manager/event/src/test/java/pl/ticket/event/IT/internal/TicketReservationTest.java:39
[delete-test]: C:/Users/19kos/Desktop/programming/Java/ticket-manager/ticket-manager/event/src/test/java/pl/ticket/event/IT/admin/AdminEventOccurrenceDeleteTest.java:28
[parent-pom]: C:/Users/19kos/Desktop/programming/Java/ticket-manager/ticket-manager/pom.xml:35
