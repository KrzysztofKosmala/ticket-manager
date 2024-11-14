package pl.ticket.payment.controller;

import org.springframework.web.bind.annotation.*;
import pl.ticket.dto.OrderEvent;
import pl.ticket.payment.model.PaymentStatus;
import pl.ticket.payment.service.PaymentOrderStatusService;
import pl.ticket.payment.service.PaymentService;

@RestController
@RequestMapping("/api/v1/payments")
public class PaymentController {

    private final PaymentService paymentService;
    private final PaymentOrderStatusService paymentOrderStatusService;

    public PaymentController(PaymentService paymentService, PaymentOrderStatusService paymentOrderStatusService) {
        this.paymentService = paymentService;
        this.paymentOrderStatusService = paymentOrderStatusService;
    }

    @PostMapping
    public String initPayment(@RequestBody OrderEvent orderCreated) {
        return paymentService.initPayment(orderCreated);
    }
}

