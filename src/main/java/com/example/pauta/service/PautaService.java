package com.example.pauta.service;

import com.example.pauta.controller.dto.PautaRequest;
import com.example.pauta.controller.dto.PautaResponse;
import com.example.pauta.repository.PautaRepository;
import com.example.pauta.repository.dao.PautaDao;
import com.example.pauta.repository.dao.enums.PautaStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PautaService {

    private final PautaRepository repository;

    @Autowired
    public PautaService(PautaRepository repository) {
        this.repository = repository;
    }

    public PautaResponse createNewPauta(PautaRequest pautaDto) {
        PautaDao pautaDao = new PautaDao();
        pautaDao.setDescription(pautaDto.getDescription());
        pautaDao.setDuration(pautaDto.getDurationInMinutes());
        pautaDao.setStatus(PautaStatus.CREATED);

        PautaDao persisted = repository.save(pautaDao);

        PautaResponse response = new PautaResponse();
        response.setId(persisted.getId());
        response.setDescription(persisted.getDescription());
        response.setDurationInMinutes(persisted.getDuration());
        response.setStatus(persisted.getStatus());
        return response;
    }

}
