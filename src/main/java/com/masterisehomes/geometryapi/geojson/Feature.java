package com.masterisehomes.geometryapi.geojson;

import java.util.HashMap;
import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
class Feature {
    private final String type = "Feature";
    private Geometry geometry;
    private HashMap<Object, Object> properties = new HashMap<>();

    public Feature() {}

    public Feature(Geometry geometry, HashMap<Object, Object> properties) {
        this.geometry = geometry;
        this.properties = properties;
    }

    public void setGeometry(Geometry geometry) {
        this.geometry = geometry;
    }

    public void addProperty(Object key, Object value) {
        this.properties.put(key, value);
    }
}