package com.masterisehomes.geometryapi.geojson;

import java.util.List;
import java.util.Arrays;

public abstract class GeoJsonDataType {
    protected String type;

    public GeoJsonDataType() {

    }

    public void checkType(String type) throws IllegalArgumentException {
        List<String> validGeoJsonTypes = Arrays.asList(
                "FeatureCollection", "Feature", "Point", "LineString", "Polygon", "MultiPoint", "MultiLineString", "MultiPolygon");

        if (validGeoJsonTypes.contains(type)) {
            this.type = type;
        } else {
            throw new IllegalArgumentException("Invalid GeoJSON type, allowed types are: " + validGeoJsonTypes);
        }
    }
}