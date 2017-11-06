package com.samcarpentier.login.gateway.data.development;

import com.google.common.collect.Lists;
import com.samcarpentier.login.gateway.data.InMemoryAccountRepository;
import com.samcarpentier.login.gateway.data.entity.AccountDto;

public class InMemoryAccountDevDataFactory {

  private final InMemoryAccountRepository inMemoryAccountRepository;

  public InMemoryAccountDevDataFactory(InMemoryAccountRepository inMemoryAccountRepository) {
    this.inMemoryAccountRepository = inMemoryAccountRepository;
  }

  public void createDevelopmentData() {
    AccountDto account1 = new AccountDto("username1",
                                         "password1",
                                         Lists.newArrayList("1112223333",
                                                            "4445556666",
                                                            "7778889999"));
    AccountDto account2 = new AccountDto("admin", "admin", Lists.newArrayList("1115557777"));
    AccountDto account3 = new AccountDto("empty", "empty", Lists.newArrayList());

    inMemoryAccountRepository.directSave(account1);
    inMemoryAccountRepository.directSave(account2);
    inMemoryAccountRepository.directSave(account3);
  }

}
