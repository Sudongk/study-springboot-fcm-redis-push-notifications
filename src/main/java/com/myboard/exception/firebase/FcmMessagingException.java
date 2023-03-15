package com.myboard.exception.firebase;

import com.myboard.exception.MyboardException;
import org.springframework.http.HttpStatus;

public class FcmMessagingException extends MyboardException {
    public FcmMessagingException() {
        super(HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
