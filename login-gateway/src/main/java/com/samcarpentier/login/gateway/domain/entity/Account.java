package com.samcarpentier.login.gateway.domain.entity;

import java.util.List;

public class Account {

  private final String username;
  private final String password;
  private final List<String> phoneNumbers;

  public Account(String username, String password, List<String> phoneNumbers) {
    this.username = username;
    this.password = password;
    this.phoneNumbers = phoneNumbers;
  }

  public String getUsername() {
    return username;
  }

  public String getPassword() {
    return password;
  }

  public List<String> getPhoneNumbers() {
    return phoneNumbers;
  }

}
