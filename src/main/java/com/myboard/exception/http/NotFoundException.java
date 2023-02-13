package com.myboard.exception.http;

import com.myboard.exception.MyboardException;
import org.springframework.http.HttpStatus;

public class NotFoundException extends MyboardException {
    public NotFoundException() {
        super(HttpStatus.NOT_FOUND);
    }
}
