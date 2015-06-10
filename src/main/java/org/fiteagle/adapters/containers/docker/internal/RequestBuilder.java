package org.fiteagle.adapters.containers.docker.internal;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

import org.apache.http.client.utils.URIBuilder;

public class RequestBuilder {
	private String scheme = "http";
	private String hostname;
	private int port = 80;
	
	/**
	 * Instantiate a request builder which targets an endpoint at `scheme://hostname:port`.
	 */
	public RequestBuilder(String scheme, String hostname, int port) {
		this.scheme = scheme;
		this.hostname = hostname;
		this.port = port;
	}
	
	/**
	 * Instantiate a request builder which targets an endpoint at `scheme://hostname:80`.
	 */
	public RequestBuilder(String scheme, String hostname) {
		this.scheme = scheme;
		this.hostname = hostname;
	}
	
	/**
	 * Instantiate a request builder which targets an endpoint at `http://hostname:port`.
	 */
	public RequestBuilder(String hostname, int port) {
		this.hostname = hostname;
		this.port = port;
	}
	
	/**
	 * Instantiate a request builder which targets an endpoint at `http://hostname:80`.
	 */
	public RequestBuilder(String hostname) {
		this.hostname = hostname;
	}
	
	/**
	 * Create and prepare a URLBuilder.
	 */
	private URIBuilder prepareBuilder() {
		return
			new URIBuilder()
				.setScheme(scheme)
				.setHost(hostname)
				.setPort(port);
	}
	
	/**
	 * Build the URL for a version request.
	 */
	public URL version() throws MalformedURLException, URISyntaxException {
		return
			prepareBuilder()
				.setPath("/version")
				.build()
				.toURL();
	}
	
	/**
	 * Build the URL for a information request.
	 */
	public URL info() throws MalformedURLException, URISyntaxException {
		return
			prepareBuilder()
				.setPath("/info")
				.build()
				.toURL();
	}
}
