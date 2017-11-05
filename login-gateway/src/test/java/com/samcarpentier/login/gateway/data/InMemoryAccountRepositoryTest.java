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
import com.samcarpentier.login.gateway.domain.entity.Account;

@RunWith(MockitoJUnitRunner.class)
public class InMemoryAccountRepositoryTest {

  private static final String USERNAME = "username";
  private static final String PASSWORD = "password";

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
  public void givenExistingUsername_whenFindByUsername_thenReturnAssociatedAccount()
    throws Exception
  {
    usernameToAccountAssociations.put(USERNAME, accountDto);
    given(accountDtoAssembler.assemble(accountDto)).willReturn(account);

    Account retrievedAccount = inMemoryAccountRepository.findByUsername(USERNAME);

    assertThat(retrievedAccount).isEqualTo(account);
  }

  @Test
  public void givenNonExistingUsername_whenFindByUsername_thenReturnNull() throws Exception {
    Account retrievedAccount = inMemoryAccountRepository.findByUsername(USERNAME);

    assertThat(retrievedAccount).isNull();
  }

  @Test
  public void givenAccountDto_whenDirectSave_thenAccountDtoIsCorrectlyPersisted() {
    accountDto = new AccountDto(USERNAME, PASSWORD, Collections.emptyList());
    inMemoryAccountRepository.directSave(accountDto);
    assertThat(usernameToAccountAssociations).containsEntry(USERNAME, accountDto);
  }

}
