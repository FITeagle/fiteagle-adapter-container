package org.fiteagle.adapters.containers.docker.internal;

import com.google.gson.JsonObject;

/**
 * Packs the version information retrieve from the Docker API.
 */
public class VersionInformation {
	private String operatingSystem;
	private String version;
	private String apiVersion;

	private VersionInformation(String os, String ver, String apiver) {
		operatingSystem = os;
		version = ver;
		apiVersion = apiver;
	}

	/**
	 * On which operating system is Docker running?
	 */
	public String getOperatingSystem() {
		return operatingSystem;
	}

	/**
	 * Get the Docker version.
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * Get the Docker API Version.
	 */
	public String getAPIVersion() {
		return apiVersion;
	}

	@Override
	public String toString() {
		return
			"VersionInformation {OS = "
				+ operatingSystem
				+ ", Version = "
				+ version
				+ ", APIVersion = "
				+ apiVersion
				+ "}";
	}

	/**
	 * Parse a JSON object containing version information.
	 * @param asJsonObject
	 */
	public static VersionInformation fromJSON(JsonObject asJsonObject) {
		return new VersionInformation(
			JSONHelper.getStringProperty(asJsonObject, "Os", null),
			JSONHelper.getStringProperty(asJsonObject, "Version", null),
			JSONHelper.getStringProperty(asJsonObject, "ApiVersion", null)
		);
	}
}
