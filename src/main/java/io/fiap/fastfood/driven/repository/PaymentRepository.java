package io.fiap.fastfood.driven.repository;

import io.fiap.fastfood.driven.core.entity.PaymentEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface PaymentRepository extends ReactiveCrudRepository<PaymentEntity, String> {

    Flux<PaymentEntity> findByOrderId(String orderId);
}
