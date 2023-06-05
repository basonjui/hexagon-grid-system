package com.masterisehomes.geometryapi.geojson;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
public class FeatureCollection {
	private final String type = "FeatureCollection";
	private final List<Feature> features = new ArrayList<Feature>(100);

	FeatureCollection() {
	}

	/* Methods */
	public final void addFeature(Feature feature) {
		this.features.add(feature);
	}

	public final Feature getFeature(int index) {
		return this.features.get(index);
	}

	/* Utility */
	public final boolean isEmpty() {
		return this.features.isEmpty();
	}

	public final int size() {
		return this.features.size();
	}
}