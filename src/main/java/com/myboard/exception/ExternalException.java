package com.myboard.exception;

import org.springframework.http.HttpStatus;

public class ExternalException extends myboardException {
    public ExternalException() {
        super(HttpStatus.BAD_REQUEST);
    }
}
