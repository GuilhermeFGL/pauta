package com.example.pauta.controller.dto;

import com.example.pauta.repository.entity.enums.VotoOption;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class VotoRequest {

    @NotNull
    private Long pautaId;

    @NotNull
    private VotoOption voto;

}
