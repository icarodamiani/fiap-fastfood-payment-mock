package io.fiap.fastfood.driven.adapter;

import io.fiap.fastfood.driven.core.exception.BadRequestException;
import io.fiap.fastfood.driven.core.exception.BusinessException;
import io.fiap.fastfood.driven.core.exception.NotFoundException;
import io.fiap.fastfood.driven.core.messaging.MessagingPort;
import io.vavr.CheckedFunction1;
import io.vavr.Function1;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.DeleteMessageRequest;
import software.amazon.awssdk.services.sqs.model.GetQueueUrlRequest;
import software.amazon.awssdk.services.sqs.model.GetQueueUrlResponse;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.ReceiptHandleIsInvalidException;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageResponse;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

@Service
public class MessagingAdapter implements MessagingPort {
    private static final Logger LOGGER = LoggerFactory.getLogger(MessagingAdapter.class);

    private final SqsAsyncClient sqsClient;
    private final String numberOfMessages;
    private final String waitTimeMessage;
    private final String visibilityTimeOut;

    public MessagingAdapter(SqsAsyncClient sqsClient,
                            @Value("${aws.sqs.numberOfMessages}") String numberOfMessages,
                            @Value("${aws.sqs.waitTimeMessage}") String waitTimeMessage,
                            @Value("${aws.sqs.visibilityTimeOut}") String visibilityTimeOut) {
        this.sqsClient = sqsClient;
        this.numberOfMessages = numberOfMessages;
        this.waitTimeMessage = waitTimeMessage;
        this.visibilityTimeOut = visibilityTimeOut;
    }

    @Override
    public <T> Flux<Message> read(String queue, Function1<T, Mono<T>> handle, CheckedFunction1<Message, T> readObject) {
        return receive(queue)
            .map(ReceiveMessageResponse::messages)
            .flatMapMany(messages ->
                Flux.fromIterable(messages)
                    .flatMap(message -> Mono.just(readObject.unchecked().apply(message))
                        .flatMap(m -> handle.apply(m)
                            .onErrorResume(t ->
                                    t instanceof NotFoundException
                                        || t instanceof BusinessException
                                        || t instanceof BadRequestException,
                                throwable -> {
                                    LOGGER.error(throwable.getMessage(), throwable);
                                    return Mono.defer(() -> Mono.just(m));
                                }
                            )
                            .flatMap(unused -> ack(queue, message))
                        )
                    )
            );
    }

    private Mono<ReceiveMessageResponse> receive(String queue) {
        return getQueueUrl().apply(queue)
            .map(GetQueueUrlResponse::queueUrl)
            .map(queueUrl -> ReceiveMessageRequest.builder()
                .queueUrl(queueUrl)
                .waitTimeSeconds(Integer.parseInt(waitTimeMessage))
                .maxNumberOfMessages(Integer.parseInt(numberOfMessages))
                .visibilityTimeout(Integer.parseInt(visibilityTimeOut))
                .build()
            ).flatMap(request -> Mono.fromFuture(sqsClient.receiveMessage(request)));
    }

    private Mono<Message> ack(String queue, Message message) {
        return getQueueUrl().apply(queue)
            .flatMap(q -> Mono.fromFuture(
                    sqsClient.deleteMessage(DeleteMessageRequest.builder()
                        .queueUrl(q.queueUrl())
                        .receiptHandle(message.receiptHandle())
                        .build())
                )
            )
            .doOnSuccess(deleteMessageResponse -> LOGGER.info("queue message has been deleted: {}", message.messageId()))
            .onErrorResume(ReceiptHandleIsInvalidException.class, e -> Mono.empty())
            .doOnError(throwable -> LOGGER.error("an error occurred while deleting message.", throwable))
            .map(r -> message);
    }

    @Override
    public <T> Mono<T> send(String queue, T payload, CheckedFunction1<T, String> serialize) {
        return Mono.just(serialize.unchecked().apply(payload))
            .zipWith(getQueueUrl().apply(queue))
            .map(t -> buildMessageRequest().unchecked().apply(t))
            .doOnError(throwable -> LOGGER.error("Failed to prepare message due to error.", throwable))
            .flatMap(message -> Mono.fromFuture(sqsClient.sendMessage(message)))
            .doOnError(throwable -> LOGGER.error("Failed to send message due to error.", throwable))
            .doOnSuccess(response ->
                LOGGER.debug("Message published to queue. Message ID: {} Body: {}", response.messageId(),
                    response.md5OfMessageBody()))
            .map(unused -> payload);
    }

    private CheckedFunction1<Tuple2<String, GetQueueUrlResponse>, SendMessageRequest> buildMessageRequest() {
        return t -> SendMessageRequest.builder()
            .messageBody(t.getT1())
            .queueUrl(t.getT2().queueUrl())
            .build();
    }

    private Function1<String, Mono<GetQueueUrlResponse>> getQueueUrl() {
        return queue -> Mono.fromFuture(sqsClient.getQueueUrl(GetQueueUrlRequest.builder()
                .queueName(queue)
                .build()))
            .doOnError(throwable -> LOGGER.error("Failed to get queueUrl", throwable));
    }
}