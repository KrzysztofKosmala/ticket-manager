package pl.ticket.internal;


import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.ticket.dto.OrderSearchRequest;
import pl.ticket.dto.OrderSearchResponse;
import pl.ticket.internal.service.OrderSearchService;

@RestController
@RequestMapping("/internal/ai/orders")
@RequiredArgsConstructor
public class OrderAiSearchController {

    private final OrderSearchService orderSearchService;

    @PostMapping("/search")
    public OrderSearchResponse searchOrders(@RequestBody(required = false) OrderSearchRequest request,
                                            @AuthenticationPrincipal Jwt jwt) {
        return orderSearchService.searchOrders(jwt.getSubject(), request);
    }
}