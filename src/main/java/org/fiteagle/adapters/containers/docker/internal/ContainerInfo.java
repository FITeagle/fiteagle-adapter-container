package org.fiteagle.adapters.containers.docker.internal;

public class ContainerInfo {
	public final String id;
	public final String image;

	public ContainerInfo(String containerID, String containerImage) {
		id = containerID;
		image = containerImage;
	}
}
