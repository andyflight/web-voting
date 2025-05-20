package com.example.webvoting.exceptions;

import jakarta.ejb.ApplicationException;

@ApplicationException(rollback = true)
public class UserHasAlreadyVotedException extends RuntimeException {
    public UserHasAlreadyVotedException(String message) {
        super(message);
    }
}
