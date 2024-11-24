package pl.ticket.payment.service.p24.real;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import pl.ticket.dto.OrderEvent;
import pl.ticket.payment.model.PaymentStatus;
import pl.ticket.payment.service.p24.fake.PaymentInitializer;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class PaymentMethodP24 implements PaymentInitializer {
    private final PaymentMethodP24Config config;
    private final WebClient p24Client;

    public PaymentMethodP24(PaymentMethodP24Config config, WebClient p24Client) {
        this.config = config;
        this.p24Client = p24Client;
    }

    @Override
    public void initPayment(OrderEvent newOrder) {
        System.out.println("@#@# config: " + config.getMerchantId());
        log.info("Inicjalizacja platnosci");
        ResponseEntity<TransactionRegisterResponse> result = p24Client
                .post().uri("https://sandbox.przelewy24.pl/api/v1/transaction/register")
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(TransactionRegisterRequest.builder()
                        .merchantId(config.getMerchantId())
                        .posId(config.getPosId())
                        .sessionId(createSessionId(newOrder))
//                        .amount(newOrder.getGrossValue().movePointRight(2).intValue())
                        .currency("PLN")
//                        .description("Zamowienie id: " + newOrder.getOrderId())
//                        .email(newOrder.getEmail())
//                        .client(newOrder.getFirstname() + " " + newOrder.getLastname())
                        .country("PL")
                        .language("pl")
                        .urlReturn(config.isTestMode() ? config.getTestUrlReturn() : config.getUrlReturn())
                        .sign(createSign(newOrder, config))
                        .encoding("UTF-8")
                        .build())
                .retrieve()
                .onStatus(t -> t.is4xxClientError(),
                        clientResponse -> {
                            log.error("Coś poszło źle: " + clientResponse.statusCode().toString());
                            return Mono.empty();
                        })
                .toEntity(TransactionRegisterResponse.class)// encje ktora dostajemy w odpowiedzi
                .block();// synchronicznie odczytane dzieki block()

        if(result != null && result.getBody() != null && result.getBody().getData() != null){
            String url = config.isTestMode() ? config.getTestUrl() : config.getUrl() + "/trnRequest/"
                    + result.getBody().getData().token();
        }

        log.warn("Not fully implemented P24 payment service: " + result.getStatusCode());
    }

    @Override
    public void verifyPayment(OrderEvent orderEvent) {
    }



    private String createSessionId(OrderEvent newOrder) {
        return "order_id_";
    }

    private String createSign(OrderEvent newOrder, PaymentMethodP24Config config) {
        String json  = "{\"sessionId\":\""+ createSessionId(newOrder) +
                "\",\"merchantId\":"+ config.getMerchantId() +
//                ",\"amount\":"+ newOrder.getGrossValue().movePointRight(2).intValue() +
                ",\"currency\":\"PLN\",\"crc\":\""+ (config.isTestMode() ? config.getTestCrc(): config.getCrc())+"\"}";
        return DigestUtils.sha384Hex(json);
    }
}
