package org.fiteagle.adapters.containers.docker.internal;

import com.google.gson.JsonObject;

public class VersionInformation {
	private String operatingSystem;
	private String version;
	private String apiVersion;

	private VersionInformation(String os, String ver, String apiver) {
		operatingSystem = os;
		version = ver;
		apiVersion = apiver;
	}

	public String getOperatingSystem() {
		return operatingSystem;
	}

	public String getVersion() {
		return version;
	}

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

	public static VersionInformation fromJSON(JsonObject asJsonObject) {
		return new VersionInformation(
			JSONHelper.getStringProperty(asJsonObject, "Os", null),
			JSONHelper.getStringProperty(asJsonObject, "Version", null),
			JSONHelper.getStringProperty(asJsonObject, "ApiVersion", null)
		);
	}
}
