package pl.ticket.payment.controller;

import org.springframework.web.bind.annotation.*;
import pl.ticket.payment.service.PaymentService;

@RestController
@RequestMapping("/api/v1/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @GetMapping("/{orderId}")
    public void payOrder(@PathVariable Long orderId){
        paymentService.simulateOrderPaymentStatus(orderId);
    }


}

