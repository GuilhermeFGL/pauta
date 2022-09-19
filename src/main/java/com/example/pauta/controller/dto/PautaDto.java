package com.example.pauta.controller.dto;

import lombok.Data;
import org.springframework.lang.Nullable;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
public class PautaDto implements Serializable {

    @NotNull
    @NotEmpty
    private String description;

    @Nullable
    @Min(1)
    private Integer durationInMinutes;

}
