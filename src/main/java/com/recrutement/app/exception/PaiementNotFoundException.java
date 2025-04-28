package com.recrutement.app.exception;

public class PaiementNotFoundException extends RuntimeException {

    // Constructor that accepts the error message
    public PaiementNotFoundException(String message) {
        super(message);
    }
}
