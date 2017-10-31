package com.samcarpentier.authentication.ws;

import com.samcarpentier.login.gateway.LoginRequest;
import com.samcarpentier.login.gateway.LoginResponse;
import com.samcarpentier.login.gateway.LoginServiceGrpc;
import com.samcarpentier.login.gateway.LoginServiceGrpc.LoginServiceBlockingStub;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;

public class LoginServiceClient {

  private LoginServiceBlockingStub loginServiceBlockingStub;

  public void authenticate() throws Throwable {
    ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 8080)
                                                  .usePlaintext(true)
                                                  .build();

    loginServiceBlockingStub = LoginServiceGrpc.newBlockingStub(channel);
    attemptLogin();

    channel.shutdown();
  }

  private LoginResponse attemptLogin() throws Throwable {
    LoginResponse loginResponse;

    try {
      loginResponse = loginServiceBlockingStub.authenticate(LoginRequest.newBuilder()
                                                                        .setUsername("username1")
                                                                        .setPassword("password1")
                                                                        .build());
    } catch (StatusRuntimeException e) {
      System.out.println(e.getStatus());
      throw e;
    }

    System.out.println(loginResponse);

    return loginResponse;
  }

}
