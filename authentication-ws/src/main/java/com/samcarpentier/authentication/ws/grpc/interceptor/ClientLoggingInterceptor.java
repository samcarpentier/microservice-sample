package com.samcarpentier.authentication.ws.grpc.interceptor;

import java.time.Instant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.grpc.CallOptions;
import io.grpc.Channel;
import io.grpc.ClientCall;
import io.grpc.ClientInterceptor;
import io.grpc.ForwardingClientCall.SimpleForwardingClientCall;
import io.grpc.ForwardingClientCallListener.SimpleForwardingClientCallListener;
import io.grpc.Metadata;
import io.grpc.MethodDescriptor;

public class ClientLoggingInterceptor implements ClientInterceptor {

  private static final Logger logger = LoggerFactory.getLogger(ClientLoggingInterceptor.class);

  private static final String REQUEST_CALL_IDENTIFIER = "Request";
  private static final String RESPONSE_CALL_IDENTIFIER = "Response";
  private static final String CALL_IDENTIFIER_TAG = "%CALL%";
  private static final String LOG_FORMAT = "\n<" + CALL_IDENTIFIER_TAG
                                           + ">\n  Timestamp: %s\n  Method: %s\n  Payload: (%s)\n</"
                                           + CALL_IDENTIFIER_TAG + ">";

  @Override
  public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(MethodDescriptor<ReqT, RespT> methodDescriptor,
                                                             CallOptions callOptions,
                                                             Channel next)
  {
    ClientCall<ReqT, RespT> clientCall = next.newCall(methodDescriptor, callOptions);
    ClientCall<ReqT, RespT> clientCallLoggingProxy = new SimpleForwardingClientCall<ReqT, RespT>(clientCall) {
      @Override
      public void sendMessage(ReqT requestMessage) {
        logMessage(REQUEST_CALL_IDENTIFIER,
                   methodDescriptor.getFullMethodName(),
                   requestMessage.toString());

        super.sendMessage(requestMessage);
      }

      @Override
      public void start(Listener<RespT> responseListener, Metadata headers) {
        Listener<RespT> responseListenerLoggingProxy = new SimpleForwardingClientCallListener<RespT>(responseListener) {
          @Override
          public void onMessage(RespT responseMessage) {
            logMessage(RESPONSE_CALL_IDENTIFIER,
                       methodDescriptor.getFullMethodName(),
                       responseMessage.toString());

            super.onMessage(responseMessage);
          }
        };

        super.start(responseListenerLoggingProxy, headers);
      }
    };

    return clientCallLoggingProxy;
  }

  private void logMessage(String callIdentifier, String methodName, String message) {
    logger.info(String.format(LOG_FORMAT.replaceAll(CALL_IDENTIFIER_TAG, callIdentifier),
                              Instant.now().toString(),
                              methodName,
                              formatMessageForLogging(message)));
  }

  private String formatMessageForLogging(String rawMessage) {
    String messageWithoutLineBreaks = rawMessage.replaceAll("(\\r|\\n)", ", ");
    return messageWithoutLineBreaks.substring(0, messageWithoutLineBreaks.length() - 2);
  }

}
