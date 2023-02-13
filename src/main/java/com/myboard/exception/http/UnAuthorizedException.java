package com.myboard.exception.http;

import com.myboard.exception.MyboardException;
import org.springframework.http.HttpStatus;

public class UnAuthorizedException extends MyboardException {
    public UnAuthorizedException() {
        super(HttpStatus.UNAUTHORIZED);
    }
}
