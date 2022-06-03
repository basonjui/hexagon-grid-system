package com.masterisehomes.geometryapi.geojson;

import java.util.List;
import java.util.Arrays;

public abstract class GeoJsonObject {
    protected String type;

    public GeoJsonObject(String type) throws IllegalArgumentException {
        List<String> validGeoJsonTypes = Arrays.asList("FeatureCollection", "Feature", "");

        if (validGeoJsonTypes.contains(type)) {
            this.type = type;
        } else {
            throw new IllegalArgumentException("Invalid GeoJSON type, allowed types are: " + validGeoJsonTypes); 
        }
    }
}