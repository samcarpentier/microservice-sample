package com.samcarpentier.login.gateway.data;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.BDDMockito.given;

import java.util.Collections;
import java.util.Map;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.common.collect.Maps;
import com.samcarpentier.login.gateway.data.entity.AccountDto;
import com.samcarpentier.login.gateway.data.entity.AccountDtoAssembler;
import com.samcarpentier.login.gateway.data.exception.AccountNotFoundException;
import com.samcarpentier.login.gateway.data.exception.WrongUsernamePasswordException;
import com.samcarpentier.login.gateway.domain.entity.Account;

@RunWith(MockitoJUnitRunner.class)
public class InMemoryAccountRepositoryTest {

  private static final String USERNAME = "username";
  private static final String PASSWORD = "password";
  private static final String WRONG_PASSWORD = "wrongPassword";

  private InMemoryAccountRepository inMemoryAccountRepository;
  private Map<String, AccountDto> usernameToAccountAssociations;

  @Mock
  private AccountDtoAssembler accountDtoAssembler;

  @Mock
  private Account account;
  @Mock
  private AccountDto accountDto;

  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  @Before
  public void setUp() {
    usernameToAccountAssociations = Maps.newHashMap();
    given(accountDtoAssembler.assembleDto(account)).willReturn(accountDto);

    inMemoryAccountRepository = new InMemoryAccountRepository(accountDtoAssembler) {
      @Override
      protected Map<String, AccountDto> createDatabaseContainer() {
        return usernameToAccountAssociations;
      }
    };
  }

  @Test
  public void givenSuccessfulLogin_whenLogin_thenReturnAssociatedAccount() throws Exception {
    usernameToAccountAssociations.put(USERNAME, accountDto);
    given(account.getPassword()).willReturn(PASSWORD);
    given(accountDtoAssembler.assemble(accountDto)).willReturn(account);

    Account retrievedAccount = inMemoryAccountRepository.login(USERNAME, PASSWORD);

    assertThat(retrievedAccount).isEqualTo(account);
  }

  @Test
  public void givenNoAccountAssociatedWithUsername_thenLogin_thenThrowAccountNotFoundException()
    throws Exception
  {
    expectedException.expect(AccountNotFoundException.class);
    inMemoryAccountRepository.login(USERNAME, PASSWORD);
  }

  @Test
  public void givenWrongPasswordForAccount_whenLogin_thenThrowWrongUsernamePasswordException()
    throws Exception
  {
    expectedException.expect(WrongUsernamePasswordException.class);
    usernameToAccountAssociations.put(USERNAME, accountDto);
    given(account.getPassword()).willReturn(PASSWORD);
    given(accountDtoAssembler.assemble(accountDto)).willReturn(account);

    inMemoryAccountRepository.login(USERNAME, WRONG_PASSWORD);
  }

  @Test
  public void givenAccountDto_whenDirectSave_thenAccountDtoIsCorrectlyPersisted() {
    accountDto = new AccountDto(USERNAME, PASSWORD, Collections.emptyList());
    inMemoryAccountRepository.directSave(accountDto);
    assertThat(usernameToAccountAssociations).containsEntry(USERNAME, accountDto);
  }

}
