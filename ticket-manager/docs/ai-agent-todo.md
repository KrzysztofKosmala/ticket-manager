# AI Agent TODO

This roadmap is ordered for organic development: first make the current flow reliable, then clean up developer experience, then harden architecture and security, and only later add advanced AI capabilities.

## Phase 0 - Make the current state safe

- Fix the current `ai-agent` compilation failure and keep `mvn -pl ai-agent -am -DskipTests compile` green before adding more features.
- Ensure local generated files and model data such as `ollama/` and IDE-only files do not get committed.
- Fix UTF-8/encoding issues in source files and docs, for example broken Polish text like `CzeĹ›Ä‡`.
- Rename or remove the temporary `/check` endpoint and clean up copy-pasted method names.
- Add `@Valid` to controller request bodies so `PlanRequest.message` validation is actually enforced.
- Validate that all plan `step.id` values are unique.
- Add a minimal README section for `ai-agent`: purpose, startup order, required services, Swagger URL, sample request, and known limitations.

## Phase 1 - Stabilize the vertical AI flow

- Replace single-tool execution with a real `PlanExecutor` that processes steps by order.
- Store step results by `step.id` so later steps can use earlier outputs.
- Replace the order-specific `PlanExecutionResult.orderSearchResponse` with a generic execution response model that can hold results from many tool/action types.
- Add explicit handling for `ASK_CLARIFY`, `ANSWER`, and unsupported `KNOWLEDGE_SEARCH`.
- Add `AnswerComposer` that converts plan + tool results into a final Polish user-facing answer.
- Introduce global exception handling for the whole `ai-agent` module: move custom exceptions to `pl.ticket.aiagent.exception`, add a shared `ApiError` response model, and handle all controller/service/planner/tool errors with `@RestControllerAdvice`.
- Add tests for `PlanSchemaValidator`, `PlanSemanticValidator`, `ToolPolicyService`, `ToolExecutionService`, and `PlanExecutor`.
- Add controller tests for request validation, security behavior, and error responses.
- Add runtime test for `ai-agent -> MCP -> ai-tools-gateway -> order-service`.

## Phase 2 - Clean configuration and local developer experience

- Split `ai-agent` configuration into explicit profiles such as `local`, `docker`, and `prod`.
- Remove production-unsafe settings from default/prod config, especially `ddl-auto: update`, `liquibase.drop-first: true`, `show-sql: true`, and hardcoded `localhost` infrastructure URLs.
- Replace hardcoded local service URLs with configuration properties and environment-variable overrides.
- Add profile-based LLM provider switching using Spring AI configuration instead of a custom provider framework.
- Create a local Ollama profile for development, for example `local-ollama`.
- Create an OpenAI or Azure OpenAI profile for demo/production-style runs.
- Externalize model names, base URLs, API keys, timeouts, and temperature settings through environment variables.
- Add a fake or mocked chat model configuration for tests so planner tests do not call a real LLM.
- Log the active LLM provider and model at startup.
- Document how to switch providers in the `ai-agent` README.

## Phase 3 - Central Swagger through API Gateway

- Add central Swagger UI to `apigw` so one page lists API docs for all services.
- Configure `apigw` routes for each service OpenAPI document, for example `/ai-agent/v3/api-docs -> ai-agent:/v3/api-docs`.
- Configure Swagger UI groups in `apigw` for all relevant services: `customer`, `order`, `cart`, `discount`, `event`, `payment`, `ai-agent`, and `ai-tools-gateway`.
- Add proper `RewritePath` or `SetPath` filters for routed `/v3/api-docs` endpoints so each downstream service receives `/v3/api-docs`.
- Update `apigw` security to permit Swagger UI assets and routed OpenAPI docs without authentication, while keeping business endpoints protected.
- Move browser-facing CORS policy to `apigw` and define allowed frontend origins there.
- After central Swagger UI works through `apigw`, remove standalone Swagger UI dependencies/configuration from microservices where they are no longer needed.
- Keep OpenAPI generation in microservices even after removing their local Swagger UI, because `apigw` still needs each service's `/v3/api-docs`.
- Document the local and production Swagger URLs, including which origins are allowed for browser access.

## Phase 4 - Tool catalog and MCP discovery

- Replace the static `ToolRegistry` with a single `ToolCatalog` that is the source of truth for all agent tools.
- Make `ToolCatalog` aggregate tools from local callbacks, internal MCP servers such as `ai-tools-gateway`, future external MCP servers, and future RAG/knowledge tools.
- Remove the current split where the planner reads static `ToolRegistry` contracts but execution reads dynamically discovered MCP `ToolCallbackProvider` callbacks.
- Add startup diagnostics that log MCP-discovered tool names, including tools exposed by `ai-tools-gateway`.
- Add a debug/admin endpoint or actuator-style view that shows enabled tools, disabled tools, source, access mode, and required scopes.
- Decide how discovered MCP tools are normalized into internal `ToolContract` metadata: name, description, input schema, output schema, access mode, required scopes, and enabled/disabled status.
- Ensure planner prompt generation, tool execution, and policy checks all read from the same catalog instead of hardcoded or duplicated tool definitions.

## Phase 5 - Prompt and policy hardening

- Move intent descriptions and planner rules into a versioned catalog/config.
- Generate planner prompt from `ToolCatalog`, intent catalog, planner rules, and plan schema.
- Add policy rules for read/write tools and per-use-case allow-lists.
- Decide how to handle unsupported intents before knowledge search is implemented.
- Add confirmation flow for steps with `requiresConfirmation=true`.

## Phase 6 - Security and auth propagation

- Verify whether Spring AI MCP client forwards the incoming `Authorization` header from `ai-agent` to `ai-tools-gateway`.
- Implement explicit token relay for `ai-agent -> MCP -> ai-tools-gateway` if the header is not propagated automatically.
- Decide the final auth model: user token relay vs service-to-service token plus trusted user context.
- Replace temporary `permitAll()` for MCP transport endpoints (`/sse`, `/mcp/message`) with proper service-to-service authentication and scope/audience checks.
- Add gateway-side authorization for tool scopes/audience, not only `authenticated()`.
- Add an integration test proving JWT reaches `order-service` through `ai-agent -> MCP -> ai-tools-gateway`.
- Analyze and implement scope assignment in the project: define where scopes should live in Keycloak, how they are added to JWTs, and where they should be enforced (`ai-agent`, `ai-tools-gateway`, domain services).

## Phase 7 - Observability

- Add metrics for planner latency, tool latency, tool failures, and repair attempts.
- Add trace/correlation IDs across `ai-agent`, `ai-tools-gateway`, and domain services.
- Add structured audit events for plan creation and tool execution.

## Phase 8 - Project-wide portfolio hardening

- Normalize Java version configuration across parent and modules; avoid conflicting Java 19 properties with compiler target 17.
- Pin Maven plugin versions in the parent POM to remove build warnings.
- Clean duplicate dependencies and Maven warnings across the monorepo.
- Expand README documentation at the repository level: architecture diagram, service map, startup instructions, test strategy, and known tradeoffs.

## Phase 9 - Advanced capabilities

- Add real `KNOWLEDGE_SEARCH` / RAG support.
- Add external MCP tools.
- Add multi-tool and multi-step orchestration beyond the initial order-search use case.
- Plan a full project migration to newer Spring Boot and Spring Cloud versions after the AI flow is stable.
- Check compatibility service by service before migration: Spring Cloud, Eureka, Gateway, OpenFeign, Security, JPA, Micrometer/Zipkin, Testcontainers, and Springdoc.
- Decide whether AI modules should stay on a newer independent stack or whether the whole monorepo should be aligned to one Spring platform version.
