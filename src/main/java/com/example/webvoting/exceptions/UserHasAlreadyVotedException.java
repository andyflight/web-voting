package com.example.webvoting.exceptions;

public class UserHasAlreadyVotedException extends RuntimeException {
    public UserHasAlreadyVotedException(String message) {
        super(message);
    }
}
