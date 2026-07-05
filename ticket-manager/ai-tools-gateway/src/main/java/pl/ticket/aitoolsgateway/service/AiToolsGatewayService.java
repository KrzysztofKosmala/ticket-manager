package pl.ticket.aitoolsgateway.service;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pl.ticket.aitoolsgateway.cart.CartToolHandler;
import pl.ticket.aitoolsgateway.event.EventToolHandler;
import pl.ticket.aitoolsgateway.orders.OrdersToolHandler;
import pl.ticket.aitoolsgateway.payment.PaymentToolHandler;
import pl.ticket.dto.OrderSearchRequest;
import pl.ticket.dto.OrderSearchResponse;

@Service
public class AiToolsGatewayService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AiToolsGatewayService.class);

    private final OrdersToolHandler ordersToolHandler;
    private final CartToolHandler cartToolHandler;
    private final PaymentToolHandler paymentToolHandler;
    private final EventToolHandler eventToolHandler;

    public AiToolsGatewayService(
            OrdersToolHandler ordersToolHandler,
            CartToolHandler cartToolHandler,
            PaymentToolHandler paymentToolHandler,
            EventToolHandler eventToolHandler
    ) {
        this.ordersToolHandler = ordersToolHandler;
        this.cartToolHandler = cartToolHandler;
        this.paymentToolHandler = paymentToolHandler;
        this.eventToolHandler = eventToolHandler;
    }

    @Tool(name = "tm_my_orders_search", description = "Wyszukuje zamowienia aktualnego uzytkownika po filtrach, sortowaniu i paginacji.")
    public OrderSearchResponse searchMyOrders(
            @ToolParam(description = "Parametry wyszukiwania moich zamowien", required = false)
            OrderSearchRequest request) {
        LOGGER.info("AI tools gateway tool started: toolName=tm_my_orders_search, request={}", request);
        OrderSearchResponse response = ordersToolHandler.searchMyOrders(request);
        LOGGER.info(
                "AI tools gateway tool completed: toolName=tm_my_orders_search, returnedItems={}, totalCount={}, hasMore={}",
                response.items().size(),
                response.totalCount(),
                response.hasMore()
        );
        return response;
    }

    @Tool(name = "tm_my_order_status_get", description = "Zwraca status mojego zamowienia po identyfikatorze zamowienia.")
    public OrdersToolHandler.OrderStatusResponse getMyOrderStatus(
            @ToolParam(description = "Identyfikator mojego zamowienia")
            Long orderId) {
        LOGGER.info("AI tools gateway tool started: toolName=tm_my_order_status_get, orderId={}", orderId);
        OrdersToolHandler.OrderStatusResponse response = ordersToolHandler.getMyOrderStatus(orderId);
        LOGGER.info("AI tools gateway tool completed: toolName=tm_my_order_status_get, response={}", response);
        return response;
    }

    @Tool(name = "tm_my_cart_get", description = "Zwraca zawartosc koszyka aktualnego uzytkownika.")
    public CartToolHandler.CartResponse getMyCart() {
        LOGGER.info("AI tools gateway tool started: toolName=tm_my_cart_get");
        CartToolHandler.CartResponse response = cartToolHandler.getMyCart();
        LOGGER.info(
                "AI tools gateway tool completed: toolName=tm_my_cart_get, cartId={}, itemCount={}, found={}",
                response.cartId(),
                response.itemCount(),
                response.found()
        );
        return response;
    }

    @Tool(name = "tm_my_cart_items_count", description = "Zwraca liczbe pozycji w koszyku aktualnego uzytkownika.")
    public CartToolHandler.CartItemsCountResponse countMyCartItems() {
        LOGGER.info("AI tools gateway tool started: toolName=tm_my_cart_items_count");
        CartToolHandler.CartItemsCountResponse response = cartToolHandler.countMyCartItems();
        LOGGER.info("AI tools gateway tool completed: toolName=tm_my_cart_items_count, response={}", response);
        return response;
    }

    @Tool(name = "tm_my_order_payment_status_get", description = "Zwraca status platnosci dla mojego zamowienia.")
    public PaymentToolHandler.PaymentStatusResponse getMyOrderPaymentStatus(
            @ToolParam(description = "Identyfikator mojego zamowienia")
            Long orderId) {
        LOGGER.info("AI tools gateway tool started: toolName=tm_my_order_payment_status_get, orderId={}", orderId);
        PaymentToolHandler.PaymentStatusResponse response = paymentToolHandler.getMyOrderPaymentStatus(orderId);
        LOGGER.info("AI tools gateway tool completed: toolName=tm_my_order_payment_status_get, response={}", response);
        return response;
    }

    @Tool(name = "tm_events_search", description = "Wyszukuje wydarzenia po nazwie, zakresie dat i miescie.")
    public EventToolHandler.EventSearchResponse searchEvents(
            @ToolParam(description = "Parametry wyszukiwania wydarzen", required = false)
            EventToolHandler.EventSearchRequest request) {
        LOGGER.info("AI tools gateway tool started: toolName=tm_events_search, request={}", request);
        EventToolHandler.EventSearchResponse response = eventToolHandler.searchEvents(request);
        LOGGER.info(
                "AI tools gateway tool completed: toolName=tm_events_search, returnedEvents={}",
                response.events().size()
        );
        return response;
    }

    @Tool(name = "tm_event_capacity_check", description = "Sprawdza dostepnosc miejsc na wydarzeniu po nazwie, dacie i miescie.")
    public EventToolHandler.EventCapacityResponse checkEventCapacity(
            @ToolParam(description = "Parametry wydarzenia do sprawdzenia")
            EventToolHandler.EventCapacityRequest request) {
        LOGGER.info("AI tools gateway tool started: toolName=tm_event_capacity_check, request={}", request);
        EventToolHandler.EventCapacityResponse response = eventToolHandler.checkEventCapacity(request);
        LOGGER.info("AI tools gateway tool completed: toolName=tm_event_capacity_check, response={}", response);
        return response;
    }

    @Tool(name = "tm_event_details_get", description = "Zwraca szczegoly wydarzenia po nazwie, dacie i miescie.")
    public EventToolHandler.EventDetailsResponse getEventDetails(
            @ToolParam(description = "Parametry wydarzenia")
            EventToolHandler.EventDetailsRequest request) {
        LOGGER.info("AI tools gateway tool started: toolName=tm_event_details_get, request={}", request);
        EventToolHandler.EventDetailsResponse response = eventToolHandler.getEventDetails(request);
        LOGGER.info("AI tools gateway tool completed: toolName=tm_event_details_get, response={}", response);
        return response;
    }
}
