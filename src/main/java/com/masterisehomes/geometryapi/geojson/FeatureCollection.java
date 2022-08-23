package com.masterisehomes.geometryapi.geojson;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
public class FeatureCollection {
	private final String type = "FeatureCollection";
	private final List<Feature> features = new ArrayList<Feature>();

	FeatureCollection() {
	}

	/* Methods */
	public void addFeature(Feature feature) {
		this.features.add(feature);
	}

	public Feature getFeature(int index) {
		return this.features.get(index);
	}

	/* Utility */
	public boolean isEmpty() {
		return this.features.isEmpty();
	}

	public int size() {
		return this.features.size();
	}
}