package com.myboard.exception.http;

import com.myboard.exception.MyboardException;
import org.springframework.http.HttpStatus;

public class ForbiddenException extends MyboardException {
    public ForbiddenException() {
        super(HttpStatus.FORBIDDEN);
    }
}
