package com.myboard.exception;

import com.myboard.exception.common.ErrorType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public class MyboardException extends RuntimeException {
    private final HttpStatus httpStatus;
    private final ErrorType errorType = ErrorType.of(this.getClass());
}
