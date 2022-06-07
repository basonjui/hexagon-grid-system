package com.masterisehomes.geometryapi.geojson;

import java.util.List;

public class Feature extends GeoJsonDataType{
    Geometry geometry;
    List<Properties> properties;

    public Feature() {
        super();
        this.type = "Feature";
        checkType(this.type);
    }
}