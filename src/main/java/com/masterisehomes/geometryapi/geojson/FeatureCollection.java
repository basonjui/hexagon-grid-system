package com.masterisehomes.geometryapi.geojson;

import java.util.ArrayList;
import java.util.List;

public class FeatureCollection extends GeoJsonDataType{
    List<Feature> features = new ArrayList<Feature>();

    public FeatureCollection() {
        super();
        checkType(this.type);
        this.type = "FeatureCollection";
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