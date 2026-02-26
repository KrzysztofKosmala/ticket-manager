# ai-tools-gateway

`ai-tools-gateway` jest **MCP Serverem** dla `ai-agent`.

## Kanały komunikacji

### A) `ai-agent` ⇄ `ai-tools-gateway` (MCP)

```text
[ai-agent]
  Spring AI MCP Client
        |
        | tools/list, tools/call (MCP)
        v
[ai-tools-gateway]
  Spring AI MCP Server (@Tool)
```

W tej warstwie agent nie wywołuje klasycznych endpointów REST typu `/api/v1/tools/...`.

### B) `ai-tools-gateway` ⇄ domenowe mikroserwisy (REST/Feign/gRPC)

```text
[ai-tools-gateway @Tool]
        |
        | Feign / REST / gRPC
        v
[order-service] [crm-service] [billing-service]
```

MCP służy do komunikacji agentowej, a nie do zastąpienia całej komunikacji między mikroserwisami domenowymi.

## Konfiguracja MCP server (gateway)

W `src/main/resources/application.yml`:

```yaml
spring:
  ai:
    mcp:
      server:
        enabled: true
        protocol: STREAMABLE
        endpoint: /mcp
        annotation-scanner:
          enabled: true
```

## Przykładowa konfiguracja MCP client (`ai-agent`)

Przykład znajduje się w pliku:

- `src/main/resources/application-mcp-client-example.yml`

## Dostępne narzędzie

- `order-service.searchOrders` — deleguje do `order-service` przez `OrderClient`.
