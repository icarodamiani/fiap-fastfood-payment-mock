package io.fiap.fastfood.driven.core.domain.payment.mapper;

import io.fiap.fastfood.driven.core.domain.model.Payment;
import io.fiap.fastfood.driven.core.entity.PaymentEntity;
import io.fiap.fastfood.driver.controller.payment.dto.PaymentDTO;
import io.fiap.fastfood.driver.controller.payment.dto.PaymentStatusDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface PaymentMapper {

    @Mapping(source = "status", target = "status", qualifiedByName = "paymentStatus")
    Payment domainFromDto(PaymentDTO paymentDTO);

    PaymentDTO dtoFromDomain(Payment payment);

    PaymentEntity entityFromDomain(Payment payment);

    Payment domainFromEntity(PaymentEntity paymentEntity);

    @Named("paymentStatus")
    default String paymentStatus(PaymentStatusDTO statusDTO) {
        return statusDTO.name();
    }
}