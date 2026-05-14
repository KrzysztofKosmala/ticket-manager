package pl.ticket.aiagent.tool;

import org.springframework.stereotype.Component;
import pl.ticket.dto.OrderSearchRequest;
import pl.ticket.dto.OrderSearchResponse;

import java.util.List;
import java.util.Map;

@Component
public class ToolRegistry {

    private final Map<String, ToolContract> toolsByName;

    public ToolRegistry() {
        ToolContract orderSearch = new ToolContract(
                ToolNames.ORDERS_SEARCH,
                ToolNames.LEGACY_ORDERS_SEARCH,
                "Wyszukuje zamowienia aktualnego uzytkownika po filtrach, sortowaniu i paginacji.",
                "args: filters(orderId, statuses, dateFrom, dateTo, minGrossValue, maxGrossValue), "
                        + "sort(field: placeDate|grossValue|orderStatus, direction: ASC|DESC), "
                        + "limit, offset, includeRows",
                ToolAccessMode.READ,
                "tools:orders.read",
                OrderSearchRequest.class,
                OrderSearchResponse.class
        );
        this.toolsByName = Map.of(
                ToolNames.ORDERS_SEARCH, orderSearch,
                ToolNames.LEGACY_ORDERS_SEARCH, orderSearch
        );
    }

    public ToolContract getRequired(String requestedName) {
        ToolContract contract = toolsByName.get(requestedName);
        if (contract == null) {
            throw new ToolExecutionException("Unsupported tool: " + requestedName);
        }
        return contract;
    }

    public List<ToolContract> plannerTools() {
        return List.of(getRequired(ToolNames.ORDERS_SEARCH));
    }
}
