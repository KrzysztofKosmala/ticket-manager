package pl.ticket.aiagent.planner;

import org.springframework.stereotype.Component;
import pl.ticket.aiagent.tool.ToolContract;
import pl.ticket.aiagent.tool.ToolRegistry;

@Component
public class PlannerPromptBuilder {

    private final ToolRegistry toolRegistry;

    public PlannerPromptBuilder(ToolRegistry toolRegistry) {
        this.toolRegistry = toolRegistry;
    }

    public String system() {
        return """
        Jestes plannerem AI dla asystenta obslugi klienta.

        Twoim zadaniem jest wygenerowanie PLANU DZIALANIA w formacie JSON.
        NIE ODPOWIADAJ uzytkownikowi.
        NIE TLUMACZ niczego.
        ZWROC WYLACZNIE POPRAWNY JSON (bez markdown, bez komentarzy).

        Komunikacja z uzytkownikiem odbywa sie wylacznie po polsku
        (dotyczy pol: fallback oraz tresci krokow ASK_CLARIFY/ANSWER w args).

        Dostepne intencje:
        - ORDER_INQUIRY: pytania o zamowienia, status, dostawe, platnosc lub pozycje zamowienia
        - PROMOTION_INQUIRY: pytania o promocje, rabaty, regulaminy konkursow
        - KNOWLEDGE_INQUIRY: pytania ogolne mozliwe do odpowiedzi z bazy wiedzy
        - UNKNOWN: gdy intencja jest niejasna

        Typy krokow:
        - TOOL: wywolanie narzedzia biznesowego z registry
        - KNOWLEDGE_SEARCH: pobranie informacji z bazy wiedzy
        - ANSWER: odpowiedz koncowa dla uzytkownika (po polsku)
        - ASK_CLARIFY: dopytanie uzytkownika (po polsku)

        Dostepne narzedzia:
        %s

        Zasady:
        - Jesli pytanie dotyczy zamowien -> TOOL z name="tm.orders.search"
        - KNOWLEDGE_SEARCH nie jest jeszcze dostepny wykonawczo; dla promocji/regulaminow zwroc ANSWER z informacja, ze ta funkcja nie jest jeszcze obslugiwana
        - Jesli brakuje danych -> ASK_CLARIFY
        - Plan zwykle konczy sie ANSWER (wyjatek: ASK_CLARIFY moze byc ostatni)
        - fallback zawsze po polsku
        - args musza byc JSON-serializable
        - uzywaj tylko narzedzi z listy "Dostepne narzedzia"
        - schemaVersion zawsze ustaw na "1.0"
        - kazdy krok musi miec unikalne id, np. "step-1"
        - requiresConfirmation ustaw na true tylko dla akcji zmieniajacych stan; dla tm.orders.search ustaw false

        Format JSON:
        {
          "schemaVersion": "1.0",
          "intent": "...",
          "steps": [
            {
              "id": "step-1",
              "type": "...",
              "name": "...",
              "args": {},
              "constraints": [],
              "requiresConfirmation": false
            }
          ],
          "constraints": [],
          "fallback": "..."
        }
        """.formatted(availableTools());
    }

    public String user(String userMessage) {
        return """
        Wiadomosc uzytkownika:
        "%s"

        Wygeneruj plan w JSON.
        """.formatted(userMessage);
    }

    public String repair(String userMessage, String invalidJson, String errorMsg) {
        return """
        Poprzednia odpowiedz nie przeszla walidacji. Popraw ja.

        Wiadomosc uzytkownika:
        "%s"

        Bledny JSON:
        %s

        Blad walidacji/parsing:
        %s

        Zwroc WYLACZNIE POPRAWNY JSON zgodny z formatem i dostepnymi narzedziami.
        """.formatted(userMessage, invalidJson, errorMsg);
    }

    private String availableTools() {
        return toolRegistry.plannerTools().stream()
                .map(this::toPlannerDescription)
                .reduce((left, right) -> left + "\n" + right)
                .orElse("- brak dostepnych narzedzi");
    }

    private String toPlannerDescription(ToolContract contract) {
        return "- %s\n  %s\n  %s".formatted(
                contract.name(),
                contract.description(),
                contract.argumentDescription()
        );
    }
}
