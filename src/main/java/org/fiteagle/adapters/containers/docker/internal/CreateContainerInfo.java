package org.fiteagle.adapters.containers.docker.internal;

import java.io.UnsupportedEncodingException;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.apache.http.HttpEntity;
import org.apache.http.entity.StringEntity;
import org.apache.jena.atlas.json.JsonArray;
import org.apache.jena.atlas.json.JsonObject;
import org.apache.jena.atlas.json.JsonValue;

/**
 * Capsulates the information required to create a container.
 */
public class CreateContainerInfo {
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
	public String user;

	/**
	 * Container image name
	 */
	public String image;

	/**
	 * Execution environment variables
	 */
	private JsonObject environment;

	/**
	 * Working directory for the commands to be executed in
	 */
	public String workingDirectory;

	/**
	 * Command to be executed inside the container
	 */
	private JsonArray command;

	/**
	 * Container labels
	 */
	private JsonObject labels;

	public CreateContainerInfo(String n, String u, String i) {
		host = domain = n;
		user = u;
		image = i;

		environment = new JsonObject();
		command = new JsonArray();
		workingDirectory = null;

		labels = new JsonObject();
	}

	/**
	 * Set an environment variable.
	 */
	public void putEnvironment(String name, String value) {
		environment.put(name, value);
	}

	/**
	 * Remove an environment variable.
	 */
	public void removeEnvironment(String name) {
		environment.remove(name);
	}

	/**
	 * Fetch all environment variables.
	 */
	public TreeMap<String, String> getEnvironment() {
		TreeMap<String, String> map = new TreeMap<String, String>();

		// This seems cumbersome, there should be another way of copying
		// the key-value associations.
		for (Entry<String, JsonValue> pair: environment.entrySet()) {
			map.put(pair.getKey(), pair.getValue().getAsString().value());
		}

		return map;
	}

	/**
	 * Remove all environment variables.
	 */
	public void clearEnvironment() {
		environment.clear();
	}

	/**
	 * Set the command which will be executed inside the container.
	 */
	public void setCommand(String[] cmd) {
		command.clear();

		for (String c: cmd) {
			command.add(c);
		}
	}

	public void setCommandEasily(String... cmd) {
		setCommand(cmd);
	}

	/**
	 * Tag the container with a label
	 */
	public void putLabel(String key, String value) {
		labels.put(key, value);
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
		for (Entry<String, JsonValue> pair: labels.entrySet()) {
			map.put(pair.getKey(), pair.getValue().getAsString().value());
		}

		return map;
	}

	/**
	 * Remove all labels which are associated with the container.
	 */
	public void clearLabels() {
		labels.clear();
	}

	/**
	 * Generate a JsonObject which can be sent to an API endpoint
	 * to create a new container.
	 */
	public JsonObject toJsonObject() {
		JsonObject resultObject = new JsonObject();

		resultObject.put("Hostname", host);
		resultObject.put("Domainname", domain);
		resultObject.put("User", user);

		resultObject.put("Env", environment);
		resultObject.put("Cmd", command);

		if (workingDirectory != null)
			resultObject.put("WorkingDir", workingDirectory);

		return resultObject;
	}

	/**
	 * Generate the HTTP payload which shall be attached to the request.
	 */
	public HttpEntity toEntity() throws UnsupportedEncodingException {
		return new StringEntity(toJsonObject().toString());
	}
}
