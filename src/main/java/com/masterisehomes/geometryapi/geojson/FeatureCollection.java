package com.masterisehomes.geometryapi.geojson;

import java.util.ArrayList;
import java.util.List;

import lombok.ToString;

@ToString
class FeatureCollection  {
    private String type = "FeatureCollection";
    List<Feature> features = new ArrayList<Feature>();

    FeatureCollection() {}

    // Methods
    public void add(Feature feature) {
        this.features.add(feature);
    }

    public boolean isEmpty() {
        return this.features.isEmpty();
    }

    public int size() {
        return this.features.size();
    }
}