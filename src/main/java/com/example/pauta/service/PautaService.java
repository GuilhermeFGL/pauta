package com.example.pauta.service;

import com.example.pauta.controller.dto.PautaRequest;
import com.example.pauta.controller.dto.PautaResponse;
import com.example.pauta.repository.PautaRepository;
import com.example.pauta.repository.entity.PautaEntity;
import com.example.pauta.repository.entity.enums.PautaStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class PautaService {

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
        PautaEntity pautaEntity = new PautaEntity();
        pautaEntity.setDescription(pautaDto.getDescription());
        pautaEntity.setDuration(pautaDto.getDurationInMinutes());
        pautaEntity.setStatus(PautaStatus.CREATED);

        PautaEntity persisted = this.repository.save(pautaEntity);

        return this.mapToResponse(persisted);
    }

    public PautaResponse openPauta(Long id) {
        Optional<PautaEntity> oPersisted = this.repository.findById(id);
        if (!oPersisted.isPresent()) {
            return null;
        }

        LocalDateTime now = LocalDateTime.now();
        PautaEntity persisted = oPersisted.get();
        persisted.setStatus(PautaStatus.OPENED);
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
