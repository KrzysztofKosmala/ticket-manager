# AI Agent Session Summary

## Goal

Move `ai-agent` toward a production-style agent architecture:

```text
user -> ai-agent -> planner -> MCP -> ai-tools-gateway -> domain services
```

The main principle: the LLM suggests a plan, but backend code validates, authorizes, audits, and executes it.

## What changed

### `ai-agent`

- Added a real Maven module descriptor: `ai-agent/pom.xml`.
- Added `AiAgentApplication`.
- Removed direct Feign usage from `ai-agent`.
- Removed `AuthFeignInterceptor` from `ai-agent`.
- Added MCP client configuration for `ai-tools-gateway`.
- Added tool execution layers:
  - `ToolRegistry`
  - `ToolContract`
  - `ToolPolicyService`
  - `ToolExecutionService`
  - `ToolAuditService`
  - `CallerContext`
  - `AiAgentToolProperties`
- `AiAgentService` now orchestrates planning and delegates tool execution to `ToolExecutionService`.
- `AiAgentController` now builds `CallerContext` from JWT.

### Planner

- `Plan` was changed from a mutable class with public fields to immutable records.
- Plan now includes:
  - `schemaVersion`
  - `intent`
  - `steps`
  - `constraints`
  - `fallback`
- Step now includes:
  - `id`
  - `type`
  - `name`
  - `args`
  - `constraints`
  - `requiresConfirmation`
- Intent enum changed to:
  - `ORDER_INQUIRY`
  - `PROMOTION_INQUIRY`
  - `KNOWLEDGE_INQUIRY`
  - `UNKNOWN`
- Step type enum changed to:
  - `TOOL`
  - `KNOWLEDGE_SEARCH`
  - `ANSWER`
  - `ASK_CLARIFY`
- Removed unsupported `ASYNC_JOB`.
- Renamed `RAG` to `KNOWLEDGE_SEARCH`.
- Added `PlanSemanticValidator`.
- `PlannerService` now validates:
  - JSON schema
  - Java parsing
  - semantic correctness against `ToolRegistry`
- `PlannerPromptBuilder` now gets tool descriptions from `ToolRegistry`.

### `ai-tools-gateway`

- Tool name changed from `order-service.searchOrders` to `tm.orders.search`.
- MCP server starter changed to the Spring AI `1.0.0-M6` compatible artifact:
  - `spring-ai-mcp-server-webmvc-spring-boot-starter`
- Gateway MCP transport adjusted to SSE:
  - `/sse`
  - `/mcp/message`

## Current Flow

Example request:

```http
POST /api/v1/aiagent/ask
Authorization: Bearer <JWT>
Content-Type: application/json
```

```json
{
  "message": "Pokaż moje ostatnie zamówienia"
}
```

Flow:

```text
AiAgentController
 -> CallerContext.from(jwt)
 -> AiAgentService
 -> PlannerService
 -> PlannerPromptBuilder
 -> ToolRegistry
 -> ChatClient / Ollama
 -> PlanSchemaValidator
 -> PlanSemanticValidator
 -> AiAgentService selects first TOOL step
 -> ToolExecutionService
 -> ToolRegistry
 -> ToolPolicyService
 -> Spring AI MCP FunctionCallback
 -> ai-tools-gateway
 -> AiToolsGatewayService.searchOrders
 -> Feign OrderClient
 -> order-service
 -> PostgreSQL
 -> result returns back to user
```

Current response is still technical:

```text
PlanExecutionResult = plan + raw orderSearchResponse
```

There is no final natural-language `AnswerComposer` yet.

## Important Design Decisions

- `ai-agent` no longer talks directly to `order-service`.
- Feign remains only in `ai-tools-gateway`.
- `tm.orders.search` is a business tool name, not a microservice-specific name.
- Tool execution is controlled by registry, policy, and audit.
- LLM output is treated as untrusted data.
- Planner uses one executable tool for now.
- `KNOWLEDGE_SEARCH` exists in the contract but is not executable yet.

## Known Gaps

- `AiAgentService` executes only the first `TOOL` step.
- No `PlanExecutor` yet.
- No `AnswerComposer` yet.
- No runtime confirmation that MCP tool discovery works.
- No runtime confirmation that JWT is propagated through MCP to `ai-tools-gateway`.
- Gateway authorization is still too broad; it mainly checks `authenticated()`.
- Prompt still hardcodes intent/rule descriptions; tools are already registry-driven.
- No RAG yet.
- No external MCP tools yet.
- No integration test for the full AI flow yet.

## Next Things To Test

1. Start `ai-tools-gateway`.
2. Confirm it exposes MCP tool `tm.orders.search`.
3. Start `ai-agent`.
4. Confirm `ai-agent` discovers the MCP tool.
5. Call:

```http
POST /api/v1/aiagent/ask
```

with:

```json
{
  "message": "Pokaż moje ostatnie zamówienia"
}
```

6. Check whether request reaches `ai-tools-gateway`.
7. Check whether JWT reaches `order-service`.
8. If JWT is missing, implement explicit MCP token relay.

## Build Verification

The following builds passed:

```text
mvn -pl ai-agent -am -DskipTests compile
mvn -pl ai-tools-gateway -am -DskipTests compile
```

Maven still reports existing global project warnings:

- missing explicit `maven-compiler-plugin` version in parent POM
- duplicate Testcontainers dependencies in some modules
- effective compiler target appears as 16 despite Java 19 module properties

These are not caused by the AI changes, but should be cleaned up later.

## Related TODO

See:

```text
docs/ai-agent-todo.md
```
