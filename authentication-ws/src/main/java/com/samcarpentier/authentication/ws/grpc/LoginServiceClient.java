package com.samcarpentier.authentication.ws.grpc;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.samcarpentier.authentication.ws.grpc.name.resolver.StaticAddressNameResolverFactory;
import com.samcarpentier.login.gateway.LoginRequest;
import com.samcarpentier.login.gateway.LoginResponse;
import com.samcarpentier.login.gateway.LoginServiceGrpc;
import com.samcarpentier.login.gateway.LoginServiceGrpc.LoginServiceBlockingStub;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import io.grpc.util.RoundRobinLoadBalancerFactory;

public class LoginServiceClient {

  private static final Logger logger = LoggerFactory.getLogger(LoginServiceClient.class);

  private final String ipAddress;
  private final List<InetSocketAddress> staticServerAddresses;

  private LoginServiceBlockingStub loginServiceBlockingStub;

  public LoginServiceClient(String ipAddress, Set<Integer> ports) {
    this.ipAddress = ipAddress;
    this.staticServerAddresses = ports.stream()
                                      .map(this::socketAddressFromPort)
                                      .collect(Collectors.toList());
  }

  public void authenticate(String username, String password) throws Throwable {
    ManagedChannel channel = ManagedChannelBuilder.forTarget(ipAddress)
                                                  .usePlaintext(true)
                                                  .nameResolverFactory(new StaticAddressNameResolverFactory(staticServerAddresses))
                                                  .loadBalancerFactory(RoundRobinLoadBalancerFactory.getInstance())
                                                  .build();

    loginServiceBlockingStub = LoginServiceGrpc.newBlockingStub(channel);
    attemptLogin(username, password);

    channel.shutdownNow();
  }

  private InetSocketAddress socketAddressFromPort(int port) {
    return new InetSocketAddress(ipAddress, port);
  }

  private LoginResponse attemptLogin(String username, String password) throws Throwable {
    LoginResponse loginResponse = null;

    try {
      loginResponse = loginServiceBlockingStub.login(LoginRequest.newBuilder()
                                                                 .setUsername(username)
                                                                 .setPassword(password)
                                                                 .build());
    } catch (StatusRuntimeException e) {
      logger.warn(String.format("Login failed: %s", e.getStatus().getCode()));
    }

    logger.debug(String.format("Phone numbers: %s", loginResponse.getPhoneNumbersList()));
    return loginResponse;
  }

}
