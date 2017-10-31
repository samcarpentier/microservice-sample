package com.samcarpentier.login.gateway.domain;

import com.samcarpentier.login.gateway.domain.entity.Account;

public interface AccountRepository {

  Account login(String username, String password);

}
