package com.recrutement.app.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Getter
@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class ApiException extends RuntimeException {
    
    private final HttpStatus status;
    private final String message;
    
    public ApiException(HttpStatus status, String message) {
        super(message);
        this.status = status;
        this.message = message;
    }
    
    public ApiException(String message, HttpStatus status, Throwable cause) {
        super(message, cause);
        this.status = status;
        this.message = message;
    }
}
