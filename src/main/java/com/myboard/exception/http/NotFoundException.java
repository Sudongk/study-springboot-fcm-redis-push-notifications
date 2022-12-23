package com.myboard.exception.http;

import com.myboard.exception.myboardException;
import org.springframework.http.HttpStatus;

public class NotFoundException extends myboardException {
    public NotFoundException() {
        super(HttpStatus.NOT_FOUND);
    }
}
