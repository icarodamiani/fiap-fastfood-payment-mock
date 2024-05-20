package io.fiap.fastfood.driven.core.domain.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;


public record Payment(
    String id,
    String method,
    BigDecimal amount,
    LocalDateTime dateTime,
    String orderId,
    String webhook,
    String status) implements Serializable {


    public static final class PaymentBuilder {
        private String id;
        private String method;
        private BigDecimal amount;
        private LocalDateTime dateTime;
        private String orderId;
        private String webhook;
        private String status;

        private PaymentBuilder() {
        }

        public static PaymentBuilder builder() {
            return new PaymentBuilder();
        }

        public static PaymentBuilder from(Payment payment) {
            return PaymentBuilder.builder()
                .withId(payment.id)
                .withOrderId(payment.orderId)
                .withDateTime(payment.dateTime)
                .withMethod(payment.method)
                .withAmount(payment.amount)
                .withWebhook(payment.webhook)
                .withStatus(payment.status);
        }

        public PaymentBuilder withId(String id) {
            this.id = id;
            return this;
        }

        public PaymentBuilder withMethod(String method) {
            this.method = method;
            return this;
        }

        public PaymentBuilder withAmount(BigDecimal amount) {
            this.amount = amount;
            return this;
        }

        public PaymentBuilder withDateTime(LocalDateTime date) {
            this.dateTime = date;
            return this;
        }

        public PaymentBuilder withOrderId(String orderId) {
            this.orderId = orderId;
            return this;
        }
        public PaymentBuilder withWebhook(String webhook) {
            this.webhook = webhook;
            return this;
        }

        public PaymentBuilder withStatus(String status) {
            this.status = status;
            return this;
        }

        public Payment build() {
            return new Payment(id, method, amount, dateTime, orderId, webhook, status);
        }
    }
}
