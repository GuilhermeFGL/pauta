package com.example.pauta.controller.dto;

import lombok.Data;
import org.springframework.lang.Nullable;

import javax.validation.constraints.Size;

@Data
public class OpenPautaRequest {

    @Nullable
    @Size(min = 1)
    private Integer durationInMinutes;

}
