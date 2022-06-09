package com.masterisehomes.geometryapi.geojson;

import java.util.HashMap;
import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
class Feature {
    private final String type = "Feature";
    private Geometry geometry;
    private HashMap<String, ?> properties;

    public Feature() {}

    public Feature(Geometry geometry, HashMap<String, ?> properties) {
        this.geometry = geometry;
        this.properties = properties;
    }

    public void setGeometry(Geometry geometry) {
        this.geometry = geometry;
    }
}