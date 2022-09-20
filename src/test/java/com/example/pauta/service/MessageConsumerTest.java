package com.example.pauta.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class MessageConsumerTest {

    @Mock
    private ComputePautaService computePautaService;

    private MessageConsumer service;

    @BeforeEach
    public void setUp() {
        this.service = new MessageConsumer(this.computePautaService);
    }

    @Test
    public void testPublishClosePautaMessageShouldReceiveMessage() {
        this.service.publishClosePautaMessage(1L);

        verify(this.computePautaService, times(1)).closePauta(eq(1L));
    }

}