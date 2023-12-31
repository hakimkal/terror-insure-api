package com.terron.services.payment;

import com.google.gson.Gson;
import com.terron.dto.BankTransferRequest;
import com.terron.dto.CardPaymentRequest;
import com.terron.dto.FixedVirtualAccountRequest;
import com.terron.dto.PaymentResponse;
import com.terron.dto.VirtualAccountResponse;
import com.terron.models.company.Company;
import com.terron.models.payment.Payment;
import com.terron.repository.company.CompanyRepository;
import com.terron.repository.payment.PaymentRepository;
import com.terron.services.payment.dto.GraphData;
import com.terron.services.payment.dto.PaymentStatSummary;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Base64;
import java.util.UUID;
import javax.naming.ServiceUnavailableException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

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

    public PaymentResponse generateStatsCurrentMonth() {

      PaymentResponse paymentResponse =  new PaymentResponse();
      paymentResponse.setResponseCode("00");
      paymentResponse.setResponseMessage("Payment Summary for 30 Days");

        PaymentStatSummary statisticsData = new PaymentStatSummary(
            3000,
            500000,
            Arrays.asList(
                new GraphData("January", 30000),
                new GraphData("February", 40000),
                new GraphData("March", 600),
                new GraphData("April", 30000),
                new GraphData("May", 30000),
                new GraphData("June", 30000),
                new GraphData("July", 50000),
                new GraphData("August", 30000),
                new GraphData("September", 30000),
                new GraphData("October", 30000),
                new GraphData("November", 30000),
                new GraphData("December", 30000)
            )
        );
      paymentResponse.setData(statisticsData);
      paymentResponse.setSuccess(true);
      return paymentResponse;
    }
}