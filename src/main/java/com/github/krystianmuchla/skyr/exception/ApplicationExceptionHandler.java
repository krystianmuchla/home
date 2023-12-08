package com.github.krystianmuchla.skyr.exception;

import com.github.krystianmuchla.skyr.exception.api.ProblemResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
public final class ApplicationExceptionHandler {
    private static final String UNEXPECTED_ERROR_MESSAGE = "Unexpected error occurred";

    @ExceptionHandler
    public ResponseEntity<ProblemResponse> handle(final Throwable throwable) {
        log.warn(UNEXPECTED_ERROR_MESSAGE, throwable);
        return new ResponseEntity<>(new ProblemResponse(UNEXPECTED_ERROR_MESSAGE), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler
    public ResponseEntity<ProblemResponse> handle(final ClientErrorException exception) {
        log.info("{}", exception.getMessage(), exception);
        return new ResponseEntity<>(new ProblemResponse(exception.getMessage()), exception.getStatus());
    }

    @ExceptionHandler
    public ResponseEntity<ProblemResponse> handle(final ServerErrorException exception) {
        log.warn("{}", exception.getMessage(), exception);
        return new ResponseEntity<>(new ProblemResponse(UNEXPECTED_ERROR_MESSAGE), exception.getStatus());
    }
}
