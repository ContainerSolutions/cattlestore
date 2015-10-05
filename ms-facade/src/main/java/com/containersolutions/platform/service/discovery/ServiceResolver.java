package com.containersolutions.platform.service.discovery;

import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;

public interface ServiceResolver {
	InetSocketAddress resolve(String serviceName) throws ServiceResolutionException;

	default URI resolveAsURI(String serviceName) throws ServiceResolutionException {
		InetSocketAddress inetSocketAddress = resolve(serviceName);
		URI uri;
		try {
			uri = new URI(null, null, inetSocketAddress.getHostString(), inetSocketAddress.getPort(), null, null, null);
		} catch (URISyntaxException e) {
			throw new ServiceResolutionException(e.getMessage());
		}
		return uri;
	}
}
