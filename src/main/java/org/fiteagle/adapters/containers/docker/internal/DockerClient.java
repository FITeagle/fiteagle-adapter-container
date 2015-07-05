package org.fiteagle.adapters.containers.docker.internal;

import java.util.LinkedList;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

public class DockerClient {
	private RequestBuilder requestBuilder;
	private CloseableHttpClient httpClient;

	public DockerClient(String hostname, int port) {
		requestBuilder = new RequestBuilder(hostname, port);
		httpClient = HttpClients.createDefault();
	}

	public String createContainer(CreateContainerInfo cci)
		throws DockerException
	{
		try {
			return ResponseParser.createContainer(
				httpClient.execute(requestBuilder.createContainer(cci))
			);
		} catch (Exception e) {
			throw new DockerException(e);
		}
	}

	public LinkedList<ContainerInfo> listContainers(boolean all)
		throws DockerException
	{
		try {
			return ResponseParser.listContainers(
				httpClient.execute(requestBuilder.listContainers(all))
			);
		} catch (Exception e) {
			throw new DockerException(e);
		}
	}

	public boolean deleteContainer(String containerID, boolean deleteVolume, boolean force)
		throws DockerException
	{
		try {
			return ResponseParser.deleteContainer(
				httpClient.execute(requestBuilder.deleteContainer(containerID, deleteVolume, force))
			);
		} catch (Exception e) {
			throw new DockerException(e);
		}
	}
}
