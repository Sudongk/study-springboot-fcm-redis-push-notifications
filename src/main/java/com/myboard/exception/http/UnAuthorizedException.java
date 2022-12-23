package com.myboard.exception.http;

import com.myboard.exception.myboardException;
import org.springframework.http.HttpStatus;

public class UnAuthorizedException extends myboardException {
    public UnAuthorizedException() {
        super(HttpStatus.UNAUTHORIZED);
    }
}
