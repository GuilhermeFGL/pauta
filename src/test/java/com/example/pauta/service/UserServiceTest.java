package com.example.pauta.service;

import com.example.pauta.controller.dto.UserRequest;
import com.example.pauta.controller.dto.UserResponse;
import com.example.pauta.repository.UserRepository;
import com.example.pauta.repository.entity.UserEntity;
import com.example.pauta.service.exception.InvalidUserException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository repository;

    private UserService service;

    @BeforeEach
    public void setUp() {
        this.service = new UserService(this.repository);
    }

    @Test
    public void testGetUserShouldReturnUserWhenExists() {
        Long id = 1L;

        UserEntity user = new UserEntity();
        user.setId(id);

        when(this.repository.findById(id)).thenReturn(Optional.of(user));

        UserResponse result = this.service.getUser(id);

        assertNotNull(result);
    }

    @Test
    public void testGetUserShouldReturnNullWhenNotExists() {
        Long id = 1L;

        when(this.repository.findById(id)).thenReturn(Optional.empty());

        UserResponse result = this.service.getUser(id);

        assertNull(result);
    }

    @Test
    public void testCreateUserShouldCreateUserWhenValid() {
        String cpf = "47934290098";

        UserRequest request = new UserRequest();
        request.setCpf(cpf);

        when(this.repository.save(any(UserEntity.class))).thenAnswer(i -> i.getArguments()[0]);

        UserResponse result = this.service.createUser(request);

        assertNotNull(result);
        assertEquals(cpf, request.getCpf());
    }

    @Test
    public void testCreateUserShouldNotCreateUserWhenCpfNull() {
        String cpf = null;

        UserRequest request = new UserRequest();
        request.setCpf(cpf);

        assertThrows(InvalidUserException.class, () -> this.service.createUser(request));
    }

    @Test
    public void testCreateUserShouldNotCreateUserWhenCpfEmpty() {
        String cpf = "";

        UserRequest request = new UserRequest();
        request.setCpf(cpf);


        assertThrows(InvalidUserException.class, () -> this.service.createUser(request));
    }

    @Test
    public void testCreateUserShouldNotCreateUserWhenCpfInvalid() {
        String cpf = "XXX";

        UserRequest request = new UserRequest();
        request.setCpf(cpf);


        assertThrows(InvalidUserException.class, () -> this.service.createUser(request));
    }

}