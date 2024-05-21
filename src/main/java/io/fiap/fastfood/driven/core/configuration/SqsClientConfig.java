package io.fiap.fastfood.driven.core.configuration;

import java.net.URI;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.auth.credentials.WebIdentityTokenFileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.regions.providers.DefaultAwsRegionProviderChain;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageResponse;


@Configuration
public class SqsClientConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(SqsClientConfig.class);

    @Value("${aws.accessKeyId:}")
    private String accessKeyId;

    @Value("${aws.secretAccessKey:}")
    private String secretAccessKey;

    @Value("${aws.region:}")
    private String region;

    @Value("${aws.sqs.queue:}")
    private String queueName;

    @Value("${aws.sqs.uri:}")
    private String uri;

    @Bean
    public SqsAsyncClient amazonSQSClient() {
        if (StringUtils.isNotEmpty(uri)) {
            LOGGER.info("SqsClientConfig -> amazonSQSClient -> Using local development configuration for SQS.");
            var awsCredentials = AwsBasicCredentials.create(accessKeyId, secretAccessKey);

            return SqsAsyncClient.builder()
                .region(Region.of(region))
                .endpointOverride(URI.create(uri))
                .credentialsProvider(StaticCredentialsProvider.create(awsCredentials))
                .build();
        }
        LOGGER.info("SqsClientConfig -> amazonSQSClient -> Creating client to connect to cloud instance of AWS SQS.");
        return SqsAsyncClient
            .builder()
            .region(DefaultAwsRegionProviderChain.builder().build().getRegion())
            .credentialsProvider(WebIdentityTokenFileCredentialsProvider.create())
            .build();
    }

    @Bean
    public Function<ReceiveMessageRequest, CompletableFuture<ReceiveMessageResponse>> sqsReceiver(SqsAsyncClient client) {
        return client::receiveMessage;
    }
}
