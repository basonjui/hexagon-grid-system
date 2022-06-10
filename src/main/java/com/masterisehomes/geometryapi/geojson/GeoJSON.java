package com.masterisehomes.geometryapi.geojson;

import lombok.Getter;
import lombok.ToString;

@ToString
public class GeoJSON {
    @Getter private final FeatureCollection featureCollection = new FeatureCollection();

    public GeoJSON() {}

    public void addFeature(Feature feature) {
        this.featureCollection.add(feature);
    }

    public void addFeature(Geometry geometry) {
        Feature feature = new Feature(geometry);
        this.featureCollection.add(feature);
    }

    public void addFeature(Geometry geometry, Properties properties) {
        // Todo: add logic to add a Feature using Geometry and Properties
        Feature feature = new Feature(geometry);
        feature.addProperties(properties);
        this.featureCollection.add(feature);
    }
}

