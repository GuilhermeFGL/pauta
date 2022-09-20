package com.example.pauta.controller;

import com.example.pauta.controller.dto.VotoRequest;
import com.example.pauta.controller.dto.VotoResponse;
import com.example.pauta.service.VotoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/voto")
public class VotoController {

    public static final String MESSAGE_VOTO_COMMITTED = "Voto committed";

    private final VotoService service;

    @Autowired
    public VotoController(VotoService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<VotoResponse> commitVoto(@RequestHeader(value="userId") Long userId,
                                     @RequestBody VotoRequest votoDto) {
        this.service.voto(userId, votoDto);

        VotoResponse response = new VotoResponse();
        response.setMessage(MESSAGE_VOTO_COMMITTED);

        return new ResponseEntity<>(response, HttpStatus.ACCEPTED);
    }

}
