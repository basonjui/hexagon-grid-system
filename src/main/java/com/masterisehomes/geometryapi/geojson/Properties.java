package com.masterisehomes.geometryapi.geojson;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import com.google.gson.Gson;

public class Properties<K, V> {
    HashMap<K, V> properties = new HashMap<K, V>();

    public Properties() {}

    // Setters
    public void addProperty(K key, V value) {
        properties.put(key, value);
    }

    // Getter
    public List<K> getKeys() {
        List<K> keys = new ArrayList<K>(this.properties.keySet());
        return keys;
    } 

    public static void main(String[] args) {
        Gson gson = new Gson();
        Properties<String, String> obj = new Properties<String, String>();
        obj.addProperty("someKey", "someValue");

        String jsonString = gson.toJson(obj);
        System.out.println(obj.getKeys());
    }
}