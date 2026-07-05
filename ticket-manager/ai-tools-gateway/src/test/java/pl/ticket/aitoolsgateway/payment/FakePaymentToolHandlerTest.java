package pl.ticket.aitoolsgateway.payment;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class FakePaymentToolHandlerTest {

    @Test
    void shouldReturnDeterministicPaymentStatus() {
        FakePaymentToolHandler handler = new FakePaymentToolHandler();

        PaymentToolHandler.PaymentStatusResponse response = handler.getMyOrderPaymentStatus(1001L);

        assertThat(response)
                .isEqualTo(new PaymentToolHandler.PaymentStatusResponse(
                        1001L,
                        "PAID",
                        new BigDecimal("199.99"),
                        "FAKE_P24",
                        true
                ));
    }

    @Test
    void shouldReturnNotFoundPaymentStatusForUnknownOrder() {
        FakePaymentToolHandler handler = new FakePaymentToolHandler();

        PaymentToolHandler.PaymentStatusResponse response = handler.getMyOrderPaymentStatus(9999L);

        assertThat(response.found()).isFalse();
        assertThat(response.status()).isEqualTo("UNKNOWN");
    }
}
