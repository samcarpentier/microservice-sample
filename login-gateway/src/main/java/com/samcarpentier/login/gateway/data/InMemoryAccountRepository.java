package com.samcarpentier.login.gateway.data;

import java.util.Map;
import java.util.Optional;

import com.google.common.collect.Maps;
import com.samcarpentier.login.gateway.data.entity.AccountDto;
import com.samcarpentier.login.gateway.data.entity.AccountDtoAssembler;
import com.samcarpentier.login.gateway.domain.AccountRepository;
import com.samcarpentier.login.gateway.domain.entity.Account;

public class InMemoryAccountRepository
  implements
    AccountRepository,
    DevelopmentDataSupplier<AccountDto>
{

  private final AccountDtoAssembler accountDtoAssembler;
  private final Map<String, AccountDto> usernameToAccountAssociations;

  public InMemoryAccountRepository(AccountDtoAssembler accountDtoAssembler) {
    this.accountDtoAssembler = accountDtoAssembler;
    this.usernameToAccountAssociations = createDatabaseContainer();
  }

  @Override
  public Account findByUsername(String username) {
    return Optional.ofNullable(usernameToAccountAssociations.get(username))
                   .map(accountDtoAssembler::assemble)
                   .orElse(null);
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
