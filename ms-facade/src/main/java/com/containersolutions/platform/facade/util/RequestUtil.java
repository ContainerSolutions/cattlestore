package com.containersolutions.platform.facade.util;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.URI;

@Slf4j
@Getter
public abstract class RequestUtil {
	public static String get(URI uri) {
		log.debug("Internal GET request for {} expecting response of type 'String'", uri);

		try {
			// todo get the scheme from proper place
			HttpRequestBase httpRequest = new HttpGet("http:" + uri);
			HttpEntity entity = null;
			try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
				try (CloseableHttpResponse response = httpClient.execute(httpRequest)) {
					entity = response.getEntity();
					return EntityUtils.toString(response.getEntity());
				} finally {
					if (entity != null) {
						EntityUtils.consume(entity);
					}
				}
			}
		} catch (IOException e) {
			log.error("Failed to send request", e);
		}

		throw new IllegalStateException();
	}
}
