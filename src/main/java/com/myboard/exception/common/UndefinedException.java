package com.myboard.exception.common;

import com.myboard.exception.http.InternalServerErrorException;

public class UndefinedException extends InternalServerErrorException {
    public UndefinedException(String serverMessage) {
        super(serverMessage);
    }
}
