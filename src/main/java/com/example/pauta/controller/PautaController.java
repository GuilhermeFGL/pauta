package com.example.pauta.controller;

import com.example.pauta.controller.dto.OpenPautaRequest;
import com.example.pauta.controller.dto.PautaRequest;
import com.example.pauta.controller.dto.PautaResponse;
import com.example.pauta.service.PautaService;
import io.swagger.v3.oas.annotations.Operation;
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

    @Operation(summary = "Get description of a pauta by ID")
    @GetMapping("/{id}")
    public ResponseEntity<PautaResponse> getPauta(@PathVariable Long id) {
        PautaResponse pautaResponse = this.service.getPauta(id);
        return new ResponseEntity<>(pautaResponse,
                pautaResponse != null ? HttpStatus.OK : HttpStatus.NOT_FOUND);
    }

    @Operation(summary = "Create new pauta")
    @PostMapping
    public ResponseEntity<PautaResponse> createPauta(@RequestBody PautaRequest pautaDto) {
        PautaResponse pautaResponse = this.service.createNewPauta(pautaDto);
        return new ResponseEntity<>(pautaResponse, HttpStatus.CREATED);
    }

    @Operation(summary = "Open existing pauta")
    @PatchMapping("/{id}/open")
    public ResponseEntity<PautaResponse> openPauta(@PathVariable Long id,
                                                   @RequestBody OpenPautaRequest openPautaRequest) {
        PautaResponse pautaResponse = this.service.openPauta(id, openPautaRequest.getDurationInMinutes());
        return new ResponseEntity<>(pautaResponse,
                pautaResponse != null ? HttpStatus.ACCEPTED : HttpStatus.NO_CONTENT);
    }

}
