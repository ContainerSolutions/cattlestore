package com.containersolutions.platform.service.discovery;

import com.google.common.base.Optional;
import com.google.common.base.Throwables;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.util.concurrent.UncheckedExecutionException;
import org.xbill.DNS.ARecord;
import org.xbill.DNS.Lookup;
import org.xbill.DNS.Record;
import org.xbill.DNS.SRVRecord;
import org.xbill.DNS.SimpleResolver;
import org.xbill.DNS.TextParseException;
import org.xbill.DNS.Type;

import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

/**
 * The dnsjava library is very thread safe.
 */
public class ConsulServiceDnsSRVResolver implements ServiceResolver {
	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(ConsulServiceDnsSRVResolver.class);

	private static final String CONSUL_DEFAULT_HOSTNAME = "192.168.99.100";
	private static final int CONSUL_DNS_PORT = 8600;

	private static final SimpleResolver simpleResolver;

	static {
//		System.setProperty("dnsjava.options", "verbosecache=1,verbose=1,verbosemsg=1");
		try {
			simpleResolver = new SimpleResolver();
			simpleResolver.setAddress(new InetSocketAddress(getEnvWithDefault("CONSUL_HOSTNAME", CONSUL_DEFAULT_HOSTNAME), CONSUL_DNS_PORT));
		} catch (UnknownHostException e) {
			throw new ServiceResolutionException("Could not resolve DNS for DNS server from env, host unknown", e);
		}
	}

	private LoadingCache<String, InetSocketAddress> cache = CacheBuilder.newBuilder()
			.expireAfterWrite(5, TimeUnit.SECONDS)
			.build(
					new CacheLoader<String, InetSocketAddress>() {
						public InetSocketAddress load(@SuppressWarnings("NullableProblems") String key) throws ServiceResolutionException {
							return resolveWithoutCache(key);
						}
					});

	public InetSocketAddress resolve(final String serviceName) throws ServiceResolutionException {
		try {
			return cache.getUnchecked(serviceName);
		} catch (UncheckedExecutionException e) {
			Throwables.propagateIfPossible(e.getCause(), ServiceResolutionException.class);
			throw new IllegalStateException(e);
		}
	}

	private InetSocketAddress resolveWithoutCache(final String serviceName) throws ServiceResolutionException {
		log.debug("Resolving service {}", serviceName);

		SRVRecord srv = (SRVRecord) lookUp(serviceName + ".service.consul", Type.SRV);
		final String target = srv.getTarget().toString();
		log.trace("Found DNS record {}:{}, priority {}, for serviceName {}",
				target, srv.getPort(), srv.getPriority(), serviceName + ".service.consul");

		// Try to resolve the target as well, since it's probably only known by Consul
		ARecord record = (ARecord) lookUp(target, Type.A);
		final String ip = record.getAddress().getHostAddress();
		log.debug("Found DNS record {}:{}, priority {}, for serviceName {} ({})",
				ip, srv.getPort(), srv.getPriority(), serviceName + ".service.consul", target);

		return new InetSocketAddress(getEnvWithDefault("IP_OVERRIDE", ip), srv.getPort());
	}

	private Record lookUp(final String serviceName, final int type) {
		try {
			Lookup lookupSrv = new Lookup(serviceName, type);
			lookupSrv.setResolver(simpleResolver);

			lookupSrv.run();

			if (lookupSrv.getResult() != Lookup.SUCCESSFUL) {
				throw new ServiceResolutionException("Could not resolve " + serviceName);
			}

			Record[] records = lookupSrv.getAnswers();
			if (records == null) {
				throw new ServiceResolutionException("Could not resolve host " + serviceName + " records is null");
			}
			if (records.length == 0) {
				throw new ServiceResolutionException("Could not resolve host " + serviceName + " records has length 0");
			}

			return records[0];
		} catch (TextParseException e) {
			throw new ServiceResolutionException("Could not parse text when resolving " + serviceName, e);
		}
	}

	private static String getEnvWithDefault(final String name, final String defaultValue) {
		return Optional.fromNullable(System.getenv(name)).or(defaultValue);
	}
}
