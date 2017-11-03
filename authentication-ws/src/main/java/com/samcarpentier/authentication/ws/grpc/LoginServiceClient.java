package com.samcarpentier.authentication.ws.grpc;

import java.net.InetSocketAddress;
import java.util.Set;
import java.util.stream.Collectors;

import com.samcarpentier.authentication.ws.grpc.name.resolver.StaticResolver;
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
                                                  .nameResolverFactory(StaticResolver.factory(ports.stream()
                                                                                                   .map(port -> new InetSocketAddress(ipAddress,
                                                                                                                                      port))
                                                                                                   .collect(Collectors.toList())))
                                                  .loadBalancerFactory(RoundRobinLoadBalancerFactory.getInstance())
                                                  .build();

    loginServiceBlockingStub = LoginServiceGrpc.newBlockingStub(channel);
    attemptLogin();
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
