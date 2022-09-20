package com.example.pauta.controller;

import com.example.pauta.controller.dto.VotoRequest;
import com.example.pauta.service.VotoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/voto")
public class VotoController {

    private final VotoService service;

    @Autowired
    public VotoController(VotoService service) {
        this.service = service;
    }

    @PatchMapping
    public ResponseEntity<Void> vote(@Header("userId") Long userId,
                                     @RequestBody VotoRequest votoDto) {
        this.service.voto(userId, votoDto);
        return new ResponseEntity<>(null, HttpStatus.ACCEPTED);
    }

}
