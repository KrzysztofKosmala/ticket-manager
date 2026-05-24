package pl.ticket.aiagent.toolselection;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Locale;
import java.util.Set;

@Service
public class ToolCandidateSelector {

    private static final String ORDER_SEARCH_TOOL = "tm.orders.search";
    private static final ToolCandidate ORDER_SEARCH_CANDIDATE = new ToolCandidate(
            ORDER_SEARCH_TOOL,
            "Wyszukuje zamowienia uzytkownika po filtrach, sortowaniu i paginacji."
    );
    private static final Set<String> ORDER_KEYWORDS = Set.of(
            "order",
            "orders",
            "zamowienie",
            "zamowienia",
            "zamowien",
            "bilet",
            "bilety",
            "kupione"
    );

    private final ToolSelectionProperties properties;

    public ToolCandidateSelector(ToolSelectionProperties properties) {
        this.properties = properties;
    }

    public List<ToolCandidate> selectFor(String userMessage) {
        if (!StringUtils.hasText(userMessage)) {
            return List.of();
        }

        if (!properties.getAllowList().contains(ORDER_SEARCH_TOOL)) {
            return List.of();
        }

        if (!hasOrderIntent(userMessage)) {
            return List.of();
        }

        return List.of(ORDER_SEARCH_CANDIDATE);
    }

    private boolean hasOrderIntent(String userMessage) {
        String normalizedMessage = userMessage.toLowerCase(Locale.ROOT);
        return ORDER_KEYWORDS.stream().anyMatch(normalizedMessage::contains);
    }
}
