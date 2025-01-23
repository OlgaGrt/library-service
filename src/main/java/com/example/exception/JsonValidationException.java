package com.example.exception;

public class JsonValidationException extends RuntimeException{
    public JsonValidationException(String message) {
        super(message);
    }
}
