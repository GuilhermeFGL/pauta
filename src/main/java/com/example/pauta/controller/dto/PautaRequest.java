package com.example.pauta.controller.dto;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class PautaRequest {

    @NotNull
    @NotEmpty
    private String description;

}
