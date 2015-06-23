package org.fiteagle.adapters.containers.docker.internal;

public class DockerException extends Exception {
	private static final long serialVersionUID = 2360964686090074643L;

	public DockerException(String message) {
		super(message);
	}

	public DockerException(Throwable cause) {
		super(cause);
	}

	public DockerException(String message, Throwable cause) {
		super(message, cause);
	}
}
