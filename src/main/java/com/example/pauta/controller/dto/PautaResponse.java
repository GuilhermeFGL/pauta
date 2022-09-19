package com.example.pauta.controller.dto;

import com.example.pauta.repository.dao.enums.PautaStatus;
import lombok.Data;

@Data
public class PautaResponse {

    private Long id;
    private String description;
    private Integer durationInMinutes;
    private PautaStatus status;

}
