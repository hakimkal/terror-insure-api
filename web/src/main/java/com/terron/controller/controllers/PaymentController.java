package com.terron.controller.controllers;

import com.terron.dto.BankTransferRequest;
import com.terron.dto.CardPaymentRequest;
import com.terron.dto.PaymentResponse;
import com.terron.services.payment.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.naming.ServiceUnavailableException;

@RestController
@RequestMapping("/v1/payment")
public class PaymentController {

    private final PaymentService paymentService;

    @Autowired
    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/card-payment/{companyId}")
    public PaymentResponse makeCardPayment(@RequestBody CardPaymentRequest request, @PathVariable Long companyId) throws ServiceUnavailableException {
        return paymentService.makeCardPayment(request, companyId);
    }

    @PostMapping("/bank-transfer")
    public Mono<PaymentResponse> initiateBankTransfer(@RequestBody BankTransferRequest request) {
        return paymentService.initiateBankTransfer(request);
    }
}

