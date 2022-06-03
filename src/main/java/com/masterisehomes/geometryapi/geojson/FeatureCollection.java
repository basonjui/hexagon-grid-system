package com.masterisehomes.geometryapi.geojson;

import java.util.List;

public class FeatureCollection extends GeoJsonObject{
    List<Feature> features;

    public FeatureCollection() {
        super();
        this.type = "FeatureCollection";
        checkType(this.type);
    }

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