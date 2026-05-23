package pl.ticket.aiagent;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AiAgentInstructionsTest {

    private final AiAgentInstructions instructions = new AiAgentInstructions();

    @Test
    void shouldDescribeCustomerSupportAgentRulesWithoutPlannerContract() {
        String systemPrompt = instructions.systemPrompt();

        assertThat(systemPrompt)
                .contains("Jestes asystentem obslugi klienta Ticket Manager.")
                .contains("Odpowiadasz po polsku.")
                .contains("Jesli potrzebujesz danych z systemu, uzyj dostepnych narzedzi.")
                .contains("Nie wymyslaj danych, ktorych nie zwrocil system ani narzedzie.")
                .contains("Akcje zmieniajace stan wymagaja potwierdzenia uzytkownika.")
                .doesNotContain("PLANU DZIALANIA")
                .doesNotContain("schemaVersion")
                .doesNotContain("ZWROC WYLACZNIE POPRAWNY JSON");
    }
}
