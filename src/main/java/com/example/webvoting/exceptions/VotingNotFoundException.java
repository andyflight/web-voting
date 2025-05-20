package com.example.webvoting.exceptions;

import jakarta.ejb.ApplicationException;

@ApplicationException(rollback = true)
public class VotingNotFoundException extends RuntimeException {
    public VotingNotFoundException(String message) {
        super(message);
    }
}
