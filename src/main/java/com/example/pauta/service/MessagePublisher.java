package com.example.pauta.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class MessagePublisher {

    @Value("${cloud.aws.end-point.uri}")
    private String endpoint;

    private final QueueMessagingTemplate queueMessagingTemplate;

    @Autowired
    public MessagePublisher(QueueMessagingTemplate queueMessagingTemplate) {
        this.queueMessagingTemplate = queueMessagingTemplate;
    }

    public void sendMessageToClosePauta(Long pautaId, Integer duration) {
        MessagePublisher.log.info("Sending Message to SQS ");
        this.queueMessagingTemplate.convertAndSend(endpoint, pautaId);
    }

}