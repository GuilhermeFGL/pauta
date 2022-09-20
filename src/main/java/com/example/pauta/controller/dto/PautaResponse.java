package com.example.pauta.controller.dto;

import com.example.pauta.repository.entity.enums.PautaResult;
import com.example.pauta.repository.entity.enums.PautaStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PautaResponse {

    private Long id;
    private String description;
    private Integer durationInMinutes;

    private PautaStatus status;
    private PautaResult result;

    private LocalDateTime start;
    private LocalDateTime end;

}
