package org.fiteagle.adapters.containers.docker.internal;

import java.io.InputStreamReader;
import java.util.LinkedList;

import org.apache.http.HttpResponse;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * Parse HTTP responses according to the expected result type.
 */
public abstract class ResponseParser {
	public static String extractContainerID(JsonElement jsonContainerID)
		throws DockerException
	{
		// Require JsonString instance
		if (jsonContainerID == null || !jsonContainerID.isJsonPrimitive())
			throw new DockerException("Container ID needs to be a String");

		String containerID = jsonContainerID.getAsString();

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
	 * Parse response to a list-container request.
	 * @return List of containers
	 */
	public static LinkedList<ContainerInfo> listContainers(HttpResponse response)
		throws DockerException
	{
		// TODO: More concrete information about failure (different message for each status code)
		if (response.getStatusLine().getStatusCode() != 200)
			throw new DockerException("Failed to list containers");

		JsonElement jsonResult = null;

		// Obtain resulting JSON object
		try {
			jsonResult = new JsonParser().parse(
				new InputStreamReader(response.getEntity().getContent())
			);
		} catch (Exception e) {
			throw new DockerException(e);
		}

		// Validate the result schema
		if (jsonResult == null || !jsonResult.isJsonArray()) {
			throw new DockerException("Unexpected result schema");
		}

		LinkedList<ContainerInfo> containers = new LinkedList<ContainerInfo>();

		for (JsonElement containerValue: jsonResult.getAsJsonArray()) {
			containers.add(ContainerInfo.fromJson(containerValue));
		}

		return containers;
	}

	/**
	 * Parse the response to a create-container request.
	 * @return Container ID
	 */
	public static String createContainer(HttpResponse response)
		throws DockerException
	{
		// TODO: More concrete information about failure (different message for each status code)
		if (response.getStatusLine().getStatusCode() != 201)
			throw new DockerException("Failed to create container");

		JsonElement jsonResult = null;

		// Obtain resulting JSON object
		try {
			jsonResult = new JsonParser().parse(
					new InputStreamReader(response.getEntity().getContent())
			);
		} catch (Exception e) {
			throw new DockerException(e);
		}

		// Validate the result type
		if (jsonResult == null || !jsonResult.isJsonObject()) {
			throw new DockerException("Unexpected result type");
		}

		JsonObject jsonObject = jsonResult.getAsJsonObject();

		// Validate the result schema
		if (!jsonObject.has("Id")) {
			throw new DockerException("Unexpected result schema");
		}

		return extractContainerID(jsonObject.get("Id"));
	}

	/**
	 * Parse response to a delete-container request.
	 * @return false if container did not exist, otherwise true
	 */
	public static boolean deleteContainer(HttpResponse response)
		throws DockerException
	{
		switch (response.getStatusLine().getStatusCode()) {
			case 204:
				return true;

			case 404:
				return false;

			case 400:
				throw new DockerException("Bad parameter to delete-container request");

			case 500:
				throw new DockerException("Server error");

			default:
				throw new DockerException("Unknown status code");
		}
	}
}
