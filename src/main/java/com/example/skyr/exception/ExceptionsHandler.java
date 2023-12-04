package com.example.skyr.exception;

import com.example.skyr.exception.api.ProblemResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
public final class ExceptionsHandler {
    private static final String UNEXPECTED_ERROR_MESSAGE = "Unexpected error occurred";

    @ExceptionHandler
    public ResponseEntity<ProblemResponse> handle(final ClientErrorException exception) {
        log.info("{}", exception.getMessage(), exception);
        return new ResponseEntity<>(new ProblemResponse(exception.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<ProblemResponse> handle(final NotFoundException exception) {
        log.info("{}", exception.getMessage(), exception);
        return new ResponseEntity<>(new ProblemResponse(exception.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    public ResponseEntity<ProblemResponse> handle(final ServiceErrorException exception) {
        log.warn("{}", exception.getMessage(), exception);
        return new ResponseEntity<>(new ProblemResponse(UNEXPECTED_ERROR_MESSAGE), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler
    public ResponseEntity<ProblemResponse> handle(final Exception exception) {
        log.warn(UNEXPECTED_ERROR_MESSAGE, exception);
        return new ResponseEntity<>(new ProblemResponse(UNEXPECTED_ERROR_MESSAGE), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
