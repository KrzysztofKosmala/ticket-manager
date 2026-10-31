package pl.ticket.aitoolsgateway.payment;

import java.math.BigDecimal;

public interface PaymentToolHandler {

    PaymentStatusResponse getMyOrderPaymentStatus(Long orderId);

    record PaymentStatusResponse(
            Long orderId,
            String status,
            BigDecimal amount,
            String provider,
            boolean found
    ) {
    }
}
