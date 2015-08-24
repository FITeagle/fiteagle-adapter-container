package org.fiteagle.adapters.containers.docker;

public class DockerControlException extends Exception {
	private static final long serialVersionUID = 2301855973579526791L;

	public DockerControlException(String message) {
		super(message);
	}

	public DockerControlException(Throwable cause) {
		super(cause);
	}

	public DockerControlException(String message, Throwable cause) {
		super(message, cause);
	}
}

