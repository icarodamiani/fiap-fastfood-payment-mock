package io.fiap.fastfood.driven.core.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.config.CorsRegistry;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.reactive.config.WebFluxConfigurer;

@Configuration
@EnableWebFlux
public class CorsConfiguration implements WebFluxConfigurer {

    @Value("${spring.cors.allowed-origins}")
    public String[] allowsOrigins;

    @Value("${spring.cors.allowed-headers}")
    public String[] allowsHeaders;

    @Value("${spring.cors.allowed-methods}")
    public String[] allowsMethods;

    @Value("${spring.cors.max-age}")
    public Integer maxAge;

    @Override
    public void addCorsMappings(CorsRegistry corsRegistry) {
        corsRegistry.addMapping("/**")
                .allowedOriginPatterns(allowsOrigins)
                .allowedMethods(allowsMethods)
                .allowedHeaders(allowsHeaders)
                .allowCredentials(true)
                .maxAge(maxAge);
    }
}
