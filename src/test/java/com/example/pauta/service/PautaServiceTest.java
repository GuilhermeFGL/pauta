package com.example.pauta.service;

import com.example.pauta.controller.dto.PautaRequest;
import com.example.pauta.controller.dto.PautaResponse;
import com.example.pauta.repository.PautaRepository;
import com.example.pauta.repository.entity.PautaEntity;
import com.example.pauta.repository.entity.enums.PautaStatus;
import com.example.pauta.service.exception.InvalidOpenPautaException;
import com.example.pauta.service.exception.InvalidPautaException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PautaServiceTest {

    @Mock
    private PautaRepository repository;

    @Mock
    private MessagePublisher messagePublisher;

    private PautaService service;

    @BeforeEach
    public void setUp() {
        this.service = new PautaService(this.repository, this.messagePublisher);
    }

    @Test
    public void testGetPautaShouldReturnPautaWhenExists() {
        Long id = 1L;

        PautaEntity pauta = new PautaEntity();
        pauta.setId(id);

        when(this.repository.findById(eq(id))).thenReturn(Optional.of(pauta));

        PautaResponse result = this.service.getPauta(id);

        assertNotNull(result);
        assertEquals(id, result.getId());
    }

    @Test
    public void testGetPautaShouldReturnNullWhenNotExists() {
        Long id = 1L;

        when(this.repository.findById(eq(id))).thenReturn(Optional.empty());

        PautaResponse result = this.service.getPauta(id);

        assertNull(result);
    }

    @Test
    public void getOnGoingPautaShouldReturnPautaWhenExists() {
        Long id = 1L;

        PautaEntity pauta = new PautaEntity();
        pauta.setId(id);

        when(this.repository.findByIdAndStatusIsOpenedAndEndIsAfterThanDate(eq(id), any(LocalDateTime.class)))
                .thenReturn(Optional.of(pauta));

        PautaResponse result = this.service.getOnGoingPauta(id);

        assertNotNull(result);
        assertEquals(id, result.getId());
    }

    @Test
    public void getOnGoingPautaShouldReturnNullWhenNotExists() {
        Long id = 1L;

        PautaEntity pauta = new PautaEntity();
        pauta.setId(id);

        when(this.repository.findByIdAndStatusIsOpenedAndEndIsAfterThanDate(eq(id), any(LocalDateTime.class)))
                .thenReturn(Optional.empty());

        PautaResponse result = this.service.getOnGoingPauta(id);

        assertNull(result);
    }

    @Test
    public void testCreateNewPautaShouldCreatePautaWhenValid() {
        Long id = 1L;

        PautaEntity pauta = new PautaEntity();
        pauta.setId(id);

        PautaRequest pautaDto = new PautaRequest();
        pautaDto.setDescription("description");

        when(this.repository.save(any(PautaEntity.class))).thenAnswer(i -> i.getArguments()[0]);

        PautaResponse result = this.service.createNewPauta(pautaDto);

        assertNotNull(result);
        assertEquals(PautaStatus.CREATED, result.getStatus());
    }

    @Test
    public void testCreateNewPautaShouldNotCreatePautaWhenInvalid() {
        PautaRequest pautaDto = new PautaRequest();
        pautaDto.setDescription("description");

        when(this.repository.save(any(PautaEntity.class))).thenThrow(new InvalidPautaException("message"));

        assertThrows(InvalidPautaException.class, () -> this.service.createNewPauta(pautaDto));
    }

    @Test
    public void testCreateNewPautaShouldNotCreatePautaWhenInvalidDescriptionNull() {
        PautaRequest pautaDto = new PautaRequest();

        assertThrows(InvalidPautaException.class, () -> this.service.createNewPauta(pautaDto));
    }

    @Test
    public void testCreateNewPautaShouldNotCreatePautaWhenInvalidDescriptionEmpty() {
        PautaRequest pautaDto = new PautaRequest();
        pautaDto.setDescription("");

        assertThrows(InvalidPautaException.class, () -> this.service.createNewPauta(pautaDto));
    }

    @Test
    public void testOpenPautaShouldOpenPautaWhenValid() {
        Long id = 1L;
        Integer duration = 10;

        PautaEntity pauta = new PautaEntity();
        pauta.setId(id);
        pauta.setDuration(duration);

        when(this.repository.findByIdAndStatusIsCreated(eq(id))).thenReturn(Optional.of(pauta));
        when(this.repository.save(any(PautaEntity.class))).thenAnswer(i -> i.getArguments()[0]);

        PautaResponse result = this.service.openPauta(id, duration);

        assertNotNull(result);
        assertEquals(id, result.getId());
        assertEquals(duration, result.getDurationInMinutes());
        assertEquals(PautaStatus.OPENED, result.getStatus());
        assertNotNull(result.getStart());
        assertNotNull(result.getEnd());
        verify(this.messagePublisher, times(1)).sendMessageToClosePauta(eq(id), eq(duration));
    }

    @Test
    public void testOpenPautaShouldOpenPautaWhenValidWithDefaultDuration() {
        Long id = 1L;
        Integer duration = 1;

        PautaEntity pauta = new PautaEntity();
        pauta.setId(id);
        pauta.setDuration(duration);

        when(this.repository.findByIdAndStatusIsCreated(eq(id))).thenReturn(Optional.of(pauta));
        when(this.repository.save(any(PautaEntity.class))).thenAnswer(i -> i.getArguments()[0]);

        PautaResponse result = this.service.openPauta(id, null);

        assertNotNull(result);
        assertEquals(id, result.getId());
        assertEquals(duration, result.getDurationInMinutes());
        assertEquals(PautaStatus.OPENED, result.getStatus());
        assertNotNull(result.getStart());
        assertNotNull(result.getEnd());
        verify(this.messagePublisher, times(1)).sendMessageToClosePauta(eq(id), eq(duration));
    }

    @Test
    public void testOpenPautaShouldNotOpenPautaWhenInvalidDuration() {
        Long id = 1L;
        Integer duration = -1;

        assertThrows(InvalidOpenPautaException.class, () -> this.service.openPauta(id, duration));
    }

    @Test
    public void testOpenPautaShouldOpenPautaWhenPautaNotFound() {
        Long id = 1L;
        Integer duration = 1;

        PautaEntity pauta = new PautaEntity();
        pauta.setId(id);
        pauta.setDuration(duration);

        when(this.repository.findByIdAndStatusIsCreated(eq(id))).thenReturn(Optional.empty());

        PautaResponse result = this.service.openPauta(id, null);

        assertNull(result);
    }

}