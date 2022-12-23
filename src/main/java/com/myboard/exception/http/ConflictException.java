package com.myboard.exception.http;

import com.myboard.exception.myboardException;
import org.springframework.http.HttpStatus;

public class ConflictException extends myboardException {
    public ConflictException() {
        super(HttpStatus.CONFLICT);
    }
}