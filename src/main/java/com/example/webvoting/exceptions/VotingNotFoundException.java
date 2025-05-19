package com.example.webvoting.exceptions;

public class VotingNotFoundException extends RuntimeException {
    public VotingNotFoundException(String message) {
        super(message);
    }
}
