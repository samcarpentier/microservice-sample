package com.samcarpentier.authentication.ws.grpc.name.resolver;

import java.net.URI;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.base.Preconditions;

import io.grpc.Attributes;
import io.grpc.NameResolver;
import io.grpc.NameResolverProvider;

public class CustomNameResolverProvider extends NameResolverProvider {

  private static final int PRIORITY = 5;
  private static final String DEFAULT_SCHEME = "mesh";
  private static final boolean AVAILABLE = true;

  private Set<Integer> ports;

  @Override
  public NameResolver newNameResolver(URI targetUri, Attributes params) {
    Preconditions.checkState(ports != null, "No port registered for given target");
    return new CustomNameResolver(targetUri.getHost(), ports);
  }

  @Override
  protected boolean isAvailable() {
    return AVAILABLE;
  }

  @Override
  protected int priority() {
    return PRIORITY;
  }

  @Override
  public String getDefaultScheme() {
    return DEFAULT_SCHEME;
  }

  public CustomNameResolverProvider withPorts(Collection<Integer> ports) {
    this.ports = ports.stream().collect(Collectors.toSet());
    return this;
  }

}
