package com.example.pauta.controller;

import com.example.pauta.controller.dto.PautaRequest;
import com.example.pauta.controller.dto.PautaResponse;
import com.example.pauta.service.PautaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/pauta")
public class PautaController {

    private final PautaService service;

    @Autowired
    public PautaController(PautaService service) {
        this.service = service;
    }

    @GetMapping("/{id}")
    public ResponseEntity<PautaResponse> getPauta(@PathVariable Long id) {
        PautaResponse pautaResponse = this.service.getPauta(id);
        return new ResponseEntity<>(pautaResponse,
                pautaResponse != null ? HttpStatus.CREATED : HttpStatus.NOT_FOUND);
    }

    @PostMapping
    public ResponseEntity<PautaResponse> createPauta(@RequestBody PautaRequest pautaDto) {
        PautaResponse response = this.service.createNewPauta(pautaDto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PatchMapping("/{id}/open")
    public ResponseEntity<PautaResponse> openPauta(@PathVariable Long id) {
        PautaResponse response = this.service.openPauta(id);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

}
