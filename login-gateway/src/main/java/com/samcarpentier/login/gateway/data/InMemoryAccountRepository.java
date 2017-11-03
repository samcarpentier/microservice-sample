package com.samcarpentier.login.gateway.data;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Optional;
import com.google.common.collect.Maps;
import com.samcarpentier.login.gateway.data.entity.AccountDto;
import com.samcarpentier.login.gateway.data.entity.AccountDtoAssembler;
import com.samcarpentier.login.gateway.data.exception.AccountNotFoundException;
import com.samcarpentier.login.gateway.data.exception.WrongUsernamePasswordException;
import com.samcarpentier.login.gateway.domain.AccountRepository;
import com.samcarpentier.login.gateway.domain.entity.Account;

public class InMemoryAccountRepository
  implements
    AccountRepository,
    DevelopmentDataSupplier<AccountDto>
{

  private static final Logger logger = LoggerFactory.getLogger(InMemoryAccountRepository.class);

  private final AccountDtoAssembler accountDtoAssembler;
  private final Map<String, AccountDto> usernameToAccountAssociations;

  public InMemoryAccountRepository(AccountDtoAssembler accountDtoAssembler) {
    this.accountDtoAssembler = accountDtoAssembler;
    this.usernameToAccountAssociations = createDatabaseContainer();
  }

  @Override
  public Account login(String username, String password)
    throws WrongUsernamePasswordException,
      AccountNotFoundException
  {
    Optional<Account> account = Optional.fromNullable(usernameToAccountAssociations.get(username))
                                        .transform(accountDtoAssembler::assemble);

    if (!account.isPresent()) {
      String message = String.format("No account matching username [%s]", username);
      logger.debug(message);
      throw new AccountNotFoundException(message);
    }

    boolean isPasswordMatching = account.get().getPassword().equals(password);
    if (isPasswordMatching) {
      return account.get();
    }

    String message = String.format("Wrong password for username [%s]", username);
    logger.debug(message);
    throw new WrongUsernamePasswordException(message);
  }

  @Override
  public void directSave(AccountDto accountDto) {
    usernameToAccountAssociations.put(accountDto.username, accountDto);
  }

  // For testing purposes
  protected Map<String, AccountDto> createDatabaseContainer() {
    return Maps.newHashMap();
  }

}
