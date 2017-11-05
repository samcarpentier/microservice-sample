package com.samcarpentier.login.gateway.application;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.BDDMockito.given;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.samcarpentier.login.gateway.domain.AccountRepository;
import com.samcarpentier.login.gateway.domain.entity.Account;
import com.samcarpentier.login.gateway.domain.exception.AccountNotFoundException;
import com.samcarpentier.login.gateway.domain.exception.WrongPasswordException;

@RunWith(MockitoJUnitRunner.class)
public class LoginApplicationServiceTest {

  private static final String USERNAME = "username";
  private static final String PASSWORD = "password";
  private static final String WRONG_PASSWORD = "wrongPassword";
  private static final Account NO_ACCOUNT = null;

  private LoginApplicationService loginApplicationService;

  @Mock
  private AccountRepository accountRepository;

  @Mock
  private Account account;

  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  @Before
  public void setUp() {
    loginApplicationService = new LoginApplicationService(accountRepository);
  }

  @Test
  public void givenMatchingUsernamePassword_whenLogin_thenReturnAccountAssociatedWithUsername()
    throws Exception
  {
    given(accountRepository.findByUsername(USERNAME)).willReturn(account);
    given(account.getPassword()).willReturn(PASSWORD);

    Account retrievedAccount = loginApplicationService.login(USERNAME, PASSWORD);

    assertThat(retrievedAccount).isEqualTo(account);
  }

  @Test
  public void givenUnknownUsername_whenLogin_thenThrowAccountNotFoundException() throws Exception {
    expectedException.expect(AccountNotFoundException.class);
    given(accountRepository.findByUsername(USERNAME)).willReturn(NO_ACCOUNT);

    loginApplicationService.login(USERNAME, PASSWORD);
  }

  @Test
  public void givenWrongPasswordForAccount_whenLogin_thenThrowWrongPasswordException()
    throws Exception
  {
    expectedException.expect(WrongPasswordException.class);
    given(accountRepository.findByUsername(USERNAME)).willReturn(account);
    given(account.getPassword()).willReturn(PASSWORD);

    loginApplicationService.login(USERNAME, WRONG_PASSWORD);
  }

}
