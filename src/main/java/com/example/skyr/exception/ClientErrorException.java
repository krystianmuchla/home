package com.example.skyr.exception;

public class ClientErrorException extends RuntimeException {
    public ClientErrorException(final String message) {
        super(message);
    }
}
