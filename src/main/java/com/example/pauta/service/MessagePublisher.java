package com.example.pauta.service;

import com.example.pauta.repository.entity.enums.PautaResult;
import com.example.pauta.service.dto.PautaResultMessage;
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

    @Value("${cloud.aws.end-point.close-pauta}")
    private String closePautaEndpoint;

    @Value("${cloud.aws.end-point.publish-pauta}")
    private String publishPautaEndpoint;

    private final QueueMessagingTemplate queueMessagingTemplate;

    @Autowired
    public MessagePublisher(QueueMessagingTemplate queueMessagingTemplate) {
        this.queueMessagingTemplate = queueMessagingTemplate;
    }

    public void sendMessageToClosePauta(Long pautaId, Integer duration) {
        MessagePublisher.log.info("Sending Message to close Pauta {} with delay of {} minutes", pautaId, duration);

        Map<String, Object> headers = Stream.of(
                        new AbstractMap.SimpleEntry<>(MessageHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE),
                        new AbstractMap.SimpleEntry<>(SqsMessageHeaders.SQS_DELAY_HEADER, duration * SECONDS_IN_MINUTE))
                .collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue));

        this.queueMessagingTemplate.convertAndSend(this.closePautaEndpoint, pautaId, headers);
    }

    public void sendMessageToPublishPautaResult(Long pautaId, PautaResult result) {
        MessagePublisher.log.info("Sending Message to publish Pauta {} with result {}", pautaId, result);

        PautaResultMessage pautaResultMessage = new PautaResultMessage();
        pautaResultMessage.setPautaId(pautaId);
        pautaResultMessage.setResult(result.toString());

        this.queueMessagingTemplate.convertAndSend(this.publishPautaEndpoint, pautaResultMessage);
    }
}