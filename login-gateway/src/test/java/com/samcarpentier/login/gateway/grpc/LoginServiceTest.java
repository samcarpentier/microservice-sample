package com.samcarpentier.login.gateway.grpc;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.common.collect.Lists;
import com.samcarpentier.login.gateway.application.LoginApplicationService;
import com.samcarpentier.login.gateway.domain.entity.Account;
import com.samcarpentier.login.gateway.domain.exception.AccountNotFoundException;
import com.samcarpentier.login.gateway.domain.exception.WrongPasswordException;
import com.samcarpentier.login.gateway.grpc.LoginRequest;
import com.samcarpentier.login.gateway.grpc.LoginResponse;
import com.samcarpentier.login.gateway.grpc.LoginService;

import io.grpc.Status;
import io.grpc.StatusException;
import io.grpc.stub.StreamObserver;

@RunWith(MockitoJUnitRunner.class)
public class LoginServiceTest {

  private static final String USERNAME = "username";
  private static final String PASSWORD = "password";
  private static final List<String> PHONE_NUMBERS = Lists.newArrayList("pn1", "pn2");

  private LoginService loginService;

  @Mock
  private LoginApplicationService loginApplicationService;

  @Mock
  private StreamObserver<LoginResponse> responseObserver;
  @Mock
  private Account account;

  @Captor
  private ArgumentCaptor<LoginResponse> loginResponseArgumentCaptor;
  @Captor
  private ArgumentCaptor<Throwable> errorArgumentCaptor;

  @Before
  public void setUp() {
    loginService = new LoginService(loginApplicationService);
  }

  @Test
  public void givenSuccessfulLogin_whenAuthenticate_thenReturnLoginResponseCorrectlyFormatted()
    throws Exception
  {
    LoginRequest loginRequest = givenLoginRequest(USERNAME, PASSWORD);
    given(loginApplicationService.login(USERNAME, PASSWORD)).willReturn(account);
    given(account.getPhoneNumbers()).willReturn(PHONE_NUMBERS);

    loginService.login(loginRequest, responseObserver);

    verify(responseObserver).onNext(loginResponseArgumentCaptor.capture());
    assertThat(loginResponseArgumentCaptor.getValue()
                                          .getPhoneNumbersList()).containsAllIn(PHONE_NUMBERS);
  }

  @Test
  public void givenSuccessfulLogin_whenAuthenticate_thenResponseObserverOnNextAndOnCompletedAreCalled()
    throws Exception
  {
    LoginRequest loginRequest = givenLoginRequest(USERNAME, PASSWORD);
    given(loginApplicationService.login(USERNAME, PASSWORD)).willReturn(account);
    given(account.getPhoneNumbers()).willReturn(PHONE_NUMBERS);

    loginService.login(loginRequest, responseObserver);

    verify(responseObserver).onNext(Mockito.any());
    verify(responseObserver).onCompleted();
  }

  @Test
  public void givenAccountNotFoundExceptionIsThrown_whenAuthenticate_thenOnErrorWithStatusException()
    throws Exception
  {
    givenLoginThrows(AccountNotFoundException.class);
    assertThatRegisteredErrorIsInstanceOf(StatusException.class);
  }

  @Test
  public void givenAccountNotFoundExceptionIsThrown_whenAuthenticate_thenRegisteredErrorHasNotFoundStatus()
    throws Exception
  {
    givenLoginThrows(AccountNotFoundException.class);
    assertThatErrorStatusIs(Status.NOT_FOUND);
  }

  @Test
  public void givenWrongPasswordExceptionIsThrown_whenAuthenticate_thenOnErrorWithStatusException()
    throws Exception
  {
    givenLoginThrows(WrongPasswordException.class);
    assertThatRegisteredErrorIsInstanceOf(StatusException.class);
  }

  @Test
  public void givenWrongPasswordExceptionIsThrown_whenAuthenticate_thenRegisteredErrorHasPermissionDeniedStatus()
    throws Exception
  {
    givenLoginThrows(WrongPasswordException.class);
    assertThatErrorStatusIs(Status.PERMISSION_DENIED);
  }

  private LoginRequest givenLoginRequest(String username, String password) {
    return LoginRequest.newBuilder().setUsername(username).setPassword(password).build();
  }

  private void givenLoginThrows(Class<? extends Exception> exception)
    throws AccountNotFoundException,
      WrongPasswordException
  {
    LoginRequest loginRequest = givenLoginRequest(USERNAME, PASSWORD);
    doThrow(exception).when(loginApplicationService).login(USERNAME, PASSWORD);

    loginService.login(loginRequest, responseObserver);
  }

  private void assertThatRegisteredErrorIsInstanceOf(Class<? extends Exception> exception) {
    verify(responseObserver).onError(errorArgumentCaptor.capture());
    assertThat(errorArgumentCaptor.getValue()).isInstanceOf(exception);
  }

  private void assertThatErrorStatusIs(Status status) {
    verify(responseObserver).onError(errorArgumentCaptor.capture());
    StatusException statusException = (StatusException) errorArgumentCaptor.getValue();
    assertThat(statusException.getStatus().getCode()).isEqualTo(status.getCode());
  }

}
