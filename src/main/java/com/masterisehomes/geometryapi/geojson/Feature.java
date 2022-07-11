package com.masterisehomes.geometryapi.geojson;

import java.util.Map;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.Collection;

import lombok.Getter;
import lombok.ToString;


@ToString
@Getter
class Feature {
    private final String type = "Feature";
    private Geometry geometry;
    private Map<Object, Object> properties = new LinkedHashMap<Object, Object>();

    Feature(Geometry geometry) {
        this.geometry = geometry;
    }

    // properties methods
    void addProperty(Object key, Object value) {
        this.properties.put(key, value);
    }

    void addProperties(Map<Object, Object> properties) {
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