package com.arthurpv15.apimanagement.exception;

public class ForbiddenAccessException extends RuntimeException {

    public ForbiddenAccessException() {
        super("You do not have permission to access this resource");
    }
}
