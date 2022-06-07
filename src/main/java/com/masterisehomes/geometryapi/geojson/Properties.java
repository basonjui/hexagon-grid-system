package com.masterisehomes.geometryapi.geojson;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import com.google.gson.Gson;

public class Properties {
    HashMap<String, Object> properties = new HashMap<String, Object>();

    public Properties() {}

    // Setters
    public void addProperty(String key, Object value) {
        properties.put(key, value);
    }

    // Getter
    public List<String> getKeys() {
        List<String> keys = new ArrayList<String>(this.properties.keySet());
        return keys;
    } 

    public static void main(String[] args) {
        Gson gson = new Gson();
        Properties obj = new Properties();
        obj.addProperty("someKey", "someValue");

        String jsonString = gson.toJson(obj);
        System.out.println(jsonString);
    }
}