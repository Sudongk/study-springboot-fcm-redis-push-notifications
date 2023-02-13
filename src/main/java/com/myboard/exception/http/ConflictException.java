package com.myboard.exception.http;

import com.myboard.exception.MyboardException;
import org.springframework.http.HttpStatus;

public class ConflictException extends MyboardException {
    public ConflictException() {
        super(HttpStatus.CONFLICT);
    }
}