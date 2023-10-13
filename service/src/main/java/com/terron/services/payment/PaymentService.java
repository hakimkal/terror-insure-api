package com.terron.services.payment;

import com.google.gson.Gson;
import com.terron.dto.BankTransferRequest;
import com.terron.dto.CardPaymentRequest;
import com.terron.dto.ChargeData;
import com.terron.dto.PaymentResponse;
import com.terron.exceptions.NotFoundException;
import com.terron.models.payment.Payment;
import com.terron.repository.payment.PaymentRepository;
import io.netty.handler.codec.http.HttpUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import javax.naming.ServiceUnavailableException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;

@Service
@Slf4j
@Component
@AllArgsConstructor
public class PaymentService {

    private final WebClient webClient;

    @Autowired
    PaymentRepository paymentRepository;

    public PaymentResponse makeCardPayment(CardPaymentRequest request, Long companyId) throws ServiceUnavailableException {
        try {

            String jsonString = new Gson().toJson(request);
            String chargeData = Base64.getEncoder().encodeToString(jsonString.getBytes(StandardCharsets.UTF_8));

            URI targetUri = UriComponentsBuilder.fromHttpUrl("https://sandbox.payonus.com/pay/api/v1")
                    .path("/pay-with-card")
                    .build().toUri();

            log.info(chargeData);
            PaymentResponse response = webClient.post()
                    .uri(targetUri)
                    .contentType(MediaType.APPLICATION_JSON) // Set the Content-Type header
                    .header("Authorization", "Bearer sk_test_RUHBQBSRSRTKABNQVY0DFC2QDJLX")
                    .body(BodyInserters.fromValue("{\"chargeData\":\"" + chargeData + "\"}"))
                    .retrieve()
                    .bodyToMono(PaymentResponse.class)
                    .block();

            Payment payment = Payment.builder()
                    .reference(request.getReference())
                    .datePaid(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy hh:mm:ss")))
                    .status("pending")
                    .amountPaid(request.getAmount())
                    .companyId(companyId)
                    .build();
            paymentRepository.save(payment);
            return response;
        } catch (WebClientRequestException ex) {
            log.error(ex.getMessage());
            log.info("Got here");
            log.info("Error -> {}", ex.getLocalizedMessage());
            throw new ServiceUnavailableException(ex.getMessage());

        }
    }


    public Mono<PaymentResponse> initiateBankTransfer(BankTransferRequest request) {
        return webClient
                .post()
                .uri("/bank-transfer")
                .body(BodyInserters.fromValue(request))
                .retrieve()
                .bodyToMono(PaymentResponse.class);
    }
}

