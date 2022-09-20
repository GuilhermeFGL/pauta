package com.example.pauta.service;

import com.example.pauta.controller.dto.PautaResponse;
import com.example.pauta.controller.dto.UserResponse;
import com.example.pauta.controller.dto.VotoRequest;
import com.example.pauta.repository.VotoRepository;
import com.example.pauta.repository.entity.VotoEntity;
import com.example.pauta.service.exception.InvalidVoteException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class VotoService {

    public static final String ERROR_USER_NOT_FOUND = "User not found";
    public static final String ERROR_PAUTA_NOT_FOUND = "Pauta not found";
    private static final String ERROR_VOTO_ALREADY_COMMITTED = "Voto is already commited";
    private static final String ERROR_VOTO_INVALID = "Voto must not be null";
    private static final String ERROR_VOTO_UNABLE = "Unable to vote";

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

    public void voto(Long userId, VotoRequest votoRequest) {
        if (votoRequest.getVoto() == null) {
            throw new InvalidVoteException(VotoService.ERROR_VOTO_INVALID);
        }

        UserResponse user = this.userService.getUser(userId);
        if (user == null) {
            throw new InvalidVoteException(VotoService.ERROR_USER_NOT_FOUND);
        }

        PautaResponse pauta = this.pautaService.getOnGoingPauta(votoRequest.getPautaId());
        if (pauta == null) {
            throw new InvalidVoteException(VotoService.ERROR_PAUTA_NOT_FOUND);
        }

        VotoEntity.PautaUserKey votoKey = new VotoEntity.PautaUserKey();
        votoKey.setPautaId(pauta.getId());
        votoKey.setUserId(user.getId());

        Optional<VotoEntity> oVoto = this.repository.findById(votoKey);
        if (oVoto.isPresent()) {
            throw new InvalidVoteException(VotoService.ERROR_VOTO_ALREADY_COMMITTED);
        }

        if (!this.cpfService.cpfCanVote(user.getCpf())) {
            throw new InvalidVoteException(VotoService.ERROR_VOTO_UNABLE);
        }

        VotoEntity voto = new VotoEntity();
        voto.setVotoKey(votoKey);
        voto.setVoto(votoRequest.getVoto());

        this.repository.save(voto);
    }

}
