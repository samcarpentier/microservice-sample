package com.samcarpentier.login.gateway.data;

public interface DevelopmentDataSupplier<T> {

  void directSave(T object);

}
