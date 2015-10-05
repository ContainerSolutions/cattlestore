package com.containersolutions.platform.facade.resources;

import com.containersolutions.platform.facade.util.RequestUtil;
import com.containersolutions.platform.service.discovery.ServiceResolver;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.utils.URIBuilder;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.net.URI;
import java.net.URISyntaxException;

@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Path("/tick")
@Slf4j
public class TickResource {
	private ServiceResolver serviceResolver;

	public TickResource(final ServiceResolver serviceResolver) {
		this.serviceResolver = serviceResolver;
	}

	@GET
	public String tick(@PathParam("data") String data) {
		URI result;
		URI cattlestore = serviceResolver.resolveAsURI("cattlestore");
		log.warn(String.valueOf(cattlestore));
		URIBuilder uriBuilder = new URIBuilder(cattlestore);
		try {
			result = uriBuilder.setPath(uriBuilder.getPath() + "/tick").build();
		} catch (URISyntaxException e) {
			log.error("Could not create URI from parameters: {} {} {}", "tick", "/tick", e);
			throw new IllegalStateException("Could not create URI from parameters: " + "/tick", e);
		}
		return RequestUtil.get(result);
	}
}
