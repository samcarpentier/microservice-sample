package com.samcarpentier.login.gateway.data;

import com.google.common.collect.Lists;
import com.samcarpentier.login.gateway.data.entity.AccountDto;

public class AccountDevelopmentInMemoryDataFactory {

  private final InMemoryAccountRepository inMemoryAccountRepository;

  public AccountDevelopmentInMemoryDataFactory(InMemoryAccountRepository inMemoryAccountRepository) {
    this.inMemoryAccountRepository = inMemoryAccountRepository;
  }

  public void createDevelopmentData() {
    AccountDto account1 = new AccountDto("username1",
                                         "password1",
                                         Lists.newArrayList("pn1", "pn2", "pn3"));
    AccountDto account2 = new AccountDto("admin", "admin", Lists.newArrayList("pn1"));
    AccountDto account3 = new AccountDto("empty", "empty", Lists.newArrayList());

    inMemoryAccountRepository.directSave(account1);
    inMemoryAccountRepository.directSave(account2);
    inMemoryAccountRepository.directSave(account3);
  }

}
