package com.example.pauta.service;

import com.example.pauta.repository.PautaRepository;
import com.example.pauta.repository.entity.PautaEntity;
import com.example.pauta.repository.entity.VotoEntity;
import com.example.pauta.repository.entity.enums.PautaResult;
import com.example.pauta.repository.entity.enums.VotoOption;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ComputePautaServiceTest {

    @Mock
    private PautaRepository repository;

    @Mock
    private VotoService votoService;

    @Mock
    private MessagePublisher messagePublisher;

    private ComputePautaService service;

    @Before
    public void setUp() {
        this.service = new ComputePautaService(repository, votoService, messagePublisher);
        ReflectionTestUtils.setField(service, "pautaApprovalScore", 0.5);
    }

    @Test
    public void testClosePautaWhenRejectedShouldCloseAndPublishMessage() {
        Long pautaId = 1L;

        PautaEntity pauta = new PautaEntity();
        pauta.setId(pautaId);

        VotoEntity voto = new VotoEntity();
        voto.setVoto(VotoOption.REJECT);

        when(this.repository.findById(eq(pautaId))).thenReturn(Optional.of(pauta));
        when(this.votoService.findVotosByPautaId(eq(pautaId))).thenReturn(Collections.singletonList(voto));
        when(this.repository.save(any(PautaEntity.class))).thenAnswer(i -> i.getArguments()[0]);

        this.service.closePauta(pautaId);

        verify(this.messagePublisher, times(1))
                .sendMessageToPublishPautaResult(eq(pautaId), eq(PautaResult.REJECTED));
    }

    @Test
    public void testClosePautaWhenApprovedShouldCloseAndPublishMessage() {
        Long pautaId = 1L;

        PautaEntity pauta = new PautaEntity();
        pauta.setId(pautaId);

        VotoEntity voto = new VotoEntity();
        voto.setVoto(VotoOption.APPROVE);

        when(this.repository.findById(eq(pautaId))).thenReturn(Optional.of(pauta));
        when(this.votoService.findVotosByPautaId(eq(pautaId))).thenReturn(Collections.singletonList(voto));
        when(this.repository.save(any(PautaEntity.class))).thenAnswer(i -> i.getArguments()[0]);

        this.service.closePauta(pautaId);

        verify(this.messagePublisher, times(1))
                .sendMessageToPublishPautaResult(eq(pautaId), eq(PautaResult.APPROVED));
    }

}