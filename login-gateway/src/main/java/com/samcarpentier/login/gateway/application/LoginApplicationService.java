package com.samcarpentier.login.gateway.application;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.samcarpentier.login.gateway.domain.AccountRepository;
import com.samcarpentier.login.gateway.domain.entity.Account;
import com.samcarpentier.login.gateway.domain.exception.AccountNotFoundException;
import com.samcarpentier.login.gateway.domain.exception.WrongPasswordException;

public class LoginApplicationService {

  private static final Logger logger = LoggerFactory.getLogger(LoginApplicationService.class);

  private final AccountRepository accountRepository;

  public LoginApplicationService(AccountRepository accountRepository) {
    this.accountRepository = accountRepository;
  }

  public Account login(String username, String password)
    throws AccountNotFoundException,
      WrongPasswordException
  {
    Account account = retrieveAccountWithUsername(username);

    if (isPasswordValid(account.getPassword(), password)) {
      return account;
    }

    String message = String.format("Wrong password for username [%s]", username);
    logger.debug(message);

    throw new WrongPasswordException(message);
  }

  private Account retrieveAccountWithUsername(String username) throws AccountNotFoundException {
    return Optional.ofNullable(accountRepository.findByUsername(username)).orElseThrow(() -> {
      String message = String.format("No account matching username [%s]", username);
      logger.debug(message);

      return new AccountNotFoundException(message);
    });
  }

  private boolean isPasswordValid(String accountPassword, String requestPassword) {
    return accountPassword.equals(requestPassword);
  }

}
