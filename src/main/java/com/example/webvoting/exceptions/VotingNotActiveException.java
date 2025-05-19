package com.example.webvoting.exceptions;

public class VotingNotActiveException extends RuntimeException {
  public VotingNotActiveException(String message) {
    super(message);
  }
}
