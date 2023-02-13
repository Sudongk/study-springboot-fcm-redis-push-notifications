package com.myboard.exception;

import org.springframework.http.HttpStatus;

public class ExternalException extends MyboardException {
    public ExternalException() {
        super(HttpStatus.BAD_REQUEST);
    }
}
