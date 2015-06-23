package org.fiteagle.adapters.containers.docker.internal;

import org.apache.http.HttpResponse;
import org.apache.jena.atlas.json.JSON;
import org.apache.jena.atlas.json.JsonObject;
import org.apache.jena.atlas.json.JsonValue;

/**
 * Parse HTTP responses according to the expected result type.
 */
public abstract class ResponseParser {
	private static String extractContainerID(JsonValue jsonContainerID)
		throws DockerException
	{
		// Require JsonString instance
		if (jsonContainerID == null || !jsonContainerID.isString())
			throw new DockerException("Container ID needs to be a String");

		String containerID = jsonContainerID.getAsString().value();

		// Container IDs must be a base64-encoded
		for (byte chr: containerID.getBytes()) {
			if (('a' > chr || chr > 'z') && ('A' > chr || chr > 'Z') && ('0' > chr || chr > '9')) {
				throw new DockerException(
					"Given Container ID is not a base64-encoded String"
				);
			}
		}

		return containerID;
	}

	/**
	 * Parse the response to a create-container request.
	 * @return Container ID
	 */
	public static String createContainer(HttpResponse response)
		throws DockerException
	{
		if (response.getStatusLine().getStatusCode() != 201)
			throw new DockerException("Failed to create container");

		JsonObject jsonResult = null;

		// Obtain resulting JSON object
		try {
			jsonResult = JSON.parse(response.getEntity().getContent());
		} catch (Exception e) {
			throw new DockerException(e);
		}

		// Validate the result schema
		if (jsonResult == null || !jsonResult.hasKey("Id")) {
			throw new DockerException("Unexpected result schema");
		}

		return extractContainerID(jsonResult.get("Id"));
	}
}
