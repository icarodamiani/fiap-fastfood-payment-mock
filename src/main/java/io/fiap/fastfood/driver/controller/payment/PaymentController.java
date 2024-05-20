package io.fiap.fastfood.driver.controller.payment;

import static org.slf4j.LoggerFactory.getLogger;

import io.fiap.fastfood.driven.core.domain.payment.mapper.PaymentMapper;
import io.fiap.fastfood.driven.core.domain.payment.port.inbound.PaymentUseCase;
import io.fiap.fastfood.driven.core.exception.HttpStatusExceptionConverter;
import io.fiap.fastfood.driver.controller.payment.dto.PaymentDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.slf4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(value = "/v1/payment", produces = MediaType.APPLICATION_JSON_VALUE)
public class PaymentController {
    private static final Logger LOGGER = getLogger(PaymentController.class);
    private final PaymentMapper mapper;
    private final PaymentUseCase paymentUseCase;
    private final HttpStatusExceptionConverter httpStatusExceptionConverter;

    public PaymentController(PaymentMapper mapper,
                             PaymentUseCase paymentUseCase,
                             HttpStatusExceptionConverter httpStatusExceptionConverter) {
        this.mapper = mapper;
        this.paymentUseCase = paymentUseCase;
        this.httpStatusExceptionConverter = httpStatusExceptionConverter;
    }

    @PostMapping("/create")
    @Operation(description = "Create a new Payment")
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Opened"),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content),
        @ApiResponse(responseCode = "404", description = "Not found", content = @Content)
    })
    public Mono<ResponseEntity<PaymentDTO>> create(@RequestBody PaymentDTO payment) {
        return paymentUseCase.createPayment(mapper.domainFromDto(payment))
            .map(mapper::dtoFromDomain)
            .map(ResponseEntity::ok)
            .defaultIfEmpty(ResponseEntity.notFound().build())
            .onErrorMap(e ->
                new ResponseStatusException(httpStatusExceptionConverter.convert(e), e.getMessage(), e))
            .doOnError(throwable -> LOGGER.error(throwable.getMessage(), throwable));
    }
}
