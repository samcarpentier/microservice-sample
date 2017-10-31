package com.samcarpentier.login.gateway;

import java.io.IOException;

import com.samcarpentier.login.gateway.data.AccountDevelopmentInMemoryDataFactory;
import com.samcarpentier.login.gateway.data.InMemoryAccountRepository;
import com.samcarpentier.login.gateway.data.entity.AccountDtoAssembler;
import com.samcarpentier.login.gateway.domain.LoginService;

import io.grpc.Server;
import io.grpc.ServerBuilder;

public class LoginGatewayServer {

  private static final int DEFAULT_PORT = 8080;

  public static void main(String[] args) throws InterruptedException, IOException {
    Server server = ServerBuilder.forPort(selectHttpPort())
                                 .addService(createLoginService())
                                 .build();

    server.start();
    System.out.println("Server started on port 8080");
    server.awaitTermination();
  }

  private static LoginService createLoginService() {
    InMemoryAccountRepository inMemoryAccountRepository = new InMemoryAccountRepository(new AccountDtoAssembler());

    if (isDevelopmentExecution()) {
      System.out.println("Development execution detected. Provisioning dev data");
      new AccountDevelopmentInMemoryDataFactory(inMemoryAccountRepository).createDevelopmentData();
    }

    return new LoginService(inMemoryAccountRepository);
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

  private static boolean isDevelopmentExecution() {
    return Boolean.parseBoolean(System.getProperty("dev"));
  }

}
