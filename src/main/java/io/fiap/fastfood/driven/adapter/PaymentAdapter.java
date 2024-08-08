package io.fiap.fastfood.driven.adapter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import io.fiap.fastfood.driven.core.domain.model.Payment;
import io.fiap.fastfood.driven.core.domain.payment.mapper.PaymentMapper;
import io.fiap.fastfood.driven.core.domain.payment.port.outbound.PaymentPort;
import io.fiap.fastfood.driven.core.entity.PaymentEntity;
import io.fiap.fastfood.driven.core.exception.BadRequestException;
import io.fiap.fastfood.driven.core.exception.DuplicatedKeyException;
import io.fiap.fastfood.driven.core.messaging.MessagingPort;
import io.fiap.fastfood.driven.repository.PaymentRepository;
import io.vavr.CheckedFunction1;
import io.vavr.CheckedFunction2;
import io.vavr.Function1;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.GetQueueUrlRequest;
import software.amazon.awssdk.services.sqs.model.GetQueueUrlResponse;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

@Component
public class PaymentAdapter implements PaymentPort {

    private static final Logger LOGGER = LoggerFactory.getLogger(PaymentAdapter.class);

    private final MessagingPort messagingPort;
    private final PaymentRepository repository;
    private final PaymentMapper mapper;
    private final ObjectMapper objectMapper;
    private final SqsAsyncClient sqsClient;

    private final String queue;

    private final String numberOfMessages;
    private final String waitTimeMessage;
    private final String visibilityTimeOut;

    public PaymentAdapter(MessagingPort messagingPort, SqsAsyncClient sqsClient,
                          PaymentRepository repository,
                          PaymentMapper mapper,
                          ObjectMapper objectMapper,
                          @Value("${payment.sqs.queue}") String paymentQueue,
                          @Value("${aws.sqs.numberOfMessages}") String numberOfMessages,
                          @Value("${aws.sqs.waitTimeMessage}") String waitTimeMessage,
                          @Value("${aws.sqs.visibilityTimeOut}") String visibilityTimeOut) {
        this.messagingPort = messagingPort;
        this.sqsClient = sqsClient;
        this.numberOfMessages = numberOfMessages;
        this.waitTimeMessage = waitTimeMessage;
        this.visibilityTimeOut = visibilityTimeOut;
        this.repository = repository;
        this.mapper = mapper;
        this.objectMapper = objectMapper;
        this.queue = paymentQueue;
    }

    @Override
    public Mono<Payment> createPayment(Payment payment) {
        return repository.findByOrderNumber(payment.orderNumber())
            .next()
            .flatMap(c -> Mono.defer(() -> Mono.<PaymentEntity>error(DuplicatedKeyException::new)))
            .switchIfEmpty(Mono.defer(() -> repository.save(
                mapper.entityFromDomain(
                    Payment.PaymentBuilder.from(payment).withId(null).build()
                )
            )))
            .map(mapper::domainFromEntity)
            .flatMap(this::sendPaymentToAsyncProcessing);
    }

    @Override
    public Mono<Payment> findPayment(String id) {
        return repository.findById(id)
            .map(mapper::domainFromEntity)
            .onErrorMap(JsonPatchException.class::isInstance, BadRequestException::new);
    }

    @Override
    public Mono<Payment> updatePayment(String id, String operations) {
        return repository.findById(id)
            .map(payment -> applyPatch().unchecked().apply(payment, operations))
            .flatMap(repository::save)
            .map(mapper::domainFromEntity)
            .onErrorMap(JsonPatchException.class::isInstance, BadRequestException::new);
    }

    private CheckedFunction2<PaymentEntity, String, PaymentEntity> applyPatch() {
        return (payment, operations) -> {
            var patch = readOperations()
                .unchecked()
                .apply(operations);

            var patched = patch.apply(objectMapper.convertValue(payment, JsonNode.class));

            return objectMapper.treeToValue(patched, PaymentEntity.class);
        };
    }

    private CheckedFunction1<String, JsonPatch> readOperations() {
        return operations -> {
            final InputStream in = new ByteArrayInputStream(operations.getBytes());
            return objectMapper.readValue(in, JsonPatch.class);
        };
    }

    @Override
    public Flux<Message> readPayment(Function1<Payment, Mono<Payment>> handle) {
        return messagingPort.read(queue, handle, readEvent());
    }

    private CheckedFunction1<Message, Payment> readEvent() {
        return message -> objectMapper.readValue(message.body(), Payment.class);
    }


    public Mono<Payment> sendPaymentToAsyncProcessing(Payment payment) {
        return messagingPort.send(queue, payment, serializePayload());
    }

    private <T> CheckedFunction1<T, String> serializePayload() {
        return objectMapper::writeValueAsString;
    }

    private Function1<String, Mono<GetQueueUrlResponse>> getQueueUrl() {
        return queue -> Mono.fromFuture(sqsClient.getQueueUrl(GetQueueUrlRequest.builder()
                .queueName(queue)
                .build()))
            .doOnError(throwable -> LOGGER.error("Failed to get queueUrl", throwable));
    }

    private CheckedFunction1<Tuple2<String, GetQueueUrlResponse>, SendMessageRequest> buildMessageRequest() {
        return t -> SendMessageRequest.builder()
            .messageBody(t.getT1())
            .queueUrl(t.getT2().queueUrl())
            .build();
    }

}
