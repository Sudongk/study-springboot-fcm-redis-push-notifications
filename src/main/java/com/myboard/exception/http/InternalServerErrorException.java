package com.myboard.exception.http;

import com.myboard.exception.MyboardException;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class InternalServerErrorException extends MyboardException {
    private final String serverMessage;

    public InternalServerErrorException(String serverMessage) {
        super(HttpStatus.INTERNAL_SERVER_ERROR);
        this.serverMessage = serverMessage;
    }
}
