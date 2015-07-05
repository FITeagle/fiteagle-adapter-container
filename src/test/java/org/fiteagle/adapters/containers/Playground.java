package org.fiteagle.adapters.containers;

import org.fiteagle.adapters.containers.docker.internal.ContainerInfo;
import org.fiteagle.adapters.containers.docker.internal.CreateContainerInfo;
import org.fiteagle.adapters.containers.docker.internal.DockerClient;
import org.fiteagle.adapters.containers.docker.internal.DockerException;

public class Playground {
	public static void main(String[] args)
		throws DockerException
	{
		DockerClient client = new DockerClient("localhost", 8080);

		// Create container
		String containerID =
			client.createContainer(new CreateContainerInfo("my_container", "ubuntu"));

		System.out.println("Created '" + containerID + "'");

		// List containers
		for (ContainerInfo ci: client.listContainers(true)) {
			System.out.println("Found container: '" + ci.id + "' (" + ci.image + ")");
		}
	}
}
