package com.masterisehomes.geometryapi.geojson;

import lombok.Getter;
import lombok.ToString;

@ToString
public class GeoJsonManager {
    @Getter
    private final FeatureCollection featureCollection = new FeatureCollection();

    public GeoJsonManager() {
    }

    // GeoJSON methods
    public void addFeature(Feature feature) {
        this.featureCollection.add(feature);
    }

    public void addFeature(Geometry geometry) {
        Feature feature = new Feature(geometry);
        this.featureCollection.add(feature);
    }

    // Utility methods
    public int getHashCode() {
        return this.featureCollection.hashCode();
    }
}
