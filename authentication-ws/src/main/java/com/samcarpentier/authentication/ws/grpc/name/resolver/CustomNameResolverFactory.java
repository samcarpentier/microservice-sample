package com.samcarpentier.authentication.ws.grpc.name.resolver;

import java.net.InetSocketAddress;
import java.net.URI;
import java.util.Collection;

import io.grpc.Attributes;
import io.grpc.NameResolver;

public class CustomNameResolverFactory extends NameResolver.Factory {

  public static final String DEFAULT_SCHEME = "mesh";

  private final Collection<InetSocketAddress> serverAddresses;

  public CustomNameResolverFactory(Collection<InetSocketAddress> serverAddresses) {
    this.serverAddresses = serverAddresses;
  }

  @Override
  public NameResolver newNameResolver(URI targetUri, Attributes params) {
    return new CustomNameResolverProvider(DEFAULT_SCHEME,
                                          serverAddresses).newNameResolver(targetUri, params);
  }

  @Override
  public String getDefaultScheme() {
    return DEFAULT_SCHEME;
  }
}
