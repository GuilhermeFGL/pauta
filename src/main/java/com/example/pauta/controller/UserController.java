package com.example.pauta.controller;

import com.example.pauta.controller.dto.UserRequest;
import com.example.pauta.controller.dto.UserResponse;
import com.example.pauta.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService service;

    @Autowired
    public UserController(UserService service) {
        this.service = service;
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUser(@PathVariable Long id) {
        UserResponse userResponse = this.service.getUser(id);
        return new ResponseEntity<>(userResponse,
                userResponse != null ? HttpStatus.OK : HttpStatus.NOT_FOUND);
    }

    @PostMapping
    public ResponseEntity<UserResponse> createUser(@RequestBody UserRequest userDto) {
        UserResponse userResponse = this.service.createUser(userDto);
        return new ResponseEntity<>(userResponse, HttpStatus.CREATED);
    }
}
