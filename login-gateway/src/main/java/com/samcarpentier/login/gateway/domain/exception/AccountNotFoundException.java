package com.samcarpentier.login.gateway.domain.exception;

public class AccountNotFoundException extends Exception {

  private static final long serialVersionUID = 9164669798339648728L;

  public AccountNotFoundException(String message) {
    super(message);
  }

}
