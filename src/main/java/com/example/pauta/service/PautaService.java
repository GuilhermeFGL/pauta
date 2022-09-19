package com.example.pauta.service;

import com.example.pauta.controller.dto.PautaRequest;
import com.example.pauta.controller.dto.PautaResponse;
import com.example.pauta.repository.PautaRepository;
import com.example.pauta.repository.entity.PautaEntity;
import com.example.pauta.repository.entity.enums.PautaStatus;
import com.example.pauta.service.exception.InvalidOpenPautaException;
import com.example.pauta.service.exception.InvalidPautaException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class PautaService {

    public static final String ERROR_INVALID_DESCRIPTION = "Description must not be empty or null";
    public static final String ERROR_INVALID_DURATION = "Duration in minutes must be bigger than 1";
    private static final Integer DEFAULT_DURATION = 1;

    private final PautaRepository repository;

    @Autowired
    public PautaService(PautaRepository repository) {
        this.repository = repository;
    }

    public PautaResponse getPauta(Long id) {
        return this.repository.findById(id)
                .map(this::mapToResponse)
                .orElse(null);
    }

    public PautaResponse createNewPauta(PautaRequest pautaDto) {
        if (pautaDto.getDescription() == null || pautaDto.getDescription().isEmpty()) {
            throw new InvalidPautaException(PautaService.ERROR_INVALID_DESCRIPTION);
        }

        PautaEntity pautaEntity = new PautaEntity();
        pautaEntity.setDescription(pautaDto.getDescription());
        pautaEntity.setStatus(PautaStatus.CREATED);

        PautaEntity persisted = this.repository.save(pautaEntity);

        return this.mapToResponse(persisted);
    }

    public PautaResponse openPauta(Long id, @Nullable Integer durationInMinutes) {
        if (durationInMinutes != null && durationInMinutes < PautaService.DEFAULT_DURATION) {
            throw new InvalidOpenPautaException(PautaService.ERROR_INVALID_DURATION);
        }

        Optional<PautaEntity> oPersisted = this.repository.findByIdAndStatusIsCreated(id);
        if (!oPersisted.isPresent()) {
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
