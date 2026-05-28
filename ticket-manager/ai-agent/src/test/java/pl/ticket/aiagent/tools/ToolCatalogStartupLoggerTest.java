package pl.ticket.aiagent.tools;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(OutputCaptureExtension.class)
class ToolCatalogStartupLoggerTest {

    @Test
    void shouldLogConfiguredToolsAndDiagnostics(CapturedOutput output) {
        ToolCatalog catalog = mock(ToolCatalog.class);
        when(catalog.configuredToolNames()).thenReturn(List.of("tm.orders.search"));
        when(catalog.diagnostics()).thenReturn(new ToolCatalogDiagnostics(
                List.of("tm.knowledge.search"),
                List.of("tm.internal.unconfigured"),
                List.of("tm.orders.search")
        ));
        ToolCatalogStartupLogger startupLogger = new ToolCatalogStartupLogger(catalog);

        startupLogger.logToolCatalog();

        assertThat(output)
                .contains("AI tool catalog configured tools: [tm.orders.search]")
                .contains("AI tool catalog configured tools missing from discovery: [tm.knowledge.search]")
                .contains("AI tool catalog discovered tools missing registry metadata: [tm.internal.unconfigured]")
                .contains("AI tool catalog duplicate discovered tool names: [tm.orders.search]");
    }

    @Test
    void shouldSkipEmptyDiagnosticWarnings(CapturedOutput output) {
        ToolCatalog catalog = mock(ToolCatalog.class);
        when(catalog.configuredToolNames()).thenReturn(List.of("tm.orders.search"));
        when(catalog.diagnostics()).thenReturn(new ToolCatalogDiagnostics(List.of(), List.of(), List.of()));
        ToolCatalogStartupLogger startupLogger = new ToolCatalogStartupLogger(catalog);

        startupLogger.logToolCatalog();

        assertThat(output)
                .contains("AI tool catalog configured tools: [tm.orders.search]")
                .doesNotContain("missing from discovery")
                .doesNotContain("missing registry metadata")
                .doesNotContain("duplicate discovered tool names");
    }
}
