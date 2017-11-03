package com.samcarpentier.login.gateway.data.exception;

public class WrongUsernamePasswordException extends Exception {

  private static final long serialVersionUID = -9048674735870122621L;

  public WrongUsernamePasswordException(String message) {
    super(message);
  }

}
