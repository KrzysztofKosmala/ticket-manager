package pl.ticket.aievals;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pl.ticket.aievals.support.AgentClient;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;

class OrderToolCallingIT {

    private static final String QUESTION = "Wyszukaj moje zamowienie o numerze 1001.";
    private final ObjectMapper json = new ObjectMapper();

    @Test
    @DisplayName("Order 1001 is retrieved through a successful, correctly parameterized tool call")
    void shouldSearchOrder1001UsingTool() throws Exception {
        AgentClient agent = AgentClient.fromConfiguration();

        // Open the database first so missing configuration fails before calling the model.
        try (Connection database = openDatabase()) {
            var response = agent.ask(QUESTION, null);
            System.out.printf("Conversation: %s%nAnswer: %s%n", response.conversationId(), response.answer());
            assertThat(response.status()).isEqualTo("COMPLETED");
            assertThat(response.conversationId()).isNotBlank();
            assertThat(response.answer()).isNotBlank();

            List<RecordedToolCall> calls = readToolCalls(database, response.conversationId());
            calls.forEach(call -> System.out.printf("Recorded tool call: %s%n", call));
            assertThat(calls).as("Tool calls recorded for this new conversation").isNotEmpty();
            assertThat(calls).allSatisfy(call -> {
                assertThat(call.runId()).isNotBlank();
                assertThat(call.runStatus()).isEqualTo("COMPLETED");
                assertThat(call.userMessage()).isEqualTo(QUESTION);
            });

            // Accept a valid execution even if the model made other calls or corrected an earlier mistake.
            assertThat(calls).as("At least one successful lookup of order 1001")
                    .anySatisfy(this::assertSuccessfulOrderLookup);
        }
    }

    private void assertSuccessfulOrderLookup(RecordedToolCall call) throws Exception {
        assertThat(call.toolName()).isEqualTo("tm_my_orders_search");
        assertThat(call.status()).isEqualTo("SUCCESS");

        JsonNode arguments = json.readTree(call.argumentsJson());
        JsonNode orderId = arguments.at("/request/filters/orderId");
        assertThat(orderId.isIntegralNumber()).as("orderId must be a JSON integer").isTrue();
        assertThat(orderId.longValue()).isEqualTo(1001L);

        // Spring AI MCP 1.1.7 records a content array; its text block contains the business JSON.
        JsonNode content = json.readTree(call.resultJson());
        assertThat(content.isArray()).as("MCP content array").isTrue();
        assertThat(content.size()).isEqualTo(1);
        assertThat(content.get(0).path("type").asText()).isEqualTo("text");
        JsonNode result = json.readTree(content.get(0).path("text").asText());
        assertThat(result.path("items").isArray()).isTrue();
        assertThat(result.path("items").size()).isEqualTo(1);
        JsonNode order = result.path("items").get(0);
        assertThat(order.path("id").longValue()).isEqualTo(1001L);
        assertThat(order.path("status").asText()).isEqualTo("PAID");
        assertThat(order.path("grossValue").decimalValue()).isEqualByComparingTo("199.99");
    }

    private Connection openDatabase() throws Exception {
        Properties configuration = new Properties();
        try (InputStream input = getClass().getResourceAsStream("/eval-local.properties")) {
            assertThat(input).as("eval-local.properties must exist in src/test/resources").isNotNull();
            configuration.load(input);
        }
        String url = configuration.getProperty("eval.database.url");
        assertThat(url).as("Set eval.database.url in eval-local.properties").isNotBlank();
        Connection database = DriverManager.getConnection(url,
                configuration.getProperty("eval.database.username"),
                configuration.getProperty("eval.database.password"));
        database.setReadOnly(true);
        return database;
    }

    private List<RecordedToolCall> readToolCalls(Connection database, String conversationId) throws Exception {
        String sql = """
                SELECT r.id AS run_id, r.status AS run_status, r.user_message,
                       t.tool_name, t.status, t.arguments_json, t.result_json
                FROM ai_agent_run r
                JOIN ai_tool_invocation t ON t.run_id = r.id AND t.conversation_id = r.conversation_id
                WHERE r.conversation_id = ?
                ORDER BY t.id
                """;
        try (var statement = database.prepareStatement(sql)) {
            statement.setString(1, conversationId);
            statement.setQueryTimeout(10);
            try (var rows = statement.executeQuery()) {
                List<RecordedToolCall> calls = new ArrayList<>();
                while (rows.next()) {
                    calls.add(new RecordedToolCall(rows.getString("run_id"), rows.getString("run_status"),
                            rows.getString("user_message"), rows.getString("tool_name"), rows.getString("status"),
                            rows.getString("arguments_json"), rows.getString("result_json")));
                }
                return calls;
            }
        }
    }

    private record RecordedToolCall(String runId, String runStatus, String userMessage, String toolName,
                                    String status, String argumentsJson, String resultJson) {
    }
}
