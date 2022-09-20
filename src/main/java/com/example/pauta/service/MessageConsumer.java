package com.example.pauta.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.aws.messaging.listener.SqsMessageDeletionPolicy;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class MessageConsumer {

    @SqsListener(value = "pauta-queue", deletionPolicy = SqsMessageDeletionPolicy.ON_SUCCESS)
    public void processMessage(Long pautaId) {
        MessageConsumer.log.info("Message from SQS {}", pautaId);
    }

}