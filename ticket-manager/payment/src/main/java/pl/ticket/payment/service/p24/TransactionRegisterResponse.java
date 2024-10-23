package pl.ticket.payment.service.p24;

import lombok.Getter;

@Getter
public class TransactionRegisterResponse {
    private Data data;

    record Data (String token){}
}
