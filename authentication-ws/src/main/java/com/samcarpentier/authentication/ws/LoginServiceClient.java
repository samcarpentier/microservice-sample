package com.samcarpentier.authentication.ws;

import com.samcarpentier.login.gateway.LoginRequest;
import com.samcarpentier.login.gateway.LoginResponse;
import com.samcarpentier.login.gateway.LoginServiceGrpc;
import com.samcarpentier.login.gateway.LoginServiceGrpc.LoginServiceBlockingStub;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;

public class LoginServiceClient {

  private final String ipAddress;
  private final int port;

  private LoginServiceBlockingStub loginServiceBlockingStub;

  public LoginServiceClient(String ipAddress, int port) {
    this.ipAddress = ipAddress;
    this.port = port;
  }

  public void authenticate() throws Throwable {
    ManagedChannel channel = ManagedChannelBuilder.forAddress(ipAddress, port)
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
      return null;
    }

    System.out.println(loginResponse);

    return loginResponse;
  }

}
