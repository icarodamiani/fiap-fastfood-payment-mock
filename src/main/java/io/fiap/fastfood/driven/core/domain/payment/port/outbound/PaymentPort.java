package io.fiap.fastfood.driven.core.domain.payment.port.outbound;

import io.fiap.fastfood.driven.core.domain.model.Payment;
import io.vavr.Function1;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.sqs.model.Message;

public interface PaymentPort {

    Mono<Payment> createPayment(Payment order);

    Mono<Payment> findPayment(String id);

    Mono<Payment> updatePayment(String id, String operations);

    Flux<Message> readPayment(Function1<Payment, Mono<Payment>> handle);

}
