package com.example.pauta.service;

import com.example.pauta.repository.entity.enums.PautaResult;
import com.example.pauta.service.dto.PautaResultMessage;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class MessagePublisherTest {

    @Mock
    private QueueMessagingTemplate queueMessagingTemplate;

    private MessagePublisher service;

    @Before
    public void setUp() {
        this.service = new MessagePublisher(this.queueMessagingTemplate);
        ReflectionTestUtils.setField(service, "closePautaEndpoint", "http://closePautaEndpoint");
        ReflectionTestUtils.setField(service, "publishPautaEndpoint", "http://publishPautaEndpoint");
    }

    @Test
    public void testSendMessageToClosePautaShouldSendMessage() {
        this.service.sendMessageToClosePauta(1L, 10);

        verify(this.queueMessagingTemplate, times(1))
                .convertAndSend(eq("http://closePautaEndpoint"), eq(1L), any(Map.class));
    }

    @Test
    public void sendMessageToPublishPautaResultShouldSendMessage() {
        this.service.sendMessageToPublishPautaResult(1L, PautaResult.APPROVED);

        verify(this.queueMessagingTemplate, times(1))
                .convertAndSend(eq("http://publishPautaEndpoint"), any(PautaResultMessage.class));
    }

}