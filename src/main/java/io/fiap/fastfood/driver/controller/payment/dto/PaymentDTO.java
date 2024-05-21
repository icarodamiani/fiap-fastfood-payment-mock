package io.fiap.fastfood.driver.controller.payment.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PaymentDTO(
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    String id,
    String method,
    BigDecimal amount,
    @Schema(example = "2023-10-21T09:00:44.495Z")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.yyyy'Z'")
    LocalDateTime dateTime,
    String orderId,
    String webhook,
    PaymentStatusDTO status) {

    public static final class PaymentBuilder {
        private String id;
        private String method;
        private BigDecimal amount;
        private LocalDateTime dateTime;
        private String orderId;
        private String webhook;
        private PaymentStatusDTO status;
        private PaymentBuilder() {
        }

        public static PaymentBuilder builder() {
            return new PaymentBuilder();
        }

        public static PaymentBuilder from(PaymentDTO payment) {
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

        public PaymentBuilder withStatus(PaymentStatusDTO status) {
            this.status = status;
            return this;
        }

        public PaymentDTO build() {
            return new PaymentDTO(id, method, amount, dateTime, orderId, webhook, status);
        }
    }
}
