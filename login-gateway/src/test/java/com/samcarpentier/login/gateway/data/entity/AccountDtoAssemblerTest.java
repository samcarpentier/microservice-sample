package com.samcarpentier.login.gateway.data.entity;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import java.util.ArrayList;

import org.junit.Test;

import com.google.common.collect.Lists;
import com.samcarpentier.login.gateway.domain.entity.Account;

public class AccountDtoAssemblerTest {

  private static final String USERNAME = "username";
  private static final String PASSWORD = "password";
  private static final ArrayList<String> PHONE_NUMBERS = Lists.newArrayList("phoneNumber1",
                                                                            "phoneNumber2");

  private AccountDtoAssembler accountDtoAssembler = new AccountDtoAssembler();

  @Test
  public void givenAccount_whenAssembleDto_thenReturnAccountDtoCorrectlyFormatted() {
    Account account = mock(Account.class);
    given(account.getUsername()).willReturn(USERNAME);
    given(account.getPassword()).willReturn(PASSWORD);
    given(account.getPhoneNumbers()).willReturn(PHONE_NUMBERS);

    AccountDto accountDto = accountDtoAssembler.assembleDto(account);

    assertThat(accountDto.username).isEqualTo(USERNAME);
    assertThat(accountDto.password).isEqualTo(PASSWORD);
    assertThat(accountDto.phoneNumbers).containsExactlyElementsIn(PHONE_NUMBERS);
  }

  @Test
  public void givenAccountDto_whenAssemble_thenReturnAccountCorrectlyFormatted() {
    AccountDto accountDto = new AccountDto(USERNAME, PASSWORD, PHONE_NUMBERS);

    Account account = accountDtoAssembler.assemble(accountDto);

    assertThat(account.getUsername()).isEqualTo(USERNAME);
    assertThat(account.getPassword()).isEqualTo(PASSWORD);
    assertThat(account.getPhoneNumbers()).containsExactlyElementsIn(PHONE_NUMBERS);
  }

}
