package com.samcarpentier.login.gateway.data.development;

import com.google.common.base.Preconditions;
import com.samcarpentier.login.gateway.data.InMemoryAccountRepository;
import com.samcarpentier.login.gateway.domain.AccountRepository;

public class AccountDevDataAbstractFactory {

  public void createDevelopmentData(AccountRepository accountRepository) {
    Preconditions.checkArgument(accountRepository != null, "accountRepository must be supplied");

    if (accountRepository instanceof InMemoryAccountRepository) {
      instantiateInMemoryDevDataFactory((InMemoryAccountRepository) accountRepository).createDevelopmentData();
    } else {
      throw new IllegalArgumentException(String.format("No development data factory for provided repository implementation: [%s]",
                                                       accountRepository.getClass()));
    }
  }

  protected AccountInMemoryDevDataFactory instantiateInMemoryDevDataFactory(InMemoryAccountRepository inMemoryAccountRepository) {
    return new AccountInMemoryDevDataFactory(inMemoryAccountRepository);
  }

}
