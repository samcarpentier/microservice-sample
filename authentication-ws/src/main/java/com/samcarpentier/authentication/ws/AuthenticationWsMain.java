package com.samcarpentier.authentication.ws;

public class AuthenticationWsMain {

  public static void main(String[] args) throws Throwable {
    LoginServiceClient loginServiceClient = new LoginServiceClient();
    loginServiceClient.authenticate();
  }

}
