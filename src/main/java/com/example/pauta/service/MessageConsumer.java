package com.example.pauta.service;

import com.example.pauta.service.dto.PautaResultMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.aws.messaging.listener.SqsMessageDeletionPolicy;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class MessageConsumer {

    private final ComputePautaService computePautaService;

    @Autowired
    public MessageConsumer(ComputePautaService computePautaService) {
        this.computePautaService = computePautaService;
    }

    @SqsListener(value = "pauta-queue", deletionPolicy = SqsMessageDeletionPolicy.ON_SUCCESS)
    public void publishClosePautaMessage(Long pautaId) {
        MessageConsumer.log.info("Message from SQS pauta-queue. Pauta ID: {}", pautaId);

        computePautaService.closePauta(pautaId);
    }

    @SqsListener(value = "publish-queue", deletionPolicy = SqsMessageDeletionPolicy.NEVER)
    public void publishPautaResultMessage(PautaResultMessage result) {
        MessageConsumer.log.info("Message from SQS publish-queue. Pauta ID: {}, Result: {}",
                result.getPautaId(), result.getResult());
    }

}