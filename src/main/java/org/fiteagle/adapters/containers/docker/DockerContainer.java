package org.fiteagle.adapters.containers.docker;

import org.fiteagle.adapters.containers.docker.internal.DockerClient;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;

public class DockerContainer {
	private DockerClient client;

	public DockerContainer(DockerClient dockerClient) {
		client = dockerClient;
	}

	public void update(Resource resource) {

	}

	public void delete() {

	}

	public Model serializeModel() {
		return null;
	}
}
