package pl.ticket.payment.service.p24.fake;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import pl.ticket.email.EmailClient;
import pl.ticket.payment.service.PaymentOrderStatusService;
import pl.ticket.payment.service.SagaPaymentProcessService;
import pl.ticket.payment.service.p24.real.PaymentMethodP24;
import pl.ticket.payment.service.p24.real.PaymentMethodP24Config;

@Configuration
public class PaymentConfig {

    //na podstawie propertisu app.payments.initializer
    // tworzy odpowiednia implementacje PaymentInitializer
    @Bean
    @ConditionalOnProperty(name="app.payments.initializer", matchIfMissing = true, havingValue = "emailSimpleService")
    public PaymentInitializer emailSimpleService(PaymentMethodP24Config config, WebClient p24Client){
        return new PaymentMethodP24(config, p24Client);
    }

    @Bean
    @ConditionalOnProperty(name="app.payments.initializer", havingValue = "fakePayment24Service")
    public PaymentInitializer fakePaymentService(SagaPaymentProcessService sagaPaymentProcessService,
                                                 PaymentOrderStatusService paymentOrderStatusService,
                                                 EmailClient emailClient){
        return new FakePayment24Service(sagaPaymentProcessService, paymentOrderStatusService, emailClient);
    }
}
