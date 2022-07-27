package com.masterisehomes.geometryapi.geojson;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.ToString;

@ToString
public class FeatureCollection {
	private final String type = "FeatureCollection";

	@Getter
	private final List<Feature> features = new ArrayList<Feature>();

	FeatureCollection() {
	}

	// Setter
	public void addFeature(Feature feature) {
		this.features.add(feature);
	}

	// Getter
	public Feature getFeature(int index) {
		return this.features.get(index);
	}

	// Utility
	public boolean isEmpty() {
		return this.features.isEmpty();
	}

	public int size() {
		return this.features.size();
	}
}