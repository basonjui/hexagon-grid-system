package com.masterisehomes.geometryapi.geojson;

import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
class Feature extends GeoJsonType{
    private Geometry geometry;
    private Properties<?> properties;

    public Feature() {
        super();
        this.type = "Feature";
        checkType(this.type);
    }

    public void addProperties(Properties<?> properties) {
        this.properties = properties;
    }

    public void addGeometry(Geometry geometry) {
        this.geometry = geometry;
    }
}