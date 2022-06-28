package com.masterisehomes.geometryapi.geojson;

import java.util.ArrayList;
import java.util.List;

import lombok.ToString;

@ToString
class FeatureCollection {
    private final String type = "FeatureCollection";
    private final List<Feature> features = new ArrayList<Feature>();

    FeatureCollection() {
    }

    // Setter
    public void add(Feature feature) {
        this.features.add(feature);
    }

    // Getter
    public Feature get(int index) {
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