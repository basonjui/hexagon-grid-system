package com.masterisehomes.geometryapi.geojson;

import java.util.Map;
import java.util.Set;
import java.util.Collection;
import java.util.LinkedHashMap;

import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
class Feature {
	// private final int ccid_q;
	// private final int ccid_r;
	// private final int ccid_s;
	private final String type = "Feature";
	private final Geometry geometry;
	private final Map<String, Object> properties = new LinkedHashMap<String, Object>();

	Feature(Geometry geometry) {
		this.geometry = geometry;

		// Geometry is an abstract class, check concrete class to determine initialization logic
		/* no longer used
		if (geometry instanceof PolygonGeometry) {
			this.ccid_q = ((PolygonGeometry) geometry).getHexagon().getCCI().getQ();
			this.ccid_r = ((PolygonGeometry) geometry).getHexagon().getCCI().getR();
			this.ccid_s = ((PolygonGeometry) geometry).getHexagon().getCCI().getS();
		} else {
			// TODO: handle logic for each Geometry
			this.ccid_q = 0;
			this.ccid_r = 0;
			this.ccid_s = 0;
		}
		*/
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