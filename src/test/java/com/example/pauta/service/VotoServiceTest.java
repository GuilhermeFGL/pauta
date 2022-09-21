package com.example.pauta.service;

import com.example.pauta.controller.dto.PautaResponse;
import com.example.pauta.controller.dto.UserResponse;
import com.example.pauta.controller.dto.VotoRequest;
import com.example.pauta.repository.VotoRepository;
import com.example.pauta.repository.entity.VotoEntity;
import com.example.pauta.repository.entity.enums.VotoOption;
import com.example.pauta.service.exception.InvalidVotoException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class VotoServiceTest {

    @Mock
    private VotoRepository repository;

    @Mock
    private UserService userService;

    @Mock
    private PautaService pautaService;

    @Mock
    private CpfService cpfService;

    private VotoService service;

    @BeforeEach
    public void setUp() {
        this.service = new VotoService(this.repository, this.userService, this.pautaService, this.cpfService);
    }

    @Test
    public void testFindVotosByPautaIdShouldReturnVotosForAPauta() {
        Long id = 1L;

        when(this.repository.findByVotoKeyPautaId(eq(id))).thenReturn(new ArrayList<>());

        List<VotoEntity> result = this.service.findVotosByPautaId(id);

        assertNotNull(result);
    }

    @Test
    public void testCommitVotoShouldCreateVotoWhenValid() {
        Long userId = 1L;
        Long pautaId = 1L;
        String cpf = "XXX";
        VotoOption votoOption = VotoOption.SIM;

        VotoRequest voto = new VotoRequest();
        voto.setVoto(votoOption);
        voto.setPautaId(pautaId);

        UserResponse user = new UserResponse();
        user.setCpf(cpf);
        user.setId(userId);

        PautaResponse pauta = new PautaResponse();
        pauta.setId(pautaId);

        when(this.userService.getUser(eq(userId))).thenReturn(user);
        when(this.pautaService.getOnGoingPauta(eq(pautaId))).thenReturn(pauta);
        when(this.repository.findById(any(VotoEntity.PautaUserKey.class))).thenReturn(Optional.empty());
        when(this.cpfService.cpfCanVote(eq(cpf))).thenReturn(true);
        when(this.repository.save(any(VotoEntity.class))).thenAnswer(i -> i.getArguments()[0]);

        VotoEntity result = this.service.commitVoto(userId, voto);

        assertNotNull(result);
        assertEquals(votoOption, result.getVoto());
        assertEquals(pautaId, result.getVotoKey().getPautaId());
        assertEquals(userId, result.getVotoKey().getUserId());
    }

    @Test
    public void testCommitVotoShouldNotCreateVotoWhenVotoIsInvalid() {
        Long userId = 1L;
        Long pautaId = 1L;
        VotoOption votoOption = null;

        VotoRequest voto = new VotoRequest();
        voto.setVoto(votoOption);
        voto.setPautaId(pautaId);

        assertThrows(InvalidVotoException.class, () -> this.service.commitVoto(userId, voto));
    }

    @Test
    public void testCommitVotoShouldNotCreateVotoWhenUserNotFound() {
        Long userId = 1L;
        Long pautaId = 1L;
        VotoOption votoOption = VotoOption.SIM;

        VotoRequest voto = new VotoRequest();
        voto.setVoto(votoOption);
        voto.setPautaId(pautaId);

        when(this.userService.getUser(eq(userId))).thenReturn(null);

        assertThrows(InvalidVotoException.class, () -> this.service.commitVoto(userId, voto));
    }

    @Test
    public void testCommitVotoShouldNotCreateVotoWhenPautaNotFound() {
        Long userId = 1L;
        Long pautaId = 1L;
        String cpf = "XXX";
        VotoOption votoOption = VotoOption.SIM;

        VotoRequest voto = new VotoRequest();
        voto.setVoto(votoOption);
        voto.setPautaId(pautaId);

        UserResponse user = new UserResponse();
        user.setCpf(cpf);
        user.setId(userId);

        when(this.userService.getUser(eq(userId))).thenReturn(user);
        when(this.pautaService.getOnGoingPauta(eq(pautaId))).thenReturn(null);

        assertThrows(InvalidVotoException.class, () -> this.service.commitVoto(userId, voto));
    }

    @Test
    public void testCommitVotoShouldNotCreateVotoWhenVotoAlreadyExists() {
        Long userId = 1L;
        Long pautaId = 1L;
        String cpf = "XXX";
        VotoOption votoOption = VotoOption.SIM;

        VotoRequest voto = new VotoRequest();
        voto.setVoto(votoOption);
        voto.setPautaId(pautaId);

        UserResponse user = new UserResponse();
        user.setCpf(cpf);
        user.setId(userId);

        PautaResponse pauta = new PautaResponse();
        pauta.setId(pautaId);

        when(this.userService.getUser(eq(userId))).thenReturn(user);
        when(this.pautaService.getOnGoingPauta(eq(pautaId))).thenReturn(pauta);
        when(this.repository.findById(any(VotoEntity.PautaUserKey.class))).thenReturn(Optional.of(new VotoEntity()));

        assertThrows(InvalidVotoException.class, () -> this.service.commitVoto(userId, voto));
    }

    @Test
    public void testCommitVotoShouldNotCreateVotoWhenUnableToVote() {
        Long userId = 1L;
        Long pautaId = 1L;
        String cpf = "XXX";
        VotoOption votoOption = VotoOption.SIM;

        VotoRequest voto = new VotoRequest();
        voto.setVoto(votoOption);
        voto.setPautaId(pautaId);

        UserResponse user = new UserResponse();
        user.setCpf(cpf);
        user.setId(userId);

        PautaResponse pauta = new PautaResponse();
        pauta.setId(pautaId);

        when(this.userService.getUser(eq(userId))).thenReturn(user);
        when(this.pautaService.getOnGoingPauta(eq(pautaId))).thenReturn(pauta);
        when(this.repository.findById(any(VotoEntity.PautaUserKey.class))).thenReturn(Optional.empty());
        when(this.cpfService.cpfCanVote(eq(cpf))).thenReturn(false);

        assertThrows(InvalidVotoException.class, () -> this.service.commitVoto(userId, voto));
    }

}