package com.samcarpentier.login.gateway.data.entity;

import java.util.List;

public class AccountDto {

  public final String username;
  public final String password;
  public final List<String> phoneNumbers;

  public AccountDto(String username, String password, List<String> phoneNumbers) {
    this.username = username;
    this.password = password;
    this.phoneNumbers = phoneNumbers;
  }

}
