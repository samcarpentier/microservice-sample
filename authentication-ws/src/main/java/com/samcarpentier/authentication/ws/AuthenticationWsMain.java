package com.samcarpentier.authentication.ws;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.collect.Sets;
import com.samcarpentier.authentication.ws.grpc.LoginServiceClient;

public class AuthenticationWsMain {

  private static final int DEFAULT_PORT = 8080;

  public static void main(String[] args) throws Throwable {
    LoginServiceClient loginServiceClient = new LoginServiceClient("127.0.0.1", selectHttpPort());
    loginServiceClient.authenticate();
  }

  private static Set<Integer> selectHttpPort() {

    try {
      return Arrays.asList(System.getProperty("ports").split(","))
                   .stream()
                   .map(Integer::parseInt)
                   .collect(Collectors.toSet());
    } catch (NumberFormatException | NullPointerException e) {
      System.out.println(String.format("Could not retrieve JVM argument [-Dports]. Using default port %s",
                                       DEFAULT_PORT));
      return Sets.newHashSet(DEFAULT_PORT);
    }
  }

}
