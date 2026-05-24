package pl.ticket.aiagent.toolselection;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import pl.ticket.aiagent.caller.CallerContext;
import pl.ticket.aiagent.toolpolicy.ToolPolicy;
import pl.ticket.aiagent.toolpolicy.ToolPolicyDecision;

import java.util.List;

@Component
@Profile({"local", "test", "smoke"})
public class StaticToolCandidateSelector implements ToolCandidateSelector {

    private static final ToolCandidate ORDER_SEARCH_CANDIDATE = new ToolCandidate(
            "tm.orders.search",
            "Wyszukuje zamowienia uzytkownika po filtrach, sortowaniu i paginacji."
    );

    private final ToolPolicy toolPolicy;

    public StaticToolCandidateSelector(ToolPolicy toolPolicy) {
        this.toolPolicy = toolPolicy;
    }

    @Override
    public List<ToolCandidate> selectFor(String userMessage, CallerContext callerContext) {
        if (!StringUtils.hasText(userMessage)) {
            return List.of();
        }

        ToolPolicyDecision decision = toolPolicy.evaluate(ORDER_SEARCH_CANDIDATE, callerContext);
        if (!decision.allowed()) {
            return List.of();
        }

        return List.of(ORDER_SEARCH_CANDIDATE);
    }
}
