package com.samcarpentier.login.gateway.domain;

import com.samcarpentier.login.gateway.LoginRequest;
import com.samcarpentier.login.gateway.LoginResponse;
import com.samcarpentier.login.gateway.LoginServiceGrpc;
import com.samcarpentier.login.gateway.domain.entity.Account;

import io.grpc.Status;
import io.grpc.StatusException;
import io.grpc.stub.StreamObserver;

public class LoginService extends LoginServiceGrpc.LoginServiceImplBase {

  private final AccountRepository accountRepository;

  public LoginService(AccountRepository accountRepository) {
    this.accountRepository = accountRepository;
  }

  @Override
  public void authenticate(LoginRequest request, StreamObserver<LoginResponse> responseObserver) {
    Account account = accountRepository.login(request.getUsername(), request.getPassword());

    if (account == null) {
      responseObserver.onError(new StatusException(Status.fromThrowable(new Exception("Not found"))));
    } else {
      responseObserver.onNext(LoginResponse.newBuilder()
                                           .addAllPhoneNumbers(account.getPhoneNumbers())
                                           .build());
      responseObserver.onCompleted();
    }

  }

}
