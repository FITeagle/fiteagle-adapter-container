package org.fiteagle.adapters.containers.docker.internal;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;

import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;

/**
 * Use this to build HTTP request objects which can be sent to an API endpoint.
 */
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
	 * Create and prepare a URIBuilder.
	 */
	private URIBuilder prepareBuilder() {
		return
			new URIBuilder()
				.setScheme(scheme)
				.setHost(hostname)
				.setPort(port);
	}

	/**
	 * Retrieve version information.
	 */
	public HttpGet version() throws URISyntaxException {
		return
			new HttpGet(prepareBuilder()
			            .setPath("/version")
			            .build());
	}

	/**
	 * Retrieve general information.
	 */
	public HttpGet info() throws URISyntaxException {
		return
			new HttpGet(prepareBuilder()
			            .setPath("/info")
			            .build());
	}

	/**
	 * List containers.
	 */
	public HttpGet list(boolean all) throws URISyntaxException {
		return
			new HttpGet(prepareBuilder()
			            .setPath("/containers/json")
			            .setParameter("all", all ? "1" : "0")
			            .setParameter("size", "1")
			            .build());
	}

	/**
	 * Create a container.
	 */
	public HttpPost create(ContainerConfiguration info)
		throws URISyntaxException, UnsupportedEncodingException
	{
		HttpPost request =
			new HttpPost(prepareBuilder()
			             .setPath("/containers/create")
			             .build());

		request.addHeader("Content-Type", "application/json");
		request.setEntity(info.toEntity());

		return request;
	}

	/**
	 * Delete a container.
	 */
	public HttpDelete delete(String containerID, boolean deleteVolume, boolean force)
		throws URISyntaxException
	{
		return
			new HttpDelete(prepareBuilder()
			               .setPath("/containers/" + containerID)
			               .setParameter("v", deleteVolume ? "1" : "0")
			               .setParameter("force", force ? "1" : "0")
			               .build());
	}

	/**
	 * Inspect a container.
	 */
	public HttpGet inspect(String containerID)
		throws URISyntaxException
	{
		return
			new HttpGet(prepareBuilder()
			            .setPath("/containers/" + containerID + "/json")
			            .build());
	}

	/**
	 * Start a container.
	 */
	public HttpPost start(String containerID)
		throws URISyntaxException, UnsupportedEncodingException
	{
		HttpPost request =
			new HttpPost(prepareBuilder()
			             .setPath("/containers/" + containerID + "/start")
			             .build());

		request.addHeader("Content-Type", "application/json");

		request.setEntity(new StringEntity("{}"));

		return request;
	}

	/**
	 * Start a container.
	 */
	public HttpPost stop(String containerID, int timeout)
		throws URISyntaxException
	{
		return
			new HttpPost(prepareBuilder()
			             .setPath("/containers/" + containerID + "/stop")
			             .build());
	}

	/**
	 * Kill a container.
	 */
	public HttpPost kill(String containerID, String signal)
		throws URISyntaxException
	{
		return
			new HttpPost(prepareBuilder()
			             .setPath("/containers/" + containerID + "/kill")
			             .setParameter("signal", signal)
			             .build());
	}

	/**
	 * Restart a container.
	 */
	public HttpPost restart(String containerID, int timeout)
		throws URISyntaxException
	{
		return
			new HttpPost(prepareBuilder()
			             .setPath("/containers/" + containerID + "/restart")
			             .build());
	}

	/**
	 * Wait for a container to exit.
	 */
	public HttpPost waitFor(String containerID)
		throws URISyntaxException
	{
		return
			new HttpPost(prepareBuilder()
			             .setPath("/containers/" + containerID + "/wait")
			             .build());
	}
}
