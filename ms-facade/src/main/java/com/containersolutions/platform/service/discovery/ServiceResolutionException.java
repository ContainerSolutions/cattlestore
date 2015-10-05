package com.containersolutions.platform.service.discovery;


public class ServiceResolutionException extends RuntimeException {
	public ServiceResolutionException(final String msg) {
		super(msg);
	}

	public ServiceResolutionException(String message, Throwable cause) {
		super(message, cause);
	}
}
