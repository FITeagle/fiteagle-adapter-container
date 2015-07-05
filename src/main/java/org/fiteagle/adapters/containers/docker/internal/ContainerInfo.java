package org.fiteagle.adapters.containers.docker.internal;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class ContainerInfo {
	public final String id;
	public final String image;

	private ContainerInfo(String containerID, String containerImage) {
		id = containerID;
		image = containerImage;
	}

	public static ContainerInfo fromJson(JsonElement value)
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

		return new ContainerInfo(
			ResponseParser.extractContainerID(containerInfo.get("Id")),
			jsonImage.getAsString()
		);
	}
}
