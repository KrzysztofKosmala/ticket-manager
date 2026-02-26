# AI Agent + MCP – analiza i plan migracji

## 1) Analiza aktualnego `ai-agent`

## Co działa dzisiaj
- `AiAgentController` wystawia endpointy:
  - `POST /api/v1/aiagent/plan` – zwraca sam plan,
  - `POST /api/v1/aiagent/ask` – tworzy plan i wykonuje pierwszy krok typu `TOOL`.
- `PlannerService` buduje prompt planera, woła model przez `ChatClient`, waliduje JSON przez schema validator i ma pojedynczy retry „repair”.
- `AiAgentService` wykonuje **jeden** tool: `order-service.searchOrders` i mapuje `args` na `OrderSearchRequest`, a realne wykonanie robi przez `OrderClient` (Feign).
- Plan i tool-contract są obecnie „zaszyte” w promptcie + kodzie wykonywania (switch po nazwie narzędzia).

## Ograniczenia obecnego podejścia
- Tool-calling jest „lokalny” (w praktyce ręczny dispatcher), brak dynamicznego discovery narzędzi.
- Brak separacji odpowiedzialności między orkiestracją LLM i logiką biznesową narzędzi.
- Rosnące ryzyko konfliktu nazw narzędzi przy dołączaniu external MCP servers.
- Trudniejsze wersjonowanie narzędzi (zmiana kontraktu wymaga zmian w agencie).

---

## 2) Docelowa architektura i podział odpowiedzialności

## Architektura docelowa
- `ai-agent`:
  - odpowiedzialny za rozmowę, planowanie i orkiestrację,
  - działa jako MCP client do wielu serwerów,
  - utrzymuje lokalnie tylko „meta-tools” techniczne i policyjne.
- `ai-tools-gateway`:
  - wewnętrzny MCP server,
  - wystawia biznesowe narzędzia domenowe (orders, payments, promo, knowledge),
  - agreguje i normalizuje odpowiedzi z mikroserwisów.
- external MCP servers:
  - źródła pomocnicze (docs/github/knowledge),
  - odseparowane sandboxem i whitelistem narzędzi.

## Co przenieść do `ai-tools-gateway` (MCP tools)
Przenoś wszystko, co:
- ma logikę biznesową lub wymaga dostępu do wewnętrznych systemów,
- jest współdzielone między agentami,
- wymaga centralnego audytu/uprawnień.

Przykłady:
- `tm.orders.getOrderStatus`,
- `tm.orders.searchOrders`,
- `tm.promotions.getTerms`,
- `tm.knowledge.search` (RAG dla firmowej wiedzy),
- `tm.customer.getProfile`.

## Co może zostać lokalnie w `ai-agent`
- Narzędzia pomocnicze bez dostępu do systemów krytycznych:
  - normalizacja promptu,
  - redakcja PII przed logowaniem,
  - krótkie utility (`date_now`, `format_currency`).
- Guardrails/policy tools:
  - klasyfikacja intencji,
  - check polityk bezpieczeństwa,
  - wybór routingowy (który MCP server ma być użyty).

## Namespace / prefiksy narzędzi (antykolizyjne)
Rekomendacja:
- wewnętrzny gateway: `tm.<domain>.<action>`
  - np. `tm.orders.search`, `tm.knowledge.search`.
- external serwery:
  - zachowuj oryginalną nazwę, ale mapuj do lokalnego aliasu w agencie:
    - `ext.github.search_repositories`,
    - `ext.docs.fetch`.
- narzędzia lokalne:
  - `local.<capability>`.

Dodatkowo:
- trzymaj centralny registry nazw + whitelistę per use-case,
- wersjonowanie kontraktów narzędzi przez suffix lub metadata (`x-version`).

---

## 3) Konkretna lista zmian w `ai-agent`

## Zależności (`pom.xml`)
Dodaj (w module `ai-agent`):
- `org.springframework.ai:spring-ai-starter-model-*` (zależnie od modelu, np. ollama/openai),
- `org.springframework.ai:spring-ai-starter-mcp-client`,
- `org.springframework.boot:spring-boot-starter-validation` (jeśli jeszcze brak),
- opcjonalnie resilience:
  - `org.springframework.boot:spring-boot-starter-aop`,
  - `io.github.resilience4j:resilience4j-spring-boot3`.

Jeśli projekt jest na starszym BOM Spring Boot/Cloud, najpierw ujednolić wersje kompatybilne ze Spring AI i MCP.

## `application.yml` – konfiguracja MCP client (internal + external)
Przykładowy szkic:

```yaml
spring:
  ai:
    mcp:
      client:
        enabled: true
        request-timeout: 5s
        servers:
          ai-tools-gateway:
            transport: sse
            url: http://ai-tools-gateway:8080/mcp
            enabled-tools:
              - tm.orders.search
              - tm.orders.getStatus
              - tm.knowledge.search
          github-docs:
            transport: sse
            url: https://external-mcp.example.com/mcp
            headers:
              Authorization: Bearer ${EXTERNAL_MCP_TOKEN}
            enabled-tools:
              - ext.github.search_repositories
              - ext.docs.fetch

ai-agent:
  tools:
    allow-list:
      - tm.orders.search
      - tm.orders.getStatus
      - tm.knowledge.search
      - ext.github.search_repositories
      - local.date_now
```

Uwaga: nazwy właściwości mogą się różnić w zależności od wersji startera MCP; trzymaj się jednego release train i jego dokumentacji.

## Kod – ChatClient + ToolCallbackProvider + filtrowanie
Zmiany projektowe:
1. **Oddziel planner od execution layer**:
   - planner zwraca plan z nazwami narzędzi MCP (`tm.*`, `ext.*`, `local.*`).
2. **Execution layer**:
   - adapter `ToolExecutor` z trzema backendami:
     - LocalToolExecutor,
     - McpToolExecutor (internal),
     - McpToolExecutor (external).
3. **ToolCallbackProvider / registry**:
   - buduje listę dostępnych narzędzi z MCP + lokalnych,
   - stosuje allow-listę per endpoint/use-case,
   - loguje `toolName`, `serverId`, `duration`, `status`.
4. **Walidacja wejścia/wyjścia**:
   - JSON Schema per tool,
   - hard limit na argumenty i response size.
5. **Fallback policy**:
   - timeout MCP => alternatywa lokalna albo odpowiedź kontrolowana.

---

## 4) Minimalny szkielet `ai-tools-gateway` (MCP server)

## Struktura
- nowy moduł: `ai-tools-gateway`
- pakiety:
  - `pl.ticket.aitoolsgateway.mcp` – definicje tools/resources,
  - `pl.ticket.aitoolsgateway.service` – logika biznesowa,
  - `pl.ticket.aitoolsgateway.security` – auth + audyt.

## Zależności
- `spring-boot-starter-web` (lub webflux, zależnie od transportu MCP),
- `spring-ai-starter-mcp-server`,
- `spring-boot-starter-validation`,
- `spring-boot-starter-security`,
- `spring-boot-starter-actuator`,
- opcjonalnie `resilience4j`.

## `application.yml` (szkielet)

```yaml
server:
  port: 8105

spring:
  application:
    name: ai-tools-gateway
  ai:
    mcp:
      server:
        enabled: true
        path: /mcp

management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics

gateway:
  security:
    required-audience: ai-agent
  tools:
    timeout: 3s
```

## Przykładowe MCP tools
1) `tm.orders.getOrderStatus`
- input: `{ "orderId": "..." }`
- output: `{ "orderId": "...", "status": "PAID", "updatedAt": "..." }`
- backend: `OrderClient`/`OrderQueryService`.

2) `tm.knowledge.search`
- input: `{ "query": "...", "topK": 3 }`
- output: `{ "results": [{"title":"...","snippet":"...","source":"..."}] }`
- backend: firmowa baza wiedzy / indeks.

## Zasady bezpieczeństwa na start
- AuthN: JWT service-to-service (audience = `ai-tools-gateway`).
- AuthZ: scope per tool (np. `tools:orders.read`, `tools:knowledge.read`).
- Audyt: log każdego wywołania (`traceId`, `subject`, `tool`, `argsHash`, `latency`, `resultCode`).
- Timeouty/circuit-breaker: per tool + bulkhead.
- PII: maskowanie danych w logach i telemetry.

---

## 5) Plan migracji bez przestoju produkcji

## Etap 1 – uruchomienie gateway MCP z 1 tool
- Postaw `ai-tools-gateway` z `tm.orders.getOrderStatus`.
- Zaimplementuj pełny audyt + auth + timeout.
- Brak zmian ruchu produkcyjnego (tylko testy integracyjne i shadow traffic).

## Etap 2 – podłączenie `ai-agent` do gateway MCP
- Dodaj MCP client config do `ai-agent`.
- Włącz feature flag: `aiagent.mcp.internal.enabled=false` domyślnie.
- Canary: 1–5% ruchu przez MCP, reszta starym lokalnym toolem.

## Etap 3 – stopniowe przenoszenie narzędzi
- Dla każdego narzędzia:
  1) implementacja w gateway,
  2) kontrakt + testy,
  3) dual-run (local + MCP) i porównanie odpowiedzi,
  4) przełączenie flagą,
  5) usunięcie starej implementacji lokalnej.
- Priorytet: narzędzia o najwyższej wartości biznesowej i stabilnym API.

## Etap 4 – dodanie external MCP servers
- Dodaj 1 external serwer na początek (np. docs), tylko read-only.
- Wprowadź whitelistę narzędzi i osobne limity budżetu/tokenów.
- Routing policy:
  - pytania domenowe → `tm.*`,
  - dokumentacja/public knowledge → `ext.*`.
- Stopniowo zwiększaj zakres i monitoruj jakość odpowiedzi + koszty.

---

## KPI i gotowość produkcyjna
- P95 latency per tool/server,
- wskaźnik timeout/error,
- procent fallbacków,
- zgodność odpowiedzi MCP vs legacy tool,
- koszt/tokeny per ścieżka (`tm`, `ext`, `local`).

Taki plan pozwala przejść na MCP bez „big-bang” i bez utraty stabilności obecnego `ai-agent`.
