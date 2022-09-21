package com.example.pauta.service;

import com.example.pauta.controller.dto.UserRequest;
import com.example.pauta.controller.dto.UserResponse;
import com.example.pauta.helper.ValidateCpfHelper;
import com.example.pauta.repository.UserRepository;
import com.example.pauta.repository.entity.UserEntity;
import com.example.pauta.service.exception.InvalidUserException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
public class UserService {

    public static final String ERROR_INVALID_CFP = "CPF is not valid";
    public static final String ERROR_DUPLICATED_CFP = "CPF already registered";

    private final UserRepository repository;

    @Autowired
    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    public UserResponse getUser(Long id) {
        return this.repository.findById(id)
                .map(this::mapToResponse)
                .orElse(null);
    }

    public UserResponse createUser(UserRequest request) {
        UserService.log.info("Create user for CPF {}", request.getCpf());

        if (request.getCpf() == null || request.getCpf().isEmpty() || !ValidateCpfHelper.isCPF(request.getCpf())) {
            UserService.log.error("Invalid CPF number {}", request.getCpf());
            throw new InvalidUserException(UserService.ERROR_INVALID_CFP);
        }

        Optional<UserEntity> oUser = this.repository.findByCpf(request.getCpf());
        if (oUser.isPresent()) {
            UserService.log.error("CPF already registered {}", request.getCpf());
            throw new InvalidUserException(UserService.ERROR_DUPLICATED_CFP);
        }

        UserEntity entity = new UserEntity();
        entity.setCpf(request.getCpf());

        UserEntity persisted = this.repository.save(entity);

        return this.mapToResponse(persisted);
    }

    private UserResponse mapToResponse(UserEntity persisted) {
        UserResponse response = new UserResponse();
        response.setId(persisted.getId());
        response.setCpf(persisted.getCpf());
        return response;
    }

}
