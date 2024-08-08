package io.fiap.fastfood.driven.core.domain.payment.port.inbound;

import io.fiap.fastfood.driven.core.domain.model.Payment;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.sqs.model.DeleteMessageResponse;
import software.amazon.awssdk.services.sqs.model.Message;

public interface PaymentUseCase {
    Mono<Payment> createPayment(Payment payment);

    Flux<Message> handleEvent();
}
