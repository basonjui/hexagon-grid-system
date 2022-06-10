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

    public Feature(Geometry geometry) {
        this.geometry = geometry;
    }

    public void addProperty(Object key, Object value) {
        this.properties.put(key, value);
    }

    public void addProperties(Properties properties) {
        HashMap<Object, Object> map = properties.getMap();
        this.properties.putAll(map);
    }
}