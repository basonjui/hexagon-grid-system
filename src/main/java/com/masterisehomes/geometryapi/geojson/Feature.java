package com.masterisehomes.geometryapi.geojson;

import java.util.Map;
import java.util.Set;
import java.util.Collection;
import java.util.HashMap;

import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
class Feature {
	private final String type = "Feature";
	private final Geometry geometry;
	private final Map<String, Object> properties = new HashMap<String, Object>();

	Feature(Geometry geometry) {
		this.geometry = geometry;
	}

	// properties methods
	void addProperty(String key, Object value) {
		this.properties.put(key, value);
	}

	void addProperties(Map<String, Object> properties) {
		this.properties.putAll(properties);
	}

	// key:value getters
	Object getProperty(String key) {
		return this.properties.get(key);
	}

	Set<?> getPropertiesItems() {
		return this.properties.entrySet();
	}

	Set<String> getPropertiesKeys() {
		return this.properties.keySet();
	}

	Collection<?> getPropertiesValues() {
		return this.properties.values();
	}
}