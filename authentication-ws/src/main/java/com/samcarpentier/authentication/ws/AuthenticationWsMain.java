package com.samcarpentier.authentication.ws;

public class AuthenticationWsMain {

  private static final int DEFAULT_PORT = 8080;

  public static void main(String[] args) throws Throwable {
    LoginServiceClient loginServiceClient = new LoginServiceClient("localhost", selectHttpPort());
    loginServiceClient.authenticate();
  }

  private static int selectHttpPort() {
    try {
      return Integer.parseInt(System.getProperty("port"));
    } catch (NumberFormatException e) {
      System.out.println(String.format("Could not retrieve JVM argument [-Dport]. Using default port %s",
                                       DEFAULT_PORT));
      return DEFAULT_PORT;
    }
  }

}
