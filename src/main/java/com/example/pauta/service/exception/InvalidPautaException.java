package com.example.pauta.service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class InvalidPautaException extends RuntimeException {

    public InvalidPautaException(String message) {
        super(message);
    }
}
