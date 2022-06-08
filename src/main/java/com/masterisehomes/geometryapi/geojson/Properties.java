package com.masterisehomes.geometryapi.geojson;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;

class Properties<T> {
    HashMap<String, T> properties = new HashMap<String, T>();

    Properties() {}

    // Setters
    public void addProperty(String key, T value) {
        properties.put(key, value);
    }

    // Getter
    public List<String> getKeys() {
        List<String> keys = new ArrayList<String>(this.properties.keySet());
        return keys;
    } 
}