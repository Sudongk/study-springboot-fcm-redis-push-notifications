package com.myboard.exception.firebase;

import com.myboard.exception.MyboardException;
import org.springframework.http.HttpStatus;

public class FcmInitException extends MyboardException {
    public FcmInitException() {
        super(HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
