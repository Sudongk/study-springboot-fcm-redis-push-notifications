package com.myboard.exception.http;

import com.myboard.exception.myboardException;
import org.springframework.http.HttpStatus;

public class ForbiddenException extends myboardException {
    public ForbiddenException() {
        super(HttpStatus.FORBIDDEN);
    }
}
