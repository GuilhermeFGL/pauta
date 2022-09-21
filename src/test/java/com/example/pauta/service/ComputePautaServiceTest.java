package com.example.pauta.service;

import com.example.pauta.repository.PautaRepository;
import com.example.pauta.repository.entity.PautaEntity;
import com.example.pauta.repository.entity.VotoEntity;
import com.example.pauta.repository.entity.enums.PautaResult;
import com.example.pauta.repository.entity.enums.VotoOption;
import com.example.pauta.service.exception.InvalidPautaException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ComputePautaServiceTest {

    @Mock
    private PautaRepository repository;

    @Mock
    private VotoService votoService;

    @Mock
    private MessagePublisher messagePublisher;

    private ComputePautaService service;

    @BeforeEach
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

    @Test
    public void testClosePautaWhenEvenShouldApproveAndCloseAndPublishMessage() {
        Long pautaId = 1L;

        PautaEntity pauta = new PautaEntity();
        pauta.setId(pautaId);

        VotoEntity voto1 = new VotoEntity();
        voto1.setVoto(VotoOption.APPROVE);

        VotoEntity voto2 = new VotoEntity();
        voto2.setVoto(VotoOption.REJECT);

        when(this.repository.findById(eq(pautaId))).thenReturn(Optional.of(pauta));
        when(this.votoService.findVotosByPautaId(eq(pautaId))).thenReturn(Arrays.asList(voto1, voto2));
        when(this.repository.save(any(PautaEntity.class))).thenAnswer(i -> i.getArguments()[0]);

        this.service.closePauta(pautaId);

        verify(this.messagePublisher, times(1))
                .sendMessageToPublishPautaResult(eq(pautaId), eq(PautaResult.APPROVED));
    }

    @Test
    public void testClosePautaWhenNoVotesShouldCloseAsRejectedAndPublishMessage() {
        Long pautaId = 1L;

        PautaEntity pauta = new PautaEntity();
        pauta.setId(pautaId);

        when(this.repository.findById(eq(pautaId))).thenReturn(Optional.of(pauta));
        when(this.votoService.findVotosByPautaId(eq(pautaId))).thenReturn(new ArrayList<>());
        when(this.repository.save(any(PautaEntity.class))).thenAnswer(i -> i.getArguments()[0]);

        this.service.closePauta(pautaId);

        verify(this.messagePublisher, times(1))
                .sendMessageToPublishPautaResult(eq(pautaId), eq(PautaResult.REJECTED));
    }

    @Test
    public void testClosePautaWhenPautaNotExistsShouldThrowException() {
        Long pautaId = 1L;

        when(this.repository.findById(eq(pautaId))).thenReturn(Optional.empty());

        assertThrows(InvalidPautaException.class, () -> this.service.closePauta(pautaId));
    }

}
