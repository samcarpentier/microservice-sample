package com.samcarpentier.login.gateway.domain;

import java.util.Optional;

import com.samcarpentier.login.gateway.LoginRequest;
import com.samcarpentier.login.gateway.LoginResponse;
import com.samcarpentier.login.gateway.LoginServiceGrpc;
import com.samcarpentier.login.gateway.data.exception.AccountNotFoundException;
import com.samcarpentier.login.gateway.data.exception.WrongUsernamePasswordException;
import com.samcarpentier.login.gateway.domain.entity.Account;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;

public class LoginService extends LoginServiceGrpc.LoginServiceImplBase {

  private final AccountRepository accountRepository;

  public LoginService(AccountRepository accountRepository) {
    this.accountRepository = accountRepository;
  }

  @Override
  public void authenticate(LoginRequest request, StreamObserver<LoginResponse> responseObserver) {
    Optional<Account> account = retrieveAccount(request, responseObserver);

    if (account.isPresent()) {
      responseObserver.onNext(LoginResponse.newBuilder()
                                           .addAllPhoneNumbers(account.get().getPhoneNumbers())
                                           .build());
      responseObserver.onCompleted();
    }
  }

  private Optional<Account> retrieveAccount(LoginRequest request,
                                            StreamObserver<LoginResponse> responseObserver)
  {
    Optional<Account> account = Optional.empty();

    try {
      account = Optional.of(accountRepository.login(request.getUsername(), request.getPassword()));
    } catch (AccountNotFoundException e) {
      responseObserver.onError(Status.NOT_FOUND.withDescription(e.getMessage())
                                               .withCause(e)
                                               .asException());
    } catch (WrongUsernamePasswordException e) {
      responseObserver.onError(Status.PERMISSION_DENIED.withDescription(e.getMessage())
                                                       .withCause(e)
                                                       .asException());
    }

    return account;
  }

}
