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
    private final SmokeToolService smokeToolService;

    public AiToolsGatewayService(OrderClient orderClient, SmokeToolService smokeToolService) {
        this.orderClient = orderClient;
        this.smokeToolService = smokeToolService;
    }

    @Tool(name = "tm_orders_search", description = "Wyszukuje zamówienia użytkownika po filtrach, sortowaniu i paginacji.")
    public OrderSearchResponse searchOrders(
            @ToolParam(description = "Parametry wyszukiwania zamówień", required = false)
            OrderSearchRequest request) {
        return orderClient.searchOrders(request);
    }
    @Tool(
            name = "tm_smoke_ping",
            description = """
                    Uzyj tego narzedzia, gdy uzytkownik prosi o test dymny, smoke test,
                    sprawdzenie dzialania narzedzi, polaczenia MCP albo integracji z ai-tools-gateway.
                    Narzedzie jest tylko do recznej diagnostyki i zwraca deterministyczna odpowiedz SMOKE_OK.
                    Nie uzywaj go do prawdziwych danych klienta, zamowien, platnosci ani konta.
                    """
    )
    public SmokeEchoResponse smokePing() {
        return smokeToolService.ping();
    }

    public record SmokeEchoResponse(
            String message,
            String status,
            String source
    ) {
    }
}
