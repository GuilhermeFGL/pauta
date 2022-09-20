package com.example.pauta.controller.dto;

import com.example.pauta.repository.entity.enums.VotoOption;
import lombok.Data;

@Data
public class VotoRequest {

    private Long pautaId;
    private VotoOption voto;
}
