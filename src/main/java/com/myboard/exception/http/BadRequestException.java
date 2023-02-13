package com.myboard.exception.http;

import com.myboard.exception.MyboardException;
import org.springframework.http.HttpStatus;

public class BadRequestException extends MyboardException {
    public BadRequestException() {
        super(HttpStatus.BAD_REQUEST);
    }
}
