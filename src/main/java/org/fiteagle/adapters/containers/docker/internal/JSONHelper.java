package org.fiteagle.adapters.containers.docker.internal;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class JSONHelper {
	private JSONHelper() {}

	public static String getStringProperty(
		JsonObject object,
		String property,
		String defaultValue
	) {
		if (!object.has(property))
			return defaultValue;

		return object.get(property).getAsString();
	}

	public static boolean getBooleanProperty(
		JsonObject object,
		String property,
		boolean defaultValue
	) {
		if (!object.has(property))
			return defaultValue;

		JsonElement element = object.get(property);

		if (!element.isJsonPrimitive())
			return defaultValue;

		return element.getAsJsonPrimitive().getAsBoolean();
	}

	public static JsonObject getObjectProperty(
		JsonObject object,
		String property,
		JsonObject defaultValue
	) {
		if (!object.has(property))
			return defaultValue;

		JsonElement element = object.get(property);

		if (!element.isJsonObject())
			return defaultValue;

		return element.getAsJsonObject();
	}

	public static JsonArray getArrayProperty(
		JsonObject object,
		String property,
		JsonArray defaultValue
	) {
		if (!object.has(property))
			return defaultValue;

		JsonElement element = object.get(property);

		if (!element.isJsonArray())
			return defaultValue;

		return element.getAsJsonArray();
	}
}
