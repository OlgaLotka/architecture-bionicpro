package org.example.exception;

public class BadAuthorizeException extends RuntimeException {
    public BadAuthorizeException(Exception ex) {
        super(ex);
    }
}
