package pl.ticket.feign.order;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import pl.ticket.dto.OrderSearchRequest;
import pl.ticket.dto.OrderSearchResponse;

@FeignClient("order")
public interface OrderClient {

    @PostMapping("/internal/ai/orders/search")
    OrderSearchResponse searchOrders(@RequestBody(required = false) OrderSearchRequest request);
}
