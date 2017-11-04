package com.samcarpentier.authentication.ws.grpc.name.resolver;

import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.grpc.Attributes;
import io.grpc.EquivalentAddressGroup;
import io.grpc.NameResolver;
import io.grpc.Status;

public class CustomNameResolver extends NameResolver {

  private static final String NO_SERVICE_AUTHORITY = "";

  private static final Logger logger = LoggerFactory.getLogger(CustomNameResolver.class);

  private final Collection<InetSocketAddress> staticAddresses;

  public CustomNameResolver(Collection<InetSocketAddress> serverAddresses) {
    this.staticAddresses = serverAddresses;
  }

  @Override
  public void start(NameResolver.Listener listener) {
    try {
      List<EquivalentAddressGroup> equivalentAddressGroups = staticAddresses.stream()
                                                                            .map(address -> new EquivalentAddressGroup(address))
                                                                            .collect(Collectors.toList());

      listener.onAddresses(equivalentAddressGroups, Attributes.EMPTY);
      logger.debug(String.format("Registered servers: %s",
                                 equivalentAddressGroups.stream()
                                                        .map(EquivalentAddressGroup::getAddresses)
                                                        .collect(Collectors.toSet())));
    } catch (Throwable e) {
      listener.onError(Status.UNKNOWN);
    }
  }

  @Override
  public String getServiceAuthority() {
    return NO_SERVICE_AUTHORITY;
  }

  @Override
  public void shutdown() {
  }
}
