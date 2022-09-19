package com.example.pauta.service;

import com.example.pauta.controller.dto.PautaRequest;
import com.example.pauta.controller.dto.PautaResponse;
import com.example.pauta.repository.PautaRepository;
import com.example.pauta.repository.dao.PautaDao;
import com.example.pauta.repository.dao.enums.PautaStatus;
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
        PautaDao pautaDao = new PautaDao();
        pautaDao.setDescription(pautaDto.getDescription());
        pautaDao.setDuration(pautaDto.getDurationInMinutes());
        pautaDao.setStatus(PautaStatus.CREATED);

        PautaDao persisted = this.repository.save(pautaDao);

        return this.mapToResponse(persisted);
    }

    public PautaResponse openPauta(Long id) {
        Optional<PautaDao> oPersisted = this.repository.findById(id);
        if (!oPersisted.isPresent()) {
            return null;
        }

        LocalDateTime now = LocalDateTime.now();
        PautaDao persisted = oPersisted.get();
        persisted.setStatus(PautaStatus.OPENED);
        persisted.setStart(now);
        persisted.setEnd(now.plusMinutes(persisted.getDuration()));
        persisted = this.repository.save(persisted);

        return this.mapToResponse(persisted);
    }

    private PautaResponse mapToResponse(PautaDao dao) {
        PautaResponse response = new PautaResponse();
        response.setId(dao.getId());
        response.setDescription(dao.getDescription());
        response.setDurationInMinutes(dao.getDuration());
        response.setStatus(dao.getStatus());
        return response;
    }

}
