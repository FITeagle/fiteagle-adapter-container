package org.fiteagle.adapters.containers.docker.internal;

import java.util.LinkedList;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

public class DockerClient {
	private RequestBuilder requestBuilder;
	private CloseableHttpClient httpClient;

	/**
	 * Construct a Docker client for an instance listening at ´tcp://hostname:port´.
	 */
	public DockerClient(String hostname, int port) {
		requestBuilder = new RequestBuilder(hostname, port);
		httpClient = HttpClients.createDefault();
	}

	/**
	 * Create a new container using the given information.
	 */
	public String createContainer(ContainerConfiguration cci)
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

	/**
	 * List containers.
	 * @param all Specify whether to list all containers or not
	 */
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

	/**
	 * Delete a specific container.
	 * @param containerID Container identifier
	 * @param deleteVolume Delete the volume attached to the container
	 * @param force Kill the container before removing it
	 * @return Whether the container has been deleted or not
	 */
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
