package io.fiap.fastfood.driven.core.domain.payment.port.outbound;

import io.fiap.fastfood.driven.core.domain.model.Payment;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.sqs.model.DeleteMessageResponse;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageResponse;

public interface PaymentPort {

    Mono<Payment> createPayment(Payment order);

    Mono<Payment> findPayment(String id);

    Mono<Payment> updatePayment(String id, String operations);

    Mono<ReceiveMessageResponse> receivePayment();

    Mono<DeleteMessageResponse> acknowledgePayment(Message message);
}
