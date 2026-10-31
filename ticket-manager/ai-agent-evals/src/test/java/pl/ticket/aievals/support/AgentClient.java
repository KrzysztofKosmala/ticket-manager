package pl.ticket.aievals.support;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Properties;

public class AgentClient {

    private static final String CONFIGURATION_FILE = "eval-local.properties";

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final URI askUri;
    private final String token;
    private final Duration requestTimeout;

    public AgentClient(URI baseUrl, String token, Duration requestTimeout) {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        this.askUri = baseUrl.resolve("/api/v1/ai-agents/ask");
        this.token = token;
        this.requestTimeout = requestTimeout;
    }

    public static AgentClient fromConfiguration() {
        Properties configuration = loadConfiguration();
        String token = requiredProperty(configuration, "eval.token");
        URI baseUrl = URI.create(configuration.getProperty("eval.base-url", "http://localhost:8099"));
        Duration timeout = Duration.ofSeconds(Long.parseLong(
                configuration.getProperty("eval.request-timeout-seconds", "300")));
        return new AgentClient(baseUrl, token.strip(), timeout);
    }

    private static Properties loadConfiguration() {
        try (InputStream input = AgentClient.class.getClassLoader().getResourceAsStream(CONFIGURATION_FILE)) {
            if (input == null) {
                throw new IllegalStateException("Missing " + CONFIGURATION_FILE
                        + ". Create it from eval-local.properties.example.");
            }

            Properties properties = new Properties();
            properties.load(input);
            return properties;
        } catch (IOException exception) {
            throw new IllegalStateException("Could not read " + CONFIGURATION_FILE + ".", exception);
        }
    }

    private static String requiredProperty(Properties configuration, String name) {
        String value = configuration.getProperty(name);
        if (value == null || value.isBlank()) {
            throw new IllegalStateException("Set " + name + " in " + CONFIGURATION_FILE + ".");
        }
        return value;
    }

    public AskResponse ask(String message, String conversationId) throws IOException, InterruptedException {
        String body = objectMapper.writeValueAsString(new AskRequest(message, conversationId));
        HttpRequest request = HttpRequest.newBuilder(askUri)
                .timeout(requestTimeout)
                .header("Authorization", "Bearer " + token)
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        HttpResponse<String> response;
        try {
            response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException exception) {
            throw new IOException("Could not call " + askUri
                    + ". Check the running agent and eval.request-timeout-seconds.", exception);
        }

        if (response.statusCode() != 200) {
            throw new IllegalStateException("POST " + askUri + " returned HTTP " + response.statusCode()
                    + ". For 401/403 check eval.token in " + CONFIGURATION_FILE
                    + "; otherwise check the agent logs.");
        }

        return objectMapper.readValue(response.body(), AskResponse.class);
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private record AskRequest(String message, String conversationId) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record AskResponse(String answer, String status, String conversationId) {
    }
}
