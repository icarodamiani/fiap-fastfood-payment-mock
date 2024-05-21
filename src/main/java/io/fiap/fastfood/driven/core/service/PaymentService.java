package io.fiap.fastfood.driven.core.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.fiap.fastfood.driven.core.domain.model.Payment;
import io.fiap.fastfood.driven.core.domain.payment.port.inbound.PaymentUseCase;
import io.fiap.fastfood.driven.core.domain.payment.port.outbound.PaymentPort;
import io.fiap.fastfood.driven.core.exception.BadRequestException;
import io.fiap.fastfood.driven.core.exception.BusinessException;
import io.fiap.fastfood.driven.core.exception.NotFoundException;
import io.vavr.CheckedFunction1;
import java.net.URI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.sqs.model.DeleteMessageResponse;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageResponse;

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
    public Flux<DeleteMessageResponse> receiveAndHandlePaymentStatus() {
        return paymentPort.receivePayment()
            .map(ReceiveMessageResponse::messages)
            .flatMapMany(messages ->
                Flux.fromIterable(messages)
                    .flatMap(m -> Mono.just(readEvent().unchecked().apply(m))
                        .flatMap(this::processAndNotify)
                        .map(__ -> m)
                        .onErrorResume(t ->
                                t instanceof NotFoundException
                                    || t instanceof BusinessException
                                    || t instanceof BadRequestException,
                            throwable -> {
                                LOGGER.error(throwable.getMessage(), throwable);
                                return Mono.just(m);
                            }
                        )
                        .flatMap(paymentPort::acknowledgePayment)
                    )
            );
    }

    public Mono<Payment> processAndNotify(Payment payment) {
        return paymentPort.updatePayment(payment.id(),
                "[{\"op\": \"replace\",\"path\": \"/status\",\"value\": \"PAID\"}]")
            .flatMap(this::notify)
            .map(__ -> payment);
    }

    private CheckedFunction1<Message, Payment> readEvent() {
        return message -> mapper.readValue(message.body(), Payment.class);
    }

    private Mono<ResponseEntity<Void>> notify(Payment payment) {
        return webClient.post()
            .uri(URI.create(payment.webhook()))
            .body(BodyInserters.fromValue(payment))
            .retrieve()
            .toBodilessEntity();
    }
}
