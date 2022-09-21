package com.example.pauta.service;

import com.example.pauta.controller.dto.PautaRequest;
import com.example.pauta.controller.dto.PautaResponse;
import com.example.pauta.repository.PautaRepository;
import com.example.pauta.repository.entity.PautaEntity;
import com.example.pauta.repository.entity.enums.PautaStatus;
import com.example.pauta.service.exception.InvalidOpenPautaException;
import com.example.pauta.service.exception.InvalidPautaException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
public class PautaService {

    public static final String ERROR_INVALID_DESCRIPTION = "Description must not be empty or null";
    public static final String ERROR_INVALID_DURATION = "Duration in minutes must be bigger than 1";
    private static final Integer DEFAULT_DURATION = 1;


    private final PautaRepository repository;
    private final MessagePublisher messagePublisher;

    @Autowired
    public PautaService(PautaRepository repository,
                        MessagePublisher messagePublisher) {
        this.repository = repository;
        this.messagePublisher = messagePublisher;
    }

    public PautaResponse getPauta(Long id) {
        PautaService.log.info("Get pauta for ID {}", id);

        return this.repository.findById(id)
                .map(this::mapToResponse)
                .orElse(null);
    }

    public PautaResponse getOngoingPauta(Long id) {
        PautaService.log.info("Get ongoing pauta for ID {}", id);

        return this.repository.findByIdAndStatusIsOpenedAndEndIsAfterThanDate(id, LocalDateTime.now())
                .map(this::mapToResponse)
                .orElse(null);
    }

    public PautaResponse createNewPauta(PautaRequest pautaDto) {
        PautaService.log.info("Create new pauta for description {}", pautaDto.getDescription());

        if (pautaDto.getDescription() == null || pautaDto.getDescription().isEmpty()) {
            PautaService.log.error("Invalid pauta for description {}", pautaDto.getDescription());
            throw new InvalidPautaException(PautaService.ERROR_INVALID_DESCRIPTION);
        }

        PautaEntity pautaEntity = new PautaEntity();
        pautaEntity.setDescription(pautaDto.getDescription());
        pautaEntity.setStatus(PautaStatus.CREATED);

        PautaEntity persisted = this.repository.save(pautaEntity);

        return this.mapToResponse(persisted);
    }

    public PautaResponse openPauta(Long id, @Nullable Integer durationInMinutes) {
        PautaService.log.info("Open pauta for ID {} and duration {}", id, durationInMinutes);

        if (durationInMinutes != null && durationInMinutes < PautaService.DEFAULT_DURATION) {
            PautaService.log.error("Invalid pauta for duration {}", durationInMinutes);
            throw new InvalidOpenPautaException(PautaService.ERROR_INVALID_DURATION);
        }

        Optional<PautaEntity> oPersisted = this.repository.findByIdAndStatusIsCreated(id);
        if (!oPersisted.isPresent()) {
            PautaService.log.error("Pauta not found for ID {}", id);
            return null;
        }

        PautaEntity persisted = oPersisted.get();
        persisted.setStatus(PautaStatus.OPENED);

        if (durationInMinutes != null) {
            persisted.setDuration(durationInMinutes);
        } else {
            persisted.setDuration(PautaService.DEFAULT_DURATION);
        }

        LocalDateTime now = LocalDateTime.now();
        persisted.setStart(now);
        persisted.setEnd(now.plusMinutes(persisted.getDuration()));

        persisted = this.repository.save(persisted);

        this.messagePublisher.sendMessageToClosePauta(persisted.getId(), persisted.getDuration());

        return this.mapToResponse(persisted);
    }

    private PautaResponse mapToResponse(PautaEntity entity) {
        PautaResponse response = new PautaResponse();
        response.setId(entity.getId());
        response.setDescription(entity.getDescription());
        response.setDurationInMinutes(entity.getDuration());
        response.setStatus(entity.getStatus());
        response.setResult(entity.getResult());
        response.setStart(entity.getStart());
        response.setEnd(entity.getEnd());
        return response;
    }

}
