package com.example.pauta.service;

import com.example.pauta.repository.PautaRepository;
import com.example.pauta.repository.entity.PautaEntity;
import com.example.pauta.repository.entity.VotoEntity;
import com.example.pauta.repository.entity.enums.PautaResult;
import com.example.pauta.repository.entity.enums.PautaStatus;
import com.example.pauta.repository.entity.enums.VotoOption;
import com.example.pauta.service.exception.InvalidPautaException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ComputePautaService {

    @Value("#{new Double('${pauta.approval-score}')}")
    private Double pautaApprovalScore;

    private final PautaRepository repository;
    private final VotoService votoService;
    private final MessagePublisher messagePublisher;

    @Autowired
    public ComputePautaService(PautaRepository repository,
                        VotoService votoService,
                        MessagePublisher messagePublisher) {
        this.repository = repository;
        this.votoService = votoService;
        this.messagePublisher = messagePublisher;
    }

    public void closePauta(Long pautaId) {
        Optional<PautaEntity> oPauta = this.repository.findById(pautaId);
        if(!oPauta.isPresent()) {
            throw new InvalidPautaException("Pauta not found");
        }

        PautaEntity pauta = oPauta.get();

        List<VotoEntity> votos = this.votoService.findVotosByPautaId(pauta.getId());
        Long approved = votos.stream().filter(v -> v.getVoto() == VotoOption.SIM).count();
        Long rejected = votos.stream().filter(v -> v.getVoto() == VotoOption.NAO).count();

        pauta.setStatus(PautaStatus.CLOSED);
        pauta.setResult(this.computePautaResult(approved, rejected));
        pauta = this.repository.save(pauta);

        this.messagePublisher.sendMessageToPublishPautaResult(pauta.getId(), pauta.getResult());
    }

    private PautaResult computePautaResult(Long approved, Long rejected) {
        return approved > 0 &&
                (rejected == 0 || Double.valueOf(approved) / Double.valueOf(rejected) >= this.pautaApprovalScore) ?
                PautaResult.APPROVED : PautaResult.REJECTED;
    }

}
