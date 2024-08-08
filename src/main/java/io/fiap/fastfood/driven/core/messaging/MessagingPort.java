package io.fiap.fastfood.driven.core.messaging;

import io.vavr.CheckedFunction1;
import io.vavr.Function1;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.sqs.model.Message;

public interface MessagingPort {

    <T> Flux<Message> read(String queue, Function1<T, Mono<T>> handle, CheckedFunction1<Message, T> readObject);

    <T> Mono<T> send(String queue, T payload, CheckedFunction1<T, String> serialize);
}
