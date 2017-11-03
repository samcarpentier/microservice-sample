package com.samcarpentier.authentication.ws.grpc.name.resolver;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

import io.grpc.Attributes;
import io.grpc.EquivalentAddressGroup;
import io.grpc.NameResolver;
import io.grpc.Status;

public class CustomNameResolver extends NameResolver {

  private static final Logger logger = LoggerFactory.getLogger(CustomNameResolver.class);
  private static final String SERVICE_AUTHORITY = "";

  private final String host;
  private final Set<Integer> ports;

  public CustomNameResolver(String host, Set<Integer> ports) {
    this.host = host;
    this.ports = ports;
  }

  @Override
  public void start(Listener listener) {
    List<EquivalentAddressGroup> equivalentAddressGroups = Lists.newArrayList();
    logger.info(String.format("Registering servers: %s",
                              ports.stream()
                                   .map(port -> host + ":" + port)
                                   .collect(Collectors.toSet())));

    ports.stream()
         .map(port -> new InetSocketAddress(host, port))
         .forEach(inetSocketAddress -> new EquivalentAddressGroup(inetSocketAddress));

    if (!equivalentAddressGroups.isEmpty()) {
      listener.onAddresses(equivalentAddressGroups, Attributes.EMPTY);
      listener.onError(Status.INTERNAL);
    }

    logger.info("started");
  }

  @Override
  public String getServiceAuthority() {
    return SERVICE_AUTHORITY;
  }

  @Override
  public void shutdown() {
    throw new RuntimeException("SHIT");
  }

}
