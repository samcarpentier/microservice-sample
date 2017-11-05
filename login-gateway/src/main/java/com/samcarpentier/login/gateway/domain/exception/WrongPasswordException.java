package com.samcarpentier.login.gateway.domain.exception;

public class WrongPasswordException extends Exception {

  private static final long serialVersionUID = -9048674735870122621L;

  public WrongPasswordException(String message) {
    super(message);
  }

}
