package com.example.pauta.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class MessageConsumerTest {

    @Mock
    private ComputePautaService computePautaService;

    private MessageConsumer service;

    @Before
    public void setUp() {
        this.service = new MessageConsumer(this.computePautaService);
    }

    @Test
    public void testPublishClosePautaMessageShouldReceiveMessage() {
        this.service.publishClosePautaMessage(1L);

        verify(this.computePautaService, times(1)).closePauta(eq(1L));
    }

}