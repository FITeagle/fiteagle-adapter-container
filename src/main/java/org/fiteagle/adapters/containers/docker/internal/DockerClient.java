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
	public String create(ContainerConfiguration cci)
		throws DockerException
	{
		try {
			return ResponseParser.create(
				httpClient.execute(requestBuilder.create(cci))
			);
		} catch (Exception e) {
			throw new DockerException(e);
		}
	}

	/**
	 * List containers.
	 * @param all Specify whether to list all containers or not
	 */
	public LinkedList<ContainerHandle> list(boolean all)
		throws DockerException
	{
		try {
			return ResponseParser.list(
				httpClient.execute(requestBuilder.list(all))
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
	public boolean delete(String containerID, boolean deleteVolume, boolean force)
		throws DockerException
	{
		try {
			return ResponseParser.delete(
				httpClient.execute(requestBuilder.delete(containerID, deleteVolume, force))
			);
		} catch (Exception e) {
			throw new DockerException(e);
		}
	}

	/**
	 * Inspect a container.
	 * @param containerID Container identifier
	 */
	public ContainerInspection inspect(String containerID)
		throws DockerException
	{
		try {
			return ResponseParser.inspect(
				httpClient.execute(requestBuilder.inspect(containerID))
			);
		} catch (Exception e) {
			throw new DockerException(e);
		}
	}

	/**
	 * Start a container.
	 * @param containerID Container identifier
	 * @return true if the container has been started, false if it was already started
	 */
	public boolean start(String containerID)
		throws DockerException
	{
		try {
			return ResponseParser.start(
				httpClient.execute(requestBuilder.start(containerID))
			);
		} catch (Exception e) {
			throw new DockerException(e);
		}
	}

	/**
	 * Stop a container.
	 * @param containerID Container identifier
	 * @param timeout Seconds to wait before killing the container
	 * @return true if the container has been stop, false if it was not active in the first place
	 */
	public boolean stop(String containerID, int timeout)
		throws DockerException
	{
		try {
			return ResponseParser.stop(
				httpClient.execute(requestBuilder.stop(containerID, timeout))
			);
		} catch (Exception e) {
			throw new DockerException(e);
		}
	}

	/**
	 * Kill a container.
	 * @param containerID Container identifier
	 * @param signal Specify a signal name to kill the container process with
	 */
	public void kill(String containerID, String signal)
		throws DockerException
	{
		try {
			ResponseParser.kill(
				httpClient.execute(requestBuilder.kill(containerID, signal))
			);
		} catch (Exception e) {
			throw new DockerException(e);
		}
	}

	/**
	 * Restart a container.
	 * @param containerID Container identifier
	 * @param timeout Seconds to wait before killing the container
	 */
	public void restart(String containerID, int timeout)
		throws DockerException
	{
		try {
			ResponseParser.restart(
				httpClient.execute(requestBuilder.restart(containerID, timeout))
			);
		} catch (Exception e) {
			throw new DockerException(e);
		}
	}

	/**
	 * Wait for a container to exit.
	 * @param containerID Container identifier
	 * @return Status code upon exit
	 */
	public int waitFor(String containerID)
		throws DockerException
	{
		try {
			return ResponseParser.waitFor(
				httpClient.execute(requestBuilder.waitFor(containerID))
			);
		} catch (Exception e) {
			throw new DockerException(e);
		}
	}
}
