package com.masterisehomes.geometryapi.geojson;

import java.util.HashMap;
import lombok.Getter;
import lombok.ToString;
import java.util.Set;
import java.util.Collection;

@ToString
@Getter
class Feature {
    private final String type = "Feature";
    private Geometry geometry;
    private HashMap<Object, Object> properties = new HashMap<>();

    Feature(Geometry geometry) {
        this.geometry = geometry;
    }

    // properties methods
    void addProperty(Object key, Object value) {
        this.properties.put(key, value);
    }

    void addProperties(HashMap<Object, Object> properties) {
        this.properties.putAll(properties);
    }

    // key:value getters
    Object getPropertyByKey(Object key) {
        return this.properties.get(key);
    }

    Set<?> getPropertiesItems() {
        return this.properties.entrySet();
    }

    Set<Object> getPropertiesKeys() {
        return this.properties.keySet();
    }

    Collection<?> getPropertiesValues() {
        return this.properties.values();
    }
}