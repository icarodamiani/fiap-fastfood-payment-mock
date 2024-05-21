package io.fiap.fastfood.driven.core.configuration;

import io.netty.channel.ChannelOption;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import java.time.Duration;
import java.util.concurrent.TimeUnit;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;

@Configuration
public class WebClientConfiguration {

    @Bean
    public WebClient getWebClient() {
        HttpClient httpClient = HttpClient.create(ConnectionProvider.create("client", 5))
            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 50000)
            .responseTimeout(Duration.ofMillis(50000))
            .doOnConnected(conn ->
                conn.addHandlerLast(new ReadTimeoutHandler(50000, TimeUnit.MILLISECONDS))
                    .addHandlerLast(new WriteTimeoutHandler(50000, TimeUnit.MILLISECONDS)));

        return WebClient.builder()
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .clientConnector(new ReactorClientHttpConnector(httpClient))
            .build();
    }

}
