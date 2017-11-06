package com.samcarpentier.login.gateway.data.development;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.samcarpentier.login.gateway.data.InMemoryAccountRepository;
import com.samcarpentier.login.gateway.domain.AccountRepository;
import com.samcarpentier.login.gateway.domain.entity.Account;

@RunWith(MockitoJUnitRunner.class)
public class AccountDevDataAbstractFactoryTest {

  private static final AccountRepository NULL_ACCOUNT_REPOSITORY = null;

  private AccountDevDataAbstractFactory accountDevDataAbstractFactory;

  @Mock
  private InMemoryAccountDevDataFactory inMemoryDevDataFactory;

  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  @Before
  public void setUp() {
    accountDevDataAbstractFactory = new AccountDevDataAbstractFactory() {
      @Override
      protected InMemoryAccountDevDataFactory instantiateInMemoryDevDataFactory(InMemoryAccountRepository inMemoryAccountRepository) {
        return inMemoryDevDataFactory;
      }
    };
  }

  @Test
  public void givenInMemoryAccountRepository_whenCreateDevelopmentData_thenCreateDevelopmentData() {
    InMemoryAccountRepository inMemoryAccountRepository = mock(InMemoryAccountRepository.class);
    accountDevDataAbstractFactory.createDevelopmentData(inMemoryAccountRepository);
    verify(inMemoryDevDataFactory).createDevelopmentData();
  }

  @Test
  public void givenNullAccountRepository_whenCreateDevelopmentData_thenThrowIllegalArgumentException() {
    expectedException.expect(IllegalArgumentException.class);
    accountDevDataAbstractFactory.createDevelopmentData(NULL_ACCOUNT_REPOSITORY);
  }

  @Test
  public void givenUnknownAccountRepository_whenCreateDevelopmentData_thenThrowIllegalArgumentException() {
    expectedException.expect(IllegalArgumentException.class);
    UnknownAccoutRepository unknownAccoutRepository = mock(UnknownAccoutRepository.class);
    accountDevDataAbstractFactory.createDevelopmentData(unknownAccoutRepository);
  }

  private class UnknownAccoutRepository implements AccountRepository {

    @Override
    public Account findByUsername(String username) {
      return mock(Account.class);
    }
  }

}
