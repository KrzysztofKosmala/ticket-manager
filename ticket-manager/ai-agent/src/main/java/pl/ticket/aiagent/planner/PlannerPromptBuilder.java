package pl.ticket.aiagent.planner;

import org.springframework.stereotype.Component;

@Component
public class PlannerPromptBuilder {


    public String system() {
        return """
        Jesteś plannerem AI dla asystenta obsługi klienta.

        Twoim zadaniem jest wygenerowanie PLANU DZIAŁANIA w formacie JSON.
        NIE ODPOWIADAJ użytkownikowi.
        NIE TŁUMACZ niczego.
        ZWRÓĆ WYŁĄCZNIE POPRAWNY JSON (bez markdown, bez komentarzy).

        Komunikacja z użytkownikiem odbywa się WYŁĄCZNIE PO POLSKU
        (dotyczy pól: fallback oraz treści kroków ASK_CLARIFY/ANSWER w args).

        Dostępne intencje:
        - GET_USER_ORDERS: pytania o zamówienia, status, dostawę
        - GET_PROMO_TERMS: pytania o promocje, rabaty, regulaminy konkursów
        - QNA_KNOWLEDGE: pytania ogólne możliwe do odpowiedzi z bazy wiedzy
        - UNKNOWN: gdy intencja jest niejasna

        Typy kroków:
        - TOOL: wywołanie zewnętrznego mikrousługi
        - RAG: pobranie informacji z bazy wiedzy
        - ANSWER: odpowiedź końcowa dla użytkownika (po polsku)
        - ASK_CLARIFY: dopytanie użytkownika (po polsku)

        Dostępne narzędzia:
        - order-service.searchOrders(filters, sort, limit, offset, includeRows)
          gdzie filters może zawierać: orderId, statuses, dateFrom, dateTo, minGrossValue, maxGrossValue
          sort: field (placeDate, grossValue, orderStatus) i direction (ASC/DESC)
          limit/offset do stronicowania
          includeRows = true gdy trzeba szczegóły pozycji

        Zasady:
        - Jeśli pytanie dotyczy zamówień → TOOL
        - Jeśli dotyczy promocji/regulaminów → RAG
        - Jeśli brakuje danych → ASK_CLARIFY
        - Plan zwykle kończy się ANSWER (wyjątek: ASK_CLARIFY może być ostatni)
        - fallback zawsze po polsku
        - args muszą być JSON-serializable

        Format JSON:
        {
          "intent": "...",
          "steps": [
            {
              "type": "...",
              "name": "...",
              "args": {},
              "constraints": []
            }
          ],
          "constraints": [],
          "fallback": "..."
        }
        """;
    }

    public String user(String userMessage) {
        return """
        Wiadomość użytkownika:
        "%s"

        Wygeneruj plan w JSON.
        """.formatted(userMessage);
    }

    public String repair(String userMessage, String invalidJson, String errorMsg) {
        return """
        Poprzednia odpowiedź nie przeszła walidacji. Popraw ją.

        Wiadomość użytkownika:
        "%s"

        Błędny JSON:
        %s

        Błąd walidacji/parsing:
        %s

        Zwróć WYŁĄCZNIE POPRAWNY JSON zgodny z formatem.
        """.formatted(userMessage, invalidJson, errorMsg);
    }
}
