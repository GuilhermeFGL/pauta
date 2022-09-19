package com.example.pauta.service;

import com.example.pauta.controller.dto.UserRequest;
import com.example.pauta.controller.dto.UserResponse;
import com.example.pauta.repository.UserRepository;
import com.example.pauta.repository.entity.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

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
