package com.example.pauta.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.cloud.aws.messaging.core.SqsMessageHeaders;
import org.springframework.http.MediaType;
import org.springframework.messaging.MessageHeaders;
import org.springframework.stereotype.Service;

import java.util.AbstractMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
public class MessagePublisher {

    public static final int SECONDS_IN_MINUTE = 60;

    @Value("${cloud.aws.end-point.uri}")
    private String endpoint;

    private final QueueMessagingTemplate queueMessagingTemplate;

    @Autowired
    public MessagePublisher(QueueMessagingTemplate queueMessagingTemplate) {
        this.queueMessagingTemplate = queueMessagingTemplate;
    }

    public void sendMessageToClosePauta(Long pautaId, Integer duration) {
        MessagePublisher.log.info("Sending Message to SQS ");

        Map<String, Object> headers = Stream.of(
                        new AbstractMap.SimpleEntry<>(MessageHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE),
                        new AbstractMap.SimpleEntry<>(SqsMessageHeaders.SQS_DELAY_HEADER, duration * SECONDS_IN_MINUTE))
                .collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue));

        this.queueMessagingTemplate
                .convertAndSend(endpoint, pautaId, headers);
    }

}