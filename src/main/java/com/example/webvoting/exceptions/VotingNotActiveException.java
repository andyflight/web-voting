package com.example.webvoting.exceptions;

import jakarta.ejb.ApplicationException;

@ApplicationException(rollback = true)
public class VotingNotActiveException extends RuntimeException {
  public VotingNotActiveException(String message) {
    super(message);
  }
}
