package io.fiap.fastfood.driver.messaging;

import io.fiap.fastfood.driven.core.service.PaymentService;
import java.time.Duration;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.scheduling.support.PeriodicTrigger;
import org.springframework.scheduling.support.SimpleTriggerContext;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;


@Component
public class PaymentEventListener implements CommandLineRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(PaymentEventListener.class);

    private final SimpleTriggerContext triggerContext;
    private final PeriodicTrigger trigger;
    private final Scheduler boundedElastic;
    private final PaymentService service;

    public PaymentEventListener(@Value("${application.consumer.delay:10000}")
                                String delay,
                                @Value("${application.consumer.poolSize:1}")
                                String poolSize,
                                PaymentService service) {
        this.service = service;
        boundedElastic = Schedulers.newBoundedElastic(Integer.parseInt(poolSize), 10000,
            "paymentMockListenerPool", 600, true);

        this.triggerContext = new SimpleTriggerContext();
        this.trigger = new PeriodicTrigger(Long.parseLong(delay), TimeUnit.MILLISECONDS);

    }

    @Override
    public void run(String... args) {
        Flux.<Duration>generate(sink -> {
                Date date = this.trigger.nextExecutionTime(triggerContext);
                if (date != null) {
                    triggerContext.update(date, null, null);
                    long millis = date.getTime() - System.currentTimeMillis();
                    sink.next(Duration.ofMillis(millis));
                } else {
                    sink.complete();
                }
            })
            .concatMap(duration -> Mono.delay(duration)
                    .doOnNext(l ->
                        triggerContext.update(
                            Objects.requireNonNull(triggerContext.lastScheduledExecutionTime()),
                            new Date(),
                            null))
                    .flatMapMany(__ -> service.receiveAndHandlePaymentStatus())
                    .doOnComplete(() -> triggerContext.update(
                        Objects.requireNonNull(triggerContext.lastScheduledExecutionTime()),
                        Objects.requireNonNull(triggerContext.lastActualExecutionTime()),
                        new Date())
                    )
                    .doOnError(error -> LOGGER.error("an error occurred during message listener: " + error.getMessage(), error))
                , 0)
            .map(__ -> "")
            .onErrorResume(throwable -> Flux.just(""))
            .repeat()
            .subscribeOn(boundedElastic)
            .subscribe();
    }
}
