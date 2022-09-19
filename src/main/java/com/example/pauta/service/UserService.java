package com.example.pauta.service;

import com.example.pauta.controller.dto.UserRequest;
import com.example.pauta.controller.dto.UserResponse;
import com.example.pauta.repository.UserRepository;
import com.example.pauta.repository.entity.UserEntity;
import com.example.pauta.service.exception.InvalidUserException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    public static final String ERROR_INVALID_CFP = "CPF must not be empty or null";
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

    public UserResponse create(UserRequest request) {
        if (request.getCpf() == null || request.getCpf().isEmpty()) {
            throw new InvalidUserException(ERROR_INVALID_CFP);
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
