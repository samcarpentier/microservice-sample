package com.samcarpentier.login.gateway;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.samcarpentier.login.gateway.application.LoginApplicationService;
import com.samcarpentier.login.gateway.domain.entity.Account;
import com.samcarpentier.login.gateway.domain.exception.AccountNotFoundException;
import com.samcarpentier.login.gateway.domain.exception.WrongPasswordException;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;

public class LoginService extends LoginServiceGrpc.LoginServiceImplBase {

  private static final Logger logger = LoggerFactory.getLogger(LoginService.class);

  private final LoginApplicationService loginApplicationService;

  public LoginService(LoginApplicationService loginApplicationService) {
    this.loginApplicationService = loginApplicationService;
  }

  @Override
  public void login(LoginRequest request, StreamObserver<LoginResponse> responseObserver) {
    Optional<Account> account = retrieveAccount(request.getUsername(),
                                                request.getPassword(),
                                                responseObserver);

    if (account.isPresent()) {
      logger.info(String.format("Account with username [%s] has phone numbers: %s",
                                request.getUsername(),
                                account.get().getPhoneNumbers()));
      responseObserver.onNext(LoginResponse.newBuilder()
                                           .addAllPhoneNumbers(account.get().getPhoneNumbers())
                                           .build());
      responseObserver.onCompleted();
    }
  }

  private Optional<Account> retrieveAccount(String username,
                                            String password,
                                            StreamObserver<LoginResponse> responseObserver)
  {
    Optional<Account> account = Optional.empty();

    try {
      account = Optional.of(loginApplicationService.login(username, password));
    } catch (AccountNotFoundException e) {
      logger.warn(e.getMessage());
      responseObserver.onError(Status.NOT_FOUND.withDescription(e.getMessage())
                                               .withCause(e)
                                               .asException());
    } catch (WrongPasswordException e) {
      logger.warn(e.getMessage());
      responseObserver.onError(Status.PERMISSION_DENIED.withDescription(e.getMessage())
                                                       .withCause(e)
                                                       .asException());
    }

    return account;
  }

}
