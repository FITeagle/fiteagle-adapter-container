package org.fiteagle.adapters.containers;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.LinkedList;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.fiteagle.adapters.containers.docker.internal.ContainerInfo;
import org.fiteagle.adapters.containers.docker.internal.DockerException;
import org.fiteagle.adapters.containers.docker.internal.RequestBuilder;
import org.fiteagle.adapters.containers.docker.internal.ResponseParser;

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

		LinkedList<ContainerInfo> containers = ResponseParser.listContainers(listResponse);

		System.out.println("Got " + containers.size() + " containers");

		for (ContainerInfo ci: containers) {
			System.out.println(ci.id + ": " + ci.image);
		}

		// Finalize
		listResponse.close();
		httpClient.close();
	}
}
