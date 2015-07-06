package org.fiteagle.adapters.containers.docker.internal;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class ContainerHandle {
	/**
	 * Container ID
	 */
	public final String id;

	/**
	 * Container name
	 */
	public final String image;

	private ContainerHandle(String containerID, String containerImage) {
		id = containerID;
		image = containerImage;
	}

	/**
	 * Construct a container info using a JSON object.
	 */
	public static ContainerHandle fromJSON(JsonElement value)
		throws DockerException
	{
		// Check type
		if (value == null || !value.isJsonObject())
			throw new DockerException("Container info must be an object");

		JsonObject containerInfo = value.getAsJsonObject();

		// Validate schema
		if (!containerInfo.has("Id") || !containerInfo.has("Image"))
			throw new DockerException("Container info does not match expected schema");

		// Validate 'Image' field
		JsonElement jsonImage = containerInfo.get("Image");
		if (!jsonImage.isJsonPrimitive())
			throw new DockerException("Container info field 'Image' must be a String");

		return new ContainerHandle(
			ResponseParser.extractContainerID(containerInfo.get("Id")),
			jsonImage.getAsString()
		);
	}
}
