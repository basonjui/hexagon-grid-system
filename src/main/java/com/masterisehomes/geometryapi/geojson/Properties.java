package com.masterisehomes.geometryapi.geojson;

import java.util.HashMap;
import java.util.Set;

import lombok.ToString;

import java.util.Collection;

// A temporary data storage and interface to add properties into Feature
// The object itself will not go into the GeoJSON
@ToString
public class Properties {
    private HashMap<Object, Object> properties = new HashMap<>();

    public Properties() {}

    // Setters
    public void addProperty(Object key, Object value) {
        this.properties.put(key, value);
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
