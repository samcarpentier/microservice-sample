package com.samcarpentier.authentication.ws.grpc.name.resolver;

import java.net.InetSocketAddress;
import java.net.URI;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import io.grpc.Attributes;
import io.grpc.EquivalentAddressGroup;
import io.grpc.ManagedChannelBuilder;
import io.grpc.NameResolver;
import io.grpc.NameResolverProvider;
import io.grpc.Status;

/**
 * StaticResolver is a gRPC NameResolverProvider and NameResolver Factory that resolves every
 * request
 * to the same static address. StaticResolverProvider is useful when name resolution is being
 * delegated to an outside
 * proxy such as Linkerd or Envoy, typically running on localhost. Connecting directly to the proxy
 * is insufficient
 * because each request will have the Authority header for the proxy instead of for the destination
 * service. You can
 * also use {@link ManagedChannelBuilder#overrideAuthority(String)}, but this must be done manually
 * for every request.
 * <p>
 * By default, StaticResolverProvider only acts on request URIs with the mesh:// scheme. This
 * setting can be changed
 * in the constructor.
 * <p>
 * StaticResolverProvider is best used in conjunction with the {@link FallbackResolver} and
 * {@link ManagedChannelBuilder#nameResolverFactory(NameResolver.Factory)}
 */
public final class StaticResolver {
  private StaticResolver() {

  }

  public static final String DEFAULT_SCHEME = "mesh";

  /**
   * Constructs a StaticResolver provider that routes the mesh:// URI scheme to a static address.
   *
   * @param staticAddress The static address to route all requests to.
   */
  public static NameResolverProvider provider(Collection<InetSocketAddress> staticAddresses) {
    return provider(DEFAULT_SCHEME, staticAddresses);
  }

  /**
   * Constructs a StaticResolver provider tha routes a configurable URI scheme to a static address.
   *
   * @param scheme The URI scheme to route.
   * @param staticAddress The static address to route all requests to.
   */
  public static NameResolverProvider provider(String scheme,
                                              Collection<InetSocketAddress> staticAddresses)
  {
    return new StaticResolverProvider(scheme, staticAddresses);
  }

  /**
   * Constructs a StaticResolver factory that routes the mesh:// URI scheme to a static address.
   *
   * @param staticAddress The static address to route all requests to.
   */
  public static NameResolver.Factory factory(Collection<InetSocketAddress> staticAddresses) {
    return factory(DEFAULT_SCHEME, staticAddresses);
  }

  /**
   * Constructs a StaticResolver factory tha routes a configurable URI scheme to a static address.
   *
   * @param scheme The URI scheme to route.
   * @param staticAddress The static address to route all requests to.
   */
  public static NameResolver.Factory factory(String scheme,
                                             Collection<InetSocketAddress> staticAddresses)
  {
    return new StaticResolverFactory(scheme, staticAddresses);
  }

  /**
   * A provider of static name resolution.
   */
  private static class StaticResolverProvider extends NameResolverProvider {
    private final String scheme;
    private final Collection<InetSocketAddress> staticAddresses;

    /**
     * Constructs a StaticResolverProvider tha routes a configurable URI scheme to a static address.
     *
     * @param scheme The URI scheme to route.
     * @param staticAddress The static address to route all requests to.
     */
    StaticResolverProvider(String scheme, Collection<InetSocketAddress> staticAddresses) {
      this.scheme = scheme;
      this.staticAddresses = staticAddresses;
    }

    @Nullable
    @Override
    public NameResolver newNameResolver(URI targetUri, Attributes params) {
      if (scheme.equals(targetUri.getScheme())) {
        return new NameResolver() {
          @Override
          public String getServiceAuthority() {
            return "";
          }

          @Override
          public void start(NameResolver.Listener listener) {
            try {
              List<EquivalentAddressGroup> equivalentAddressGroups = staticAddresses.stream()
                                                                                    .map(address -> new EquivalentAddressGroup(address))
                                                                                    .collect(Collectors.toList());

              System.out.println(equivalentAddressGroups);

              listener.onAddresses(equivalentAddressGroups, Attributes.EMPTY);
            } catch (Throwable e) {
              listener.onError(Status.UNKNOWN);
            }
          }

          @Override
          public void shutdown() {

          }
        };
      }

      return null;
    }

    @Override
    protected boolean isAvailable() {
      return true;
    }

    @Override
    protected int priority() {
      return 0;
    }

    @Override
    public String getDefaultScheme() {
      return scheme;
    }
  }

  /**
   * A factory for using StaticResolverProvider directly.
   */
  private static class StaticResolverFactory extends NameResolver.Factory {
    private String scheme;
    private StaticResolverProvider provider;

    StaticResolverFactory(String scheme, Collection<InetSocketAddress> staticAddresses) {
      this.scheme = scheme;
      this.provider = new StaticResolverProvider(scheme, staticAddresses);
    }

    @Nullable
    @Override
    public NameResolver newNameResolver(URI targetUri, Attributes params) {
      return provider.newNameResolver(targetUri, params);
    }

    @Override
    public String getDefaultScheme() {
      return scheme;
    }
  }
}