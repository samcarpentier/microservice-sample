package com.samcarpentier.login.gateway.grpc.error;

import com.samcarpentier.login.gateway.domain.exception.AccountNotFoundException;
import com.samcarpentier.login.gateway.domain.exception.WrongPasswordException;

import io.grpc.Status;
import io.grpc.StatusException;

public class DomainErrorHandler {

  public static StatusException handle(Exception domainException) {
    if (domainException instanceof AccountNotFoundException) {
      return createStatusExceptionFor(Status.NOT_FOUND, domainException);
    }

    if (domainException instanceof WrongPasswordException) {
      return createStatusExceptionFor(Status.PERMISSION_DENIED, domainException);
    }

    return createStatusExceptionFor(Status.INTERNAL, domainException);
  }

  private static StatusException createStatusExceptionFor(Status status,
                                                          Exception domainException)
  {
    return status.withDescription(domainException.getMessage())
                 .withCause(domainException)
                 .asException();
  }

}
