package com.samcarpentier.authentication.ws;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Sets;
import com.samcarpentier.authentication.ws.grpc.LoginServiceClient;

public class AuthenticationWsMain {

  private static final Logger logger = LoggerFactory.getLogger(AuthenticationWsMain.class);
  private static final int DEFAULT_PORT = 8080;

  public static void main(String[] args) throws Throwable {
    Set<Integer> ports = selectPorts();
    LoginServiceClient loginServiceClient = new LoginServiceClient("127.0.0.1", ports);

    loginServiceClient.authenticate("username1", "password1");
  }

  private static Set<Integer> selectPorts() {
    try {
      return Arrays.asList(System.getProperty("ports").split(","))
                   .stream()
                   .map(Integer::parseInt)
                   .collect(Collectors.toSet());
    } catch (NumberFormatException | NullPointerException e) {
      logger.debug(String.format("Could not retrieve JVM argument [-Dports]. Using default port %s",
                                 DEFAULT_PORT));
      return Sets.newHashSet(DEFAULT_PORT);
    }
  }

}
