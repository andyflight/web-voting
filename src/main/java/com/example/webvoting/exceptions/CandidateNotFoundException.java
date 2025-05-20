package com.example.webvoting.exceptions;

import jakarta.ejb.ApplicationException;

@ApplicationException(rollback = true)
public class CandidateNotFoundException extends RuntimeException {
    public CandidateNotFoundException(String message) {
        super(message);
    }
}
