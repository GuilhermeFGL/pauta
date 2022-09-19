package com.example.pauta.controller;

import com.example.pauta.controller.dto.PautaDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/pauta")
public class PautaController {

    @PostMapping
    public ResponseEntity<String> create(@RequestBody PautaDto pautaDto) {
        return new ResponseEntity<>("ok", HttpStatus.CREATED);
    }

}
