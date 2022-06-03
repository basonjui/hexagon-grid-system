package com.masterisehomes.geometryapi.geojson;

public class Geometry extends GeoJsonDataType{
    public Geometry(String type) {
        super();
        checkType(type);
        this.type = type;
    }
}
