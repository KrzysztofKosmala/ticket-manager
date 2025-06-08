# ticket-manager

Ticket Manager to rozproszony system mikroserwisowy służący do zarządzania wydarzeniami i sprzedażą biletów. Każdy mikroserwis działa jako niezależna aplikacja zbudowana w oparciu o Java 17 oraz Spring Boot, co pozwala na przejrzyste wydzielenie odpowiedzialności, niezależne wdrażanie oraz łatwe skalowanie w zależności od potrzeb.

Architektura systemu opiera się na kilku kluczowych filarach:

🌐 Komunikacja i integracja
System wykorzystuje architekturę hybrydową, łączącą synchroniczną komunikację REST z asynchroniczną wymianą wiadomości.

Spring Web służy do udostępniania i konsumowania REST API między mikroserwisami. Do tworzenia klienta HTTP pomiędzy serwisami używany jest OpenFeign, który pozwala deklaratywnie definiować wywołania do innych usług.

W przypadkach, gdzie wymagana jest luźna zależność oraz odporność na błędy i opóźnienia, zastosowano RabbitMQ jako brokera wiadomości. Wydarzenia takie jak rezerwacja biletu są przesyłane do innych usług (np. Notification Service) przez kolejki wiadomości.

🚪 API Gateway i bezpieczeństwo
Wszystkie zewnętrzne żądania trafiają najpierw do API Gateway, zbudowanego na bazie Spring Cloud Gateway. Odpowiada on za:

routowanie żądań do odpowiednich mikroserwisów,

walidację i weryfikację tokenów JWT,

uproszczenie komunikacji z frontendem (jeden punkt wejścia do wielu usług).

Za uwierzytelnianie i autoryzację odpowiada Keycloak – zewnętrzny serwer tożsamości, który obsługuje logowanie użytkowników, zarządzanie kontami oraz przydzielanie ról (USER, ADMIN). Uwierzytelnianie odbywa się na podstawie tokenów JWT, które są sprawdzane przez gateway i mikroserwisy przy użyciu Spring Security.

Model zabezpieczeń oparty jest na RBAC – Role-Based Access Control – co pozwala precyzyjnie kontrolować dostęp do zasobów w zależności od uprawnień użytkownika.

🗄️ Warstwa danych i wersjonowanie
Każdy mikroserwis posiada własną, odizolowaną bazę danych, zgodnie z zasadą Database per service. System korzysta z PostgreSQL jako relacyjnego silnika bazy danych, a dostęp do danych realizowany jest przy użyciu Spring Data JPA (ORM).

Zmiany w strukturze bazy danych zarządzane są za pomocą Flyway, który umożliwia:

automatyczne wersjonowanie schematu bazy,

wykonywanie migracji przy starcie serwisu,

spójną kontrolę historii zmian w bazie.

📦 Konteneryzacja i uruchamianie
Wszystkie komponenty systemu – mikroserwisy, RabbitMQ, Keycloak, bazy danych – są uruchamiane i zarządzane w kontenerach Docker, co zapewnia powtarzalne i łatwe w konfiguracji środowisko uruchomieniowe. Ułatwia to lokalne uruchamianie, testowanie i wdrażanie systemu w różnych środowiskach.

🔍 Obserwowalność i monitoring
Do monitorowania działania systemu i śledzenia przepływu żądań zastosowano:

Spring Cloud Sleuth – automatyczne znakowanie żądań unikalnymi identyfikatorami (traceId, spanId),

Zipkin – śledzenie rozproszone (distributed tracing) umożliwiające analizę czasu przetwarzania i lokalizację problemów w łańcuchu usług.

Logi z mikroserwisów są również przygotowane do integracji z narzędziami typu ELK Stack, co umożliwia ich centralną analizę i przeszukiwanie.

✅ Testowanie
Projekt został pokryty testami jednostkowymi i integracyjnymi z użyciem:

JUnit 5 oraz Spring Test – do testowania komponentów w obrębie aplikacji,

Testcontainers – do uruchamiania tymczasowych kontenerów z PostgreSQL lub RabbitMQ w testach integracyjnych, co pozwala testować mikroserwisy w izolowanym i realistycznym środowisku.

📚 Dokumentacja API
Każdy mikroserwis automatycznie generuje dokumentację swojego REST API przy użyciu Swagger / OpenAPI (Springdoc). Interfejs dostępny jest pod endpointem /swagger-ui.html i umożliwia łatwe testowanie oraz podgląd struktury API.

👤 Przykładowy proces zamówienia

![image](https://github.com/user-attachments/assets/b5372393-d368-4d25-86d0-b38ad562fb08)

![image](https://github.com/user-attachments/assets/291a0177-e75a-46e3-9326-4b36e691990a)


placing order

🧩 Opis mikroserwisów (z wzorcami i rozwiązaniami)

🧪 Testowanie i jakość kodu

📚 Dokumentacja API (Swagger)

🐳 Uruchamianie aplikacji (Docker / Docker Compose)

📌 Plany rozwoju / TODO
