package org.blackequity.application.service;

public class AdminPasswordException extends RuntimeException {

    public AdminPasswordException(String message) {
        super(message);
    }

    public AdminPasswordException(String message, Throwable cause) {
        super(message, cause);
    }
}
