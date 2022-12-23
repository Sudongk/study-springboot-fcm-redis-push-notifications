package com.myboard.exception.http;

import com.myboard.exception.myboardException;
import org.springframework.http.HttpStatus;

public class BadRequestException extends myboardException {
    public BadRequestException() {
        super(HttpStatus.BAD_REQUEST);
    }
}
