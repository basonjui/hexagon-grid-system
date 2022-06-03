package com.masterisehomes.geometryapi.geojson;

import java.util.List;

public class Feature extends GeoJsonDataType{
    List<Property> properties;
    Geometry geometry;

    public Feature() {
        super();
        this.type = "Feature";
        checkType(this.type);
    }
}