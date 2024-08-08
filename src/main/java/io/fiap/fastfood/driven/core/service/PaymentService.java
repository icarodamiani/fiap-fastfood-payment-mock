package io.fiap.fastfood.driven.core.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.fiap.fastfood.driven.core.domain.model.Payment;
import io.fiap.fastfood.driven.core.domain.payment.port.inbound.PaymentUseCase;
import io.fiap.fastfood.driven.core.domain.payment.port.outbound.PaymentPort;
import io.vavr.Function1;
import java.net.URI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.sqs.model.Message;

@Service
public class PaymentService implements PaymentUseCase {

    private static final Logger LOGGER = LoggerFactory.getLogger(PaymentService.class);


    private final PaymentPort paymentPort;
    private final ObjectMapper mapper;
    private final WebClient webClient;

    public PaymentService(PaymentPort paymentPort, ObjectMapper mapper, WebClient webClient) {
        this.paymentPort = paymentPort;
        this.mapper = mapper;
        this.webClient = webClient;
    }

    @Override
    public Mono<Payment> createPayment(Payment payment) {
        return paymentPort.createPayment(payment);
    }

    @Override
    public Flux<Message> handleEvent() {
        return paymentPort.readPayment(handle());
    }

    private Function1<Payment, Mono<Payment>> handle() {
        return payment -> paymentPort.updatePayment(payment.id(),
                "[{\"op\": \"replace\",\"path\": \"/status\",\"value\": \"PAID\"}]")
            .flatMap(this::notify)
            .map(unused -> payment);
    }

    private Mono<ResponseEntity<Void>> notify(Payment payment) {
        return webClient.post()
            .uri(URI.create(payment.webhook()))
            .body(BodyInserters.fromValue(Payment.PaymentBuilder.from(payment).withStatus("PAID").build()))
            .retrieve()
            .toBodilessEntity();
    }
}
