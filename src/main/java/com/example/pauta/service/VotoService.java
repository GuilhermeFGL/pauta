package com.example.pauta.service;

import com.example.pauta.controller.dto.PautaResponse;
import com.example.pauta.controller.dto.UserResponse;
import com.example.pauta.controller.dto.VotoRequest;
import com.example.pauta.repository.VotoRepository;
import com.example.pauta.repository.entity.VotoEntity;
import com.example.pauta.service.exception.InvalidVotoException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class VotoService {

    private static final String ERROR_USER_NOT_FOUND = "User not found";
    private static final String ERROR_PAUTA_NOT_FOUND = "Pauta not found";
    private static final String ERROR_VOTO_ALREADY_COMMITTED = "Voto is already commited";
    private static final String ERROR_VOTO_INVALID = "Voto must not be null";
    private static final String ERROR_VOTO_UNABLE = "Unable to commit voto";

    private final VotoRepository repository;
    private final UserService userService;
    private final PautaService pautaService;
    private final CpfService cpfService;

    @Autowired
    public VotoService(VotoRepository repository,
                       UserService userService,
                       PautaService pautaService,
                       CpfService cpfService) {
        this.repository = repository;
        this.userService = userService;
        this.pautaService = pautaService;
        this.cpfService = cpfService;
    }

    public List<VotoEntity> findVotosByPautaId(Long pautaId) {
        return this.repository.findByVotoKeyPautaId(pautaId);
    }

    public VotoEntity commitVoto(Long userId, VotoRequest votoRequest) {
        VotoService.log.info("Commit voto for user ID {}, pauta ID {} and voto option {}",
                userId, votoRequest.getPautaId(), votoRequest.getVoto());

        if (votoRequest.getVoto() == null) {
            VotoService.log.error("Invalid voto option {}", votoRequest.getVoto());
            throw new InvalidVotoException(VotoService.ERROR_VOTO_INVALID);
        }

        UserResponse user = this.userService.getUser(userId);
        if (user == null) {
            VotoService.log.error("User not found for ID {}", userId);
            throw new InvalidVotoException(VotoService.ERROR_USER_NOT_FOUND);
        }

        PautaResponse pauta = this.pautaService.getOngoingPauta(votoRequest.getPautaId());
        if (pauta == null) {
            VotoService.log.error("Pauta not found for ID {}", votoRequest.getPautaId());
            throw new InvalidVotoException(VotoService.ERROR_PAUTA_NOT_FOUND);
        }

        VotoEntity.PautaUserKey votoKey = new VotoEntity.PautaUserKey();
        votoKey.setPautaId(pauta.getId());
        votoKey.setUserId(user.getId());

        Optional<VotoEntity> oVoto = this.repository.findById(votoKey);
        if (oVoto.isPresent()) {
            VotoService.log.error("Voto already exists for user {} and pauta {}", userId, votoRequest.getPautaId());
            throw new InvalidVotoException(VotoService.ERROR_VOTO_ALREADY_COMMITTED);
        }

        if (!this.cpfService.cpfCanVote(user.getCpf())) {
            VotoService.log.error("CPF is unable to vote {}", user.getCpf());
            throw new InvalidVotoException(VotoService.ERROR_VOTO_UNABLE);
        }

        VotoEntity voto = new VotoEntity();
        voto.setVotoKey(votoKey);
        voto.setVoto(votoRequest.getVoto());

        return this.repository.save(voto);
    }

}
