package com.terron.controller.controllers;

import com.terron.dto.BankTransferRequest;
import com.terron.dto.CardPaymentRequest;
import com.terron.dto.PaymentResponse;
import com.terron.services.payment.PaymentService;
import javax.naming.ServiceUnavailableException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/v1/payment")
public class PaymentController {

  private final PaymentService paymentService;


  @Autowired
  public PaymentController(PaymentService paymentService) {
    this.paymentService = paymentService;
  }


  @GetMapping("/stats")
  public PaymentResponse paymentMonthStats() {
    return paymentService.generateStatsCurrentMonth();

  }

  @GetMapping("/stats/hotel/{hotelId}")
  public PaymentResponse paymentMonthStatsForHotel(@PathVariable String hotelId) {
    return paymentService.generateStatsCurrentMonth();

  }
  @GetMapping("/stats/insurance/{insuranceId}")
  public PaymentResponse paymentMonthStatsForInsurance(@PathVariable String insuranceId) {
    return paymentService.generateStatsCurrentMonth();

  }


  @PostMapping("/card-payment/{companyId}")
  public PaymentResponse makeCardPayment(@RequestBody CardPaymentRequest request,
      @PathVariable Long companyId) throws ServiceUnavailableException {
    return paymentService.makeCardPayment(request, companyId);
  }

  @PostMapping("/bank-transfer")
  public Mono<PaymentResponse> initiateBankTransfer(@RequestBody BankTransferRequest request) {
    return paymentService.initiateBankTransfer(request);
  }
}