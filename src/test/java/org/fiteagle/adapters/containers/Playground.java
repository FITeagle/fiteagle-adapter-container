package org.fiteagle.adapters.containers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.util.Map.Entry;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.fiteagle.adapters.containers.docker.internal.CreateContainerInfo;
import org.fiteagle.adapters.containers.docker.internal.RequestBuilder;

public class Playground {
	public static void main(String[] args) throws URISyntaxException, ClientProtocolException, IOException {
		CreateContainerInfo cci = new CreateContainerInfo("mycontainer", "root", "ubuntu:latest");

		cci.putEnvironment("hello", "world");
		cci.putLabel("how", "wonderful");
		cci.setCommandEasily("/bin/sh", "-c", "echo Hello World");

		for (Entry<String, String> entry: cci.getEnvironment().entrySet()) {
			System.out.println(entry.getKey() + " = " + entry.getValue());
		}

		System.out.println(cci.toJsonObject().toString());

		RequestBuilder requestBuilder = new RequestBuilder("localhost", 8080);

		HttpPost request = requestBuilder.createContainer(cci);

		CloseableHttpClient httpClient = HttpClients.createDefault();

		// Clear container
		CloseableHttpResponse httpResponse = httpClient.execute(request);

		BufferedReader breader = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent()));

		String line = null;
		while ((line = breader.readLine()) != null) {
			System.out.println(line);
		}

		httpResponse.close();

		// List containers
		HttpGet httpListRequest = requestBuilder.listContainers(true);
		CloseableHttpResponse httpListResponse = httpClient.execute(httpListRequest);

		BufferedReader breader2 = new BufferedReader(new InputStreamReader(httpListResponse.getEntity().getContent()));
		while ((line = breader2.readLine()) != null) {
			System.out.println(line);
		}

		httpListResponse.close();

		httpClient.close();
	}
}
