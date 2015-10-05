package com.containersolutions.platform.facade.exceptions;

import lombok.extern.slf4j.Slf4j;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

@Slf4j
public class RuntimeExceptionMapper implements ExceptionMapper<RuntimeException> {
	@Override
	public Response toResponse(RuntimeException exception) {
		log.warn("Encountered exception", exception);

		String msg = exception.getMessage();
		return Response
				.serverError()
				.entity(msg)
				.build();
	}
}
