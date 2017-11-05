package com.samcarpentier.login.gateway;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.samcarpentier.login.gateway.application.LoginApplicationService;
import com.samcarpentier.login.gateway.data.InMemoryAccountRepository;
import com.samcarpentier.login.gateway.data.development.AccountDevDataAbstractFactory;
import com.samcarpentier.login.gateway.data.entity.AccountDtoAssembler;
import com.samcarpentier.login.gateway.grpc.LoginService;

import io.grpc.Server;
import io.grpc.ServerBuilder;

public class LoginGatewayServer {

  private static final Logger logger = LoggerFactory.getLogger(LoginGatewayServer.class);
  private static final int DEFAULT_PORT = 8080;

  public static void main(String[] args) throws InterruptedException, IOException {
    int httpPort = selectHttpPort();
    Server server = ServerBuilder.forPort(httpPort).addService(createLoginService()).build();

    server.start();
    logger.info(String.format("Server started on port %s", httpPort));
    server.awaitTermination();
  }

  private static LoginService createLoginService() {
    InMemoryAccountRepository inMemoryAccountRepository = new InMemoryAccountRepository(new AccountDtoAssembler());

    if (isDevelopmentExecution()) {
      System.out.println("Development execution detected. Provisioning dev data");
      new AccountDevDataAbstractFactory().createDevelopmentData(inMemoryAccountRepository);
    }

    LoginApplicationService loginApplicationService = new LoginApplicationService(inMemoryAccountRepository);
    return new LoginService(loginApplicationService);
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
