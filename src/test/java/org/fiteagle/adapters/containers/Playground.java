package org.fiteagle.adapters.containers;

import org.fiteagle.adapters.containers.docker.internal.ContainerConfiguration;
import org.fiteagle.adapters.containers.docker.internal.ContainerInspection;
import org.fiteagle.adapters.containers.docker.internal.DockerClient;
import org.fiteagle.adapters.containers.docker.internal.DockerException;

public class Playground {
	public static void main(String[] args)
		throws DockerException
	{
		DockerClient client = new DockerClient("localhost", 8080);

		// Create container
		ContainerConfiguration config = new ContainerConfiguration("my_container", "ubuntu");
		config.setCommandEasily("/usr/bin/env");
		config.putLabel("is_echo", "true");
		config.putEnvironment("MESSAGE", "HelloWorld");

		System.out.println(config.toJsonObject().toString());

		String containerID = client.createContainer(config);
		System.out.println("Created '" + containerID + "'");

		// Inspect container
		ContainerInspection info = client.inspectContainer(containerID);
		System.out.println(info.config.toJsonObject().toString());

		// Start container
		client.startContainer(containerID);

		// Wait for container to exit
		int statusCode = client.waitContainer(containerID);
		System.out.println("Exited with status code " + statusCode);

		// Delete created container
		client.deleteContainer(containerID, true, true);
	}
}
