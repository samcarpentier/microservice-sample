package com.samcarpentier.login.gateway.data;

import java.util.Map;

import com.google.common.base.Optional;
import com.google.common.collect.Maps;
import com.samcarpentier.login.gateway.data.entity.AccountDto;
import com.samcarpentier.login.gateway.data.entity.AccountDtoAssembler;
import com.samcarpentier.login.gateway.domain.AccountRepository;
import com.samcarpentier.login.gateway.domain.entity.Account;

public class InMemoryAccountRepository implements AccountRepository {

  private final AccountDtoAssembler accountDtoAssembler;
  private final Map<String, AccountDto> usernameToAccountAssociations;

  public InMemoryAccountRepository(AccountDtoAssembler accountDtoAssembler) {
    this.accountDtoAssembler = accountDtoAssembler;
    this.usernameToAccountAssociations = Maps.newHashMap();
  }

  @Override
  public Account login(String username, String password) {
    Optional<Account> account = Optional.fromNullable(usernameToAccountAssociations.get(username))
                                        .transform(accountDtoAssembler::assemble);

    if (!account.isPresent()) {
      System.out.println(String.format("No account matching username [%s]", username));
      return null;
    }

    boolean isPasswordMatching = account.get().getPassword().equals(password);
    if (isPasswordMatching) {
      return account.get();
    }

    System.out.println(String.format("Wrong password for username [%s]", username));
    return null;
  }

  public void directSave(AccountDto accountDto) {
    usernameToAccountAssociations.put(accountDto.username, accountDto);
  }

}
