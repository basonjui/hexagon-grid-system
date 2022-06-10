package com.masterisehomes.geometryapi.geojson;

import java.util.HashMap;
import java.util.Set;
import java.util.Collection;

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
