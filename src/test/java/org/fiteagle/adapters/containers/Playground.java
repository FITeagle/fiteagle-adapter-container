package org.fiteagle.adapters.containers;

import java.io.IOException;
import java.net.URISyntaxException;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.fiteagle.adapters.containers.docker.internal.DockerException;
import org.fiteagle.adapters.containers.docker.internal.RequestBuilder;

public class Playground {
	public static void main(String[] args)
			throws URISyntaxException, ClientProtocolException, IOException, DockerException
	{
		CloseableHttpClient httpClient = HttpClients.createDefault();
		RequestBuilder requestBuilder = new RequestBuilder("localhost", 8080);

		// List containers
		CloseableHttpResponse listResponse = httpClient.execute(
			requestBuilder.listContainers(true)
		);



		// Finalize
		listResponse.close();
		httpClient.close();
	}
}
