package org.fiteagle.adapters.containers.docker.internal;

import java.io.InputStreamReader;
import java.util.LinkedList;

import org.apache.http.HttpResponse;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

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
	public static LinkedList<ContainerHandle> listContainers(HttpResponse response)
		throws DockerException
	{
		int statusCode = response.getStatusLine().getStatusCode();
		if (statusCode != 200) {
			switch (statusCode) {
				case 400:
					throw new DockerException("Bad parameter");

				case 500:
					throw new DockerException("Server error");

				default:
					throw new DockerException("Unknown status code");
			}
		}


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

		LinkedList<ContainerHandle> containers = new LinkedList<ContainerHandle>();

		for (JsonElement containerValue: jsonResult.getAsJsonArray()) {
			containers.add(ContainerHandle.fromJSON(containerValue));
		}

		return containers;
	}

	/**
	 * Parse response to a create-container request.
	 * @return Container ID
	 */
	public static String createContainer(HttpResponse response)
		throws DockerException
	{
		int statusCode = response.getStatusLine().getStatusCode();
		if (statusCode != 201) {
			switch (statusCode) {
				case 404:
					throw new DockerException("No such image");

				case 406:
					throw new DockerException("Impossible to attach");

				case 500:
					throw new DockerException("Server error");

				default:
					throw new DockerException("Unknown status code");
			}
		}

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

	/**
	 * Parse response to a inspect-container request.
	 * @return Container configuration
	 */
	public static ContainerInspection inspectContainer(HttpResponse response)
		throws DockerException
	{
		int statusCode = response.getStatusLine().getStatusCode();
		if (statusCode != 200) {
			switch (statusCode) {
				case 404:
					throw new DockerException("Container not found");

				case 500:
					throw new DockerException("Server error");

				default:
					throw new DockerException("Unknown status code");
			}
		}

		JsonElement jsonResult = null;

		// Obtain resulting JSON object
		try {
			jsonResult = new JsonParser().parse(
				new InputStreamReader(response.getEntity().getContent())
			);
		} catch (Exception e) {
			throw new DockerException(e);
		}

		return ContainerInspection.fromJSON(jsonResult);
	}

	/**
	 * Parse response to a start-container request.
	 * @return true if the container has been started, false if it was already started
	 */
	public static boolean startContainer(HttpResponse response)
		throws DockerException
	{
		switch (response.getStatusLine().getStatusCode()) {
			case 204:
				return true;

			case 304:
				return false;

			case 404:
				throw new DockerException("Container not found");

			case 500:
				throw new DockerException("Server error");

			default:
				throw new DockerException("Unknown status code");
		}
	}

	/**
	 * Parse response to a stop-container request.
	 * @return true if the container has been stop, false if it was not active in the first place
	 */
	public static boolean stopContainer(HttpResponse response)
		throws DockerException
	{
		switch (response.getStatusLine().getStatusCode()) {
			case 204:
				return true;

			case 304:
				return false;

			case 404:
				throw new DockerException("Container not found");

			case 500:
				throw new DockerException("Server error");

			default:
				throw new DockerException("Unknown status code");
		}
	}

	/**
	 * Parse response to a kill-container request.
	 */
	public static void killContainer(HttpResponse response)
		throws DockerException
	{
		switch (response.getStatusLine().getStatusCode()) {
			case 204:
				return;

			case 404:
				throw new DockerException("Container not found");

			case 500:
				throw new DockerException("Server error");

			default:
				throw new DockerException("Unknown status code");
		}
	}

	/**
	 * Parse response to a restart-container request.
	 */
	public static void restartContainer(HttpResponse response)
		throws DockerException
	{
		switch (response.getStatusLine().getStatusCode()) {
			case 204:
				return;

			case 404:
				throw new DockerException("Container not found");

			case 500:
				throw new DockerException("Server error");

			default:
				throw new DockerException("Unknown status code");
		}
	}

	/**
	 * Parse response to a wait-container request.
	 * @return Status code upon exit
	 */
	public static int waitContainer(HttpResponse response)
		throws DockerException
	{
		int statusCode = response.getStatusLine().getStatusCode();
		if (statusCode != 200) {
			switch (statusCode) {
				case 404:
					throw new DockerException("Container not found");

				case 500:
					throw new DockerException("Server error");

				default:
					throw new DockerException("Unknown status code");
			}
		}

		JsonElement jsonResult = null;

		// Obtain resulting JSON object
		try {
			jsonResult = new JsonParser().parse(
				new InputStreamReader(response.getEntity().getContent())
			);
		} catch (Exception e) {
			throw new DockerException(e);
		}

		if (jsonResult == null || !jsonResult.isJsonObject())
			throw new DockerException("Unexpected result type");

		JsonObject object = jsonResult.getAsJsonObject();

		if (!object.has("StatusCode"))
			throw new DockerException("Unexpected result schema");

		JsonElement statusElem = object.get("StatusCode");

		if (!statusElem.isJsonPrimitive())
			throw new DockerException("Expected status code to be a JSON primitive");

		JsonPrimitive statusPrim = statusElem.getAsJsonPrimitive();

		return statusPrim.getAsInt();
	}
}
