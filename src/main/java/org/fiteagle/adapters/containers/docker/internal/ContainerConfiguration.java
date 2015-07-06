package org.fiteagle.adapters.containers.docker.internal;

import java.io.UnsupportedEncodingException;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.apache.http.HttpEntity;
import org.apache.http.entity.StringEntity;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

/**
 * Capsulates the information required to create a container.
 */
public class ContainerConfiguration {
	/**
	 * Container host name
	 */
	public String host;

	/**
	 * Container domain name
	 */
	public String domain;

	/**
	 * User inside the container
	 */
	public String user = null;

	/**
	 * Container image name
	 */
	public String image;

	/**
	 * Working directory for the commands to be executed in
	 */
	public String workingDirectory = null;

	/**
	 * Allocate a pseudo-terminal for the container
	 */
	boolean tty = false;

	/**
	 * Execution environment variables
	 */
	private JsonArray environment;

	/**
	 * Command to be executed inside the container
	 */
	private JsonArray command;

	/**
	 * Container labels
	 */
	private JsonObject labels;

	public ContainerConfiguration(String containerName, String containerImage) {
		host = domain = containerName;
		image = containerImage;

		environment = new JsonArray();
		command = new JsonArray();
		labels = new JsonObject();
	}

	/**
	 * Set an environment variable.
	 */
	public void putEnvironment(String name, String value) {
		environment.add(new JsonPrimitive(name + "=" + value));
	}

	/**
	 * Remove all environment variables.
	 */
	public void clearEnvironment() {
		environment = new JsonArray();
	}

	/**
	 * Set the command which will be executed inside the container.
	 */
	public void setCommand(String[] cmd) {
		command = new JsonArray();

		for (String c: cmd) {
			command.add(new JsonPrimitive(c));
		}
	}

	public void setCommandEasily(String... cmd) {
		setCommand(cmd);
	}

	/**
	 * Tag the container with a label
	 */
	public void putLabel(String key, String value) {
		labels.addProperty(key, value);
	}

	/**
	 * Remove a label from the container.
	 */
	public void removeLabel(String key) {
		labels.remove(key);
	}

	/**
	 * Fetch all labels associated with the container.
	 */
	public TreeMap<String, String> getLabels() {
		TreeMap<String, String> map = new TreeMap<String, String>();

		// This seems cumbersome, there should be another way of copying
		// the key-value associations.
		for (Entry<String, JsonElement> pair: labels.entrySet()) {
			map.put(pair.getKey(), pair.getValue().getAsString());
		}

		return map;
	}

	/**
	 * Remove all labels which are associated with the container.
	 */
	public void clearLabels() {
		labels = new JsonObject();
	}

	/**
	 * Generate a JsonObject which can be sent to an API endpoint
	 * to create a new container.
	 */
	public JsonObject toJsonObject() {
		JsonObject resultObject = new JsonObject();

		if (host != null)
			resultObject.addProperty("Hostname", host);

		if (domain != null)
			resultObject.addProperty("Domainname", domain);

		if (user != null)
			resultObject.addProperty("User", user);

		if (user != null)
			resultObject.addProperty("Image", image);

		if (workingDirectory != null)
			resultObject.addProperty("WorkingDir", workingDirectory);

		resultObject.addProperty("Tty", tty);
		resultObject.add("Env", environment);
		resultObject.add("Cmd", command);

		return resultObject;
	}

	/**
	 * Generate the HTTP payload which shall be attached to the request.
	 */
	public HttpEntity toEntity() throws UnsupportedEncodingException {
		return new StringEntity(toJsonObject().toString());
	}
}
