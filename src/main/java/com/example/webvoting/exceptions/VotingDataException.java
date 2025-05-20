package com.example.webvoting.exceptions;

import jakarta.ejb.ApplicationException;

@ApplicationException(rollback = true)
public class VotingDataException extends RuntimeException {
    public VotingDataException(String message) {
        super(message);
    }
}
