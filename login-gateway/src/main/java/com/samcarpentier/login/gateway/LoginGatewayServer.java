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
    int port = selectPort();
    Server server = ServerBuilder.forPort(port).addService(createLoginService()).build();

    server.start();
    logger.info(String.format("Server started on port %s", port));
    server.awaitTermination();
  }

  private static LoginService createLoginService() {
    InMemoryAccountRepository inMemoryAccountRepository = new InMemoryAccountRepository(new AccountDtoAssembler());

    if (isDevelopmentExecution()) {
      logger.debug("Development execution detected. Provisioning dev data");
      new AccountDevDataAbstractFactory().createDevelopmentData(inMemoryAccountRepository);
    }

    LoginApplicationService loginApplicationService = new LoginApplicationService(inMemoryAccountRepository);
    return new LoginService(loginApplicationService);
  }

  private static int selectPort() {
    try {
      return Integer.parseInt(System.getProperty("port"));
    } catch (NumberFormatException e) {
      logger.debug(String.format("Could not retrieve JVM argument [-Dport]. Using default port %s",
                                 DEFAULT_PORT));
      return DEFAULT_PORT;
    }
  }

  private static boolean isDevelopmentExecution() {
    return Boolean.parseBoolean(System.getProperty("dev"));
  }

}
