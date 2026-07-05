package pl.ticket.aitoolsgateway.payment;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;

@Component
@ConditionalOnProperty(name = "ai-tools-gateway.tools.payment.handler", havingValue = "fake", matchIfMissing = true)
public class FakePaymentToolHandler implements PaymentToolHandler {

    private static final Map<Long, PaymentStatusResponse> PAYMENTS = Map.of(
            1001L, new PaymentStatusResponse(1001L, "PAID", new BigDecimal("199.99"), "FAKE_P24", true),
            1002L, new PaymentStatusResponse(1002L, "PENDING", new BigDecimal("79.50"), "FAKE_P24", true),
            1003L, new PaymentStatusResponse(1003L, "REJECTED", new BigDecimal("149.00"), "FAKE_P24", true)
    );

    @Override
    public PaymentStatusResponse getMyOrderPaymentStatus(Long orderId) {
        return PAYMENTS.getOrDefault(
                orderId,
                new PaymentStatusResponse(orderId, "UNKNOWN", BigDecimal.ZERO, "FAKE_P24", false)
        );
    }
}
