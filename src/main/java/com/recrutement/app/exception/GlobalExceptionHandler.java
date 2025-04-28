package com.recrutement.app.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice  // This makes it a global exception handler
public class GlobalExceptionHandler {

    // Handle the specific StatistiqueNotFoundException
    @ExceptionHandler(StatistiqueNotFoundException.class)
    public ResponseEntity<String> handleStatistiqueNotFound(StatistiqueNotFoundException ex) {
        // Return a 404 Not Found with the error message
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    // Handle the specific PaiementNotFoundException
    @ExceptionHandler(PaiementNotFoundException.class)
    public ResponseEntity<String> handlePaiementNotFound(PaiementNotFoundException ex) {
        // Return a 404 Not Found with the error message
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    // Handle other generic exceptions
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleAllExceptions(Exception ex) {
        // Return a 500 Internal Server Error with a general error message
        return new ResponseEntity<>("An error occurred: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
