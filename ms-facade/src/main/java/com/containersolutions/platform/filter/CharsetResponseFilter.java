package com.containersolutions.platform.filter;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.MediaType;
import java.io.IOException;

public class CharsetResponseFilter implements ContainerResponseFilter {
	private static final MediaType DEFAULT_CONTENT_TYPE = MediaType.APPLICATION_JSON_TYPE;

	@Override
	public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {
		MediaType contentType = responseContext.getMediaType();

		if (contentType == null) {
			contentType = DEFAULT_CONTENT_TYPE;
		}

		responseContext.getHeaders().putSingle("Content-Type", contentType.toString() + ";charset=utf-8");
	}
}
