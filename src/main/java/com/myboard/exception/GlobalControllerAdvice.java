package com.myboard.exception;

import com.myboard.exception.common.ErrorResponse;
import com.myboard.exception.common.ErrorType;
import com.myboard.exception.http.InternalServerErrorException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolationException;
import java.util.Objects;

@Slf4j
@RestControllerAdvice
public class GlobalControllerAdvice {

    @ExceptionHandler(myboardException.class)
    public ResponseEntity<ErrorResponse> handleMyboardException(myboardException e) {
        log.info(String.format("%s %s", e.getClass().getName(), e.getErrorType().getMessage()), e);

        return ResponseEntity.status(e.getHttpStatus())
                .body(ErrorResponse.of(e.getErrorType()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        log.info(String.format("Exception %s", e.getMessage()), e);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorResponse.of(ErrorType.X001));
    }

    @ExceptionHandler(InternalServerErrorException.class)
    public ResponseEntity<ErrorResponse> handleInternalServerErrorException(InternalServerErrorException e) {
        log.info(String.format("InternalServerErrorException %s", e.getServerMessage()), e);

        return ResponseEntity.status(e.getHttpStatus())
                .body(ErrorResponse.of(e.getErrorType()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleBindingException(MethodArgumentNotValidException e) {
        FieldError fieldError = e.getFieldError();
        Objects.requireNonNull(fieldError);

        log.info(String.format("MethodArgumentNotValidException %s", e.getMessage()), e);
        ErrorType errorType = ErrorType.of(fieldError.getDefaultMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.of(errorType));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolationException(ConstraintViolationException e) {
        String message = e.getMessage().replace(" ", "");
        int idx = message.indexOf(":");
        String code = message.substring(idx + 1);

        log.info(String.format("ConstraintViolationException %s", code), e);

        ErrorType errorType = ErrorType.of(code);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.of(errorType));
    }

}
