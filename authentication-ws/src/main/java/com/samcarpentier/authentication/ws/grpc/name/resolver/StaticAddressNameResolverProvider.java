package com.samcarpentier.authentication.ws.grpc.name.resolver;

import java.net.InetSocketAddress;
import java.net.URI;
import java.util.Collection;

import io.grpc.Attributes;
import io.grpc.NameResolver;
import io.grpc.NameResolverProvider;

public class StaticAddressNameResolverProvider extends NameResolverProvider {

  private static final boolean AVAILABLE = true;
  private static final int PRIORITY = 5;

  private final String defaultScheme;
  private final Collection<InetSocketAddress> serverAddresses;

  public StaticAddressNameResolverProvider(String defaultScheme,
                                    Collection<InetSocketAddress> serverAddresses)
  {
    this.defaultScheme = defaultScheme;
    this.serverAddresses = serverAddresses;
  }

  @Override
  public NameResolver newNameResolver(URI targetUri, Attributes params) {
    if (defaultScheme.equals(targetUri.getScheme())) {
      return new StaticAddressNameResolver(serverAddresses);
    }

    return null;
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
    return defaultScheme;
  }
}
