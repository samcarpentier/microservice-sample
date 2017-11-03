package com.samcarpentier.login.gateway.data.entity;

import com.samcarpentier.login.gateway.domain.entity.Account;

public class AccountDtoAssembler {

  public AccountDto assembleDto(Account account) {
    return new AccountDto(account.getUsername(), account.getPassword(), account.getPhoneNumbers());
  }

  public Account assemble(AccountDto accountDto) {
    return new Account(accountDto.username, accountDto.password, accountDto.phoneNumbers);
  }

}
