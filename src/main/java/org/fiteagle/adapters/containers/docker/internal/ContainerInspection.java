package org.fiteagle.adapters.containers.docker.internal;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class ContainerInspection {
	/**
	 * Container configuration
	 */
	public final ContainerConfiguration config;

	private ContainerInspection(ContainerConfiguration cconfig) {
		config = cconfig;
	}

	/**
	 * Parse inspection information from a JSON object.
	 */
	public static ContainerInspection fromJSON(JsonElement element)
		throws DockerException
	{
		if (element == null || !element.isJsonObject())
			throw new DockerException("Container inspection info must be an object");

		JsonObject object = element.getAsJsonObject();

		// Configuration
		if (!object.has("Config"))
			throw new DockerException("Container inspection info does not match expected schema");

		ContainerConfiguration config = ContainerConfiguration.fromJSON(object.get("Config"));

		// Assemble
		return new ContainerInspection(config);
	}
}
