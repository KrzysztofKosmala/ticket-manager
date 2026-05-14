package pl.ticket.aitoolsgateway.service;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;
import pl.ticket.dto.OrderSearchRequest;
import pl.ticket.dto.OrderSearchResponse;
import pl.ticket.feign.order.OrderClient;

@Service
public class AiToolsGatewayService {

    private final OrderClient orderClient;

    public AiToolsGatewayService(OrderClient orderClient) {
        this.orderClient = orderClient;
    }

    @Tool(name = "tm.orders.search", description = "Wyszukuje zamówienia użytkownika po filtrach, sortowaniu i paginacji.")
    public OrderSearchResponse searchOrders(
            @ToolParam(description = "Parametry wyszukiwania zamówień", required = false)
            OrderSearchRequest request) {
        return orderClient.searchOrders(request);
    }
}
