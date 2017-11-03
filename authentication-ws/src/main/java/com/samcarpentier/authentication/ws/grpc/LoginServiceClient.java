package com.samcarpentier.authentication.ws.grpc;

import java.util.Set;

import com.samcarpentier.authentication.ws.grpc.name.resolver.CustomNameResolverProvider;
import com.samcarpentier.login.gateway.LoginRequest;
import com.samcarpentier.login.gateway.LoginResponse;
import com.samcarpentier.login.gateway.LoginServiceGrpc;
import com.samcarpentier.login.gateway.LoginServiceGrpc.LoginServiceBlockingStub;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import io.grpc.util.RoundRobinLoadBalancerFactory;

public class LoginServiceClient {

  private final String ipAddress;
  private final Set<Integer> ports;

  private LoginServiceBlockingStub loginServiceBlockingStub;

  public LoginServiceClient(String ipAddress, Set<Integer> ports) {
    this.ipAddress = ipAddress;
    this.ports = ports;
  }

  public void authenticate() throws Throwable {
    ManagedChannel channel = ManagedChannelBuilder.forTarget(ipAddress)
                                                  .usePlaintext(true)
                                                  .nameResolverFactory(new CustomNameResolverProvider().withPorts(ports))
                                                  .loadBalancerFactory(RoundRobinLoadBalancerFactory.getInstance())
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
