package com.masterisehomes.geometryapi.geojson;

import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
class Feature {
    private String type = "Feature";
    private Geometry geometry;
    private Properties<?> properties;

    public Feature() {}

    public void addProperties(Properties<?> properties) {
        this.properties = properties;
    }

    public void addGeometry(Geometry geometry) {
        this.geometry = geometry;
    }
}