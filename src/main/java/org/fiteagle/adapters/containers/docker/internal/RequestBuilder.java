package org.fiteagle.adapters.containers.docker.internal;

public class RequestBuilder {
	private String scheme = "http";
	private String hostname;
	private int port = 80;
	
	/**
	 * Instatiate a request builder which targets an endpoint at `scheme://hostname:port`.
	 */
	public RequestBuilder(String scheme, String hostname, int port) {
		this.scheme = scheme;
		this.hostname = hostname;
		this.port = port;
	}
	
	/**
	 * Instatiate a request builder which targets an endpoint at `scheme://hostname:80`.
	 */
	public RequestBuilder(String scheme, String hostname) {
		this.scheme = scheme;
		this.hostname = hostname;
	}
	
	/**
	 * Instatiate a request builder which targets an endpoint at `http://hostname:port`.
	 */
	public RequestBuilder(String hostname, int port) {
		this.hostname = hostname;
		this.port = port;
	}
	
	/**
	 * Instatiate a request builder which targets an endpoint at `http://hostname:80`.
	 */
	public RequestBuilder(String hostname) {
		this.hostname = hostname;
	}
}
