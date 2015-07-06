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
		config.setCommandEasily("/bin/echo", "$MESSAGE");
		config.putLabel("is_echo", "true");
		config.putEnvironment("MESSAGE", "Hello World");

		System.out.println(config.toJsonObject().toString());

		String containerID = client.createContainer(config);
		System.out.println("Created '" + containerID + "'");

		// Inspect container
		ContainerInspection info = client.inspectContainer(containerID);
		System.out.println(info.config.toJsonObject().toString());

		// Delete created container
		client.deleteContainer(containerID, true, true);
	}
}
