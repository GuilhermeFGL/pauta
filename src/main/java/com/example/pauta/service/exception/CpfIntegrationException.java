package com.example.pauta.service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
public class CpfIntegrationException extends RuntimeException {

    public CpfIntegrationException(String message) {
        super(message);
    }
}
