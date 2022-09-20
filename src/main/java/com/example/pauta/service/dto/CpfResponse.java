package com.example.pauta.service.dto;

import com.example.pauta.service.dto.enums.CpfStatus;
import lombok.Data;

@Data
public class CpfResponse {

    private CpfStatus status;

}
