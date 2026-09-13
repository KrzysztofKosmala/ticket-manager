# Knowledge Service

Serwis dydaktyczny odpowiedzialny za indeksowanie dokumentacji Ticket Managera
i wyszukiwanie fragmentow podobnych do pytania. To czesc retrieval przyszlego
RAG-u. Generowanie odpowiedzi i decyzja o wywolaniu narzedzia naleza do ai-agent.

## Przeplyw i klasy

```text
IMPORT (reczne wlaczenie przy starcie)
KnowledgeIngestionRunner
  -> KnowledgeIngestionService.ingest()
     -> KnowledgeDocumentLoader.load() -> MarkdownDocumentReader -> Document
     -> TokenTextSplitter.split() -> mniejsze Document z metadata
     -> dodanie knowledgeBase i ingestionId
     -> VectorStore.add() -> Ollama/embeddinggemma -> PostgreSQL/pgvector
     -> VectorStore.delete() -> usuniecie poprzedniego snapshotu

WYSZUKIWANIE (HTTP)
POST /internal/knowledge/search
  -> KnowledgeSearchController
  -> KnowledgeSearchService.search(query, topK)
  -> VectorStore.similaritySearch(SearchRequest)
     -> embedding pytania w Ollamie
     -> wyszukiwanie w knowledge_document
  -> List<KnowledgeSearchResult>: text, source, score
```

| Pakiet | Odpowiedzialnosc |
| --- | --- |
| `configuration` | Nasz bean TokenTextSplitter i jego ustawienia. |
| `document` | Odczyt Markdown i wspolne nazwy metadanych. |
| `service` | Import i wyszukiwanie; tutaj widac kolejnosc operacji. |
| `controller` | Odebranie HTTP, walidacja requestu, przekazanie do serwisu. |
| `dto` | Dane wejsciowe API. |
| `model` | Wynik wyszukiwania oraz raport z importu. |

Spring tworzy PgVectorStore na podstawie startera i YAML-a. Wstrzykuje do niego
OllamaEmbeddingModel i JdbcTemplate. Wlasne repozytorium i encje JPA nie sa
potrzebne: operacje zapisu, usuwania i wyszukiwania realizuje VectorStore.
Liquibase tworzy tabele i indeks; `initialize-schema: false` wylacza tworzenie
schematu przez Spring AI.

## Uruchomienie

Komendy ponizej wykonuj z katalogu zawierajacego glowny `docker-compose.yml`
i moduly Java, czyli `ticket-manager/ticket-manager`.
Wymagania: Java 19, Maven, Docker Desktop.

1. Uruchom istniejace uslugi infrastruktury:

   ```shell
   docker compose up -d knowledge-postgres ollama
   ```

2. Pobierz model recznie (jednorazowo, jesli jeszcze go nie ma):

   ```shell
   docker exec ollama ollama pull embeddinggemma
   ```

3. Uruchom `pl.ticket.knowledge.KnowledgeServiceApplication` w IntelliJ.
   Domyslny profil to `local`. Przy pierwszym indeksowaniu ustaw program argument:

   ```text
   --knowledge.ingestion.on-startup=true
   ```

   Alternatywnie, z terminala:

   ```shell
   mvn -f knowledge-service/pom.xml spring-boot:run "-Dspring-boot.run.arguments=--knowledge.ingestion.on-startup=true"
   ```

4. Poczekaj na log `Knowledge ingestion completed`. Przy kolejnych zwyklych
   uruchomieniach usun argument; wiedza pozostaje w bazie.
5. Otworz `knowledge-service.http` w IntelliJ i uruchom przyklad wyszukiwania.

Lokalne adresy wynikaja z istniejacej konfiguracji Compose:

| Usluga | Adres |
| --- | --- |
| knowledge-service | `http://localhost:8111` |
| PostgreSQL | `jdbc:postgresql://localhost:52/knowledge`, `admin` / `password` |
| Ollama | `http://127.0.0.1:7869` |

Zmiana portu aplikacji: argument `--server.port=8112` i odpowiedni `baseUrl`
w pliku HTTP. Nie trzeba wtedy zmieniac portu PostgreSQL ani Ollamy.

## API

```http
POST http://localhost:8111/internal/knowledge/search
Content-Type: application/json

{"query":"Co oznacza status platnosci PAID?","topK":3}
```

Odpowiedz to tablica wynikow. Przykladowy ksztalt (score jest ilustracyjny):

```json
[
  {
    "text": "Status platnosci opisuje wylacznie operacje platnicza...",
    "source": "order-and-payment-statuses.md",
    "score": 0.81
  }
]
```

- `query` musi zawierac tekst; `topK` jest wymagane i ma zakres 1-10.
- `topK` to maksymalna liczba zwracanych chunkow, nie liczba plikow.
- `source` umozliwia wskazanie dokumentu, na ktorym opiera sie odpowiedz.
- `score` pochodzi z PgVectorStore (dla cosine: 1 minus odleglosc).
  Wyzej oznacza blizsze wektory. `0.81` nie oznacza 81% pewnosci ani prawdziwosci.
- Brak znalezionych fragmentow daje `200` i `[]`. Bledny request daje `400`.
  Awaria Ollamy lub bazy powoduje blad serwera, a nie udawany brak wynikow.
- Filtr `knowledgeBase = ticket-manager` jest ustawiany przez serwis.
- Nie ma jeszcze dostrojonego progu podobienstwa. Obce tematy moga zwracac
  najblizsze, lecz nieprzydatne fragmenty. Retrieval nie potwierdza, ze korpus
  zawiera odpowiedz; to osobny temat do oceny podczas integracji z agentem.

## Import i metadata

Kazdy import to pelny snapshot wszystkich plikow `classpath*:knowledge/*.md`.
Jeden Markdown moze dac kilka Document (sekcje), a jeden Document kilka chunkow.
Splitter kopiuje metadata i dodaje `parent_document_id`, `chunk_index`,
`total_chunks`. Numeracja jest lokalna dla Document, nie dla calego pliku.
`parent_document_id` identyfikuje obiekt sekcji z loadera; nie jest kluczem obcym
do osobnej tabeli przechowujacej dokumenty nadrzedne.

Serwis zachowuje ID chunkow i dodaje `knowledgeBase` oraz wspolne `ingestionId`.
Po zapisaniu nowej wersji usuwa stare rekordy tej samej bazy wiedzy.
`@Transactional` laczy JDBC add i delete w jedna transakcje; blad wykonania
powoduje rollback. Pusty wynik splittera przerywa import przed zapisem/usuwaniem.
Transakcja obejmuje rowniez czas wywolania modelu przez `VectorStore.add()`;
to celowy kompromis dla malego, recznie importowanego korpusu.
Rollback cofa zmiany bazy, ale nie cofa wykonanych obliczen embeddingow.

HNSW utrzymuje PostgreSQL. Nie implementujemy grafu w Javie. Przy malym zbiorze
planer moze wybrac pelny skan, nawet gdy indeks istnieje. Test obecnosci indeksu
nie jest dowodem, ze kazde zapytanie go wykorzystalo.

## Testy i build

Testy jednostkowe, reader/splitter i HTTP, bez Ollamy i PostgreSQL:

```shell
mvn -f knowledge-service/pom.xml test
```

Pakowanie wykonywalnego JAR-a (uruchamia tez powyzsze testy):

```shell
mvn -f knowledge-service/pom.xml package
java -jar knowledge-service/target/knowledge-service-1.0-SNAPSHOT.jar
```

Testy integracyjne na lokalnych PostgreSQL i Ollamie (musza juz dzialac):

```shell
mvn -f knowledge-service/pom.xml -Pintegration-tests verify
```

Zwykle `test` nie uruchamia klas `*IT`. Profil Maven `integration-tests`
wlacza Failsafe; nie jest profilem Springa. Testy IT uzywaja profilu Spring `local`.
Import przy starcie kontekstu testowego jest wylaczony. Operacje na danych sa
wykonywane w transakcjach testowych i cofane po tescie. Liquibase moze przy
starcie utworzyc brakujacy schemat lub wykonac oczekujace migracje.

Testy IT obejmuja: embedding 768-wymiarowy, zapis do pgvector, istnienie HNSW,
zastepowanie snapshotu, wyszukiwanie oczekiwanego zrodla, filtr bazy wiedzy
i pusty korpus. Trzy pytania w `KnowledgeSearchIT` to maly zestaw regresyjny;
nie porownujemy dokladnych wartosci score, ktore zaleza od modelu.

## Ocena i dalsza nauka

Architektura jest odpowiednia dla tego rozmiaru projektu: dwie operacje
biznesowe, gotowe API Spring AI i niewielkie modele danych. Wlasna hierarchia
adapterow, kolejki i import przyrostowy nie sa teraz potrzebne.

Podczas przegladu uzupelniono brakujace wyszukiwanie i API, ochrone przed pustym
importem oraz transakcje snapshotu. JPA zastapiono JDBC, bo modul nie ma encji.
Test zachowania metadanych uzywa teraz prawdziwego splittera, a nie recznie
przygotowanego wyniku mocka. Test importu nie przebudowuje juz trwale lokalnej
bazy przy starcie testowego kontekstu.

Swiadome ograniczenia wersji dydaktycznej:

- Jeden import naraz, jedna lokalna instancja. Transakcja nie rozwiazuje
  konkurencji kilku importerow ani problemu duzego korpusu.
- Dokumenty sa dolaczone do aplikacji. Zmiana plikow wymaga ponownego wczytania
  zasobow i pelnego importu. Model embeddingowy i wymiar musza byc zgodne dla
  importu i wyszukiwania; zmiana samego modelu czatowego nie wymaga reindeksacji.
- Endpoint jest lokalnym API bez uwierzytelniania. Prefiks `/internal` jest
  nazwa sciezki, nie zabezpieczeniem sieciowym.
- Pobieramy pelny tekst chunka, bez rerankera, hybrydowego wyszukiwania czy
  automatycznego podsumowania. Najpierw warto obejrzec wyniki i ich zrodla.

Endpoint jest podlaczony do `ai-tools-gateway` jako `tm_knowledge_search`.
Gateway wywoluje go przez `KnowledgeClient`, a agent dostaje znalezione
fragmenty wraz ze zrodlem jako wynik toola. Dokumenty nalezy traktowac jako
dane, a nie instrukcje do wykonania przez model.

Materialy: [PgVectorStore](https://docs.spring.io/spring-ai/reference/1.1/api/vectordbs/pgvector.html),
[ETL Spring AI](https://docs.spring.io/spring-ai/reference/1.1/api/etl-pipeline.html).
Kod modulu korzysta z Spring AI 1.1.7; dokumentacja galezi 1.1 moze opisywac
nowsza wersje poprawkowa.
