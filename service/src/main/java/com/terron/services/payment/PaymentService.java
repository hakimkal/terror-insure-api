package com.terron.services.payment;

import com.google.gson.Gson;
import com.terron.dto.*;
import com.terron.exceptions.NotFoundException;
import com.terron.models.company.Company;
import com.terron.models.payment.Payment;
import com.terron.repository.company.CompanyRepository;
import com.terron.repository.payment.PaymentRepository;
import io.netty.handler.codec.http.HttpUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
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
import java.util.UUID;

@Service
@Slf4j
@Component
@AllArgsConstructor
public class PaymentService {

    private final WebClient webClient;

    @Autowired
    PaymentRepository paymentRepository;

    @Autowired
    CompanyRepository companyRepository;

    public PaymentResponse makeCardPayment(CardPaymentRequest request, Long companyId) throws ServiceUnavailableException {
        try {
            Company company = companyRepository.findById(companyId).get();
            String token = UUID.randomUUID().toString();
            request.setReference(token);
            request.setCurrency("NGN");
            request.setCustomerEmail(company.getOfficialEmailAddress());
            request.setCustomerName(company.getCompanyName());
            String jsonString = new Gson().toJson(request);
            String chargeData = Base64.getEncoder().encodeToString(jsonString.getBytes(StandardCharsets.UTF_8));

            URI targetUri = UriComponentsBuilder.fromHttpUrl("https://sandbox.payonus.com/pay/api/v1")
                    .path("/pay-with-card")
                    .build().toUri();

            log.info(chargeData);
            PaymentResponse response = webClient.post()
                    .uri(targetUri)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer sk_test_RUHBQBSRSRTKABNQVY0DFC2QDJLX")
                    .body(BodyInserters.fromValue("{\"chargeData\":\"" + chargeData + "\"}"))
                    .retrieve()
                    .bodyToMono(PaymentResponse.class)
                    .block();

            Company insuranceCompany = companyRepository.findById(companyId).get();
            Payment payment = Payment.builder()
                    .reference(request.getReference())
                    .datePaid(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy hh:mm:ss")))
                    .status("pending")
                    .insuranceCompany(insuranceCompany.getCompanyName())
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

    public VirtualAccountResponse createFixedVirtualAccount(FixedVirtualAccountRequest request) throws ServiceUnavailableException {
        try {
            URI targetUri = UriComponentsBuilder.fromHttpUrl("https://sandbox.payonus.com/pay/api/v1")
                    .path("/create-fixed-virtual-account")
                    .build().toUri();

            VirtualAccountResponse response = webClient.post()
                    .uri(targetUri)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer sk_test_RUHBQBSRSRTKABNQVY0DFC2QDJLX")
                    .body(BodyInserters.fromValue(request))
                    .retrieve()
                    .bodyToMono(VirtualAccountResponse.class)
                    .block();

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

