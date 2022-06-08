package com.masterisehomes.geometryapi.geojson;

import java.util.List;
import java.util.Arrays;

abstract class GeoJsonType {
    protected String type;

    GeoJsonType() {}

    public void checkType(String type) throws IllegalArgumentException {
        List<String> validGeoJsonTypes = Arrays.asList(
            "Feature", "FeatureCollection",
            "Point", "MultiPoint", 
            "LineString", "MultiLineString", 
            "Polygon", "MultiPolygon"
        );

        if (validGeoJsonTypes.contains(type)) {
            this.type = type;
        } else {
            throw new IllegalArgumentException("Invalid GeoJSON type, allowed types are: " + validGeoJsonTypes);
        }
    }
}