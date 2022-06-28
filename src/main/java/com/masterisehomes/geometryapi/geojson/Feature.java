package com.masterisehomes.geometryapi.geojson;

import java.util.HashMap;
import lombok.Getter;
import lombok.ToString;
import java.util.Set;
import java.util.Collection;

@ToString
@Getter
public class Feature {
    private final String type = "Feature";
    private Geometry geometry;
    private HashMap<Object, Object> properties = new HashMap<>();

    public Feature() {
    }

    public Feature(Geometry geometry) {
        this.geometry = geometry;
    }

    // properties methods
    public void addProperty(Object key, Object value) {
        this.properties.put(key, value);
    }

    public void addProperties(HashMap<Object, Object> properties) {
        this.properties.putAll(properties);
    }

    // Getters
    public Object get(Object key) {
        return this.properties.get(key);
    }

    public HashMap<Object, Object> getMap() {
        return this.properties;
    }

    public Set<?> getItems() {
        return this.properties.entrySet();
    }

    public Set<Object> getKeys() {
        return this.properties.keySet();
    }

    public Collection<?> getValues() {
        return this.properties.values();
    }

    public int getSize() {
        return this.properties.size();
    }
}