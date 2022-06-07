package com.masterisehomes.geometryapi.geojson;

import java.util.ArrayList;
import java.util.List;


public class Geometry extends GeoJsonDataType{
    private List<?> coordinates;

    public Geometry(String type) {
        super();
        checkType(type);
        this.type = type;

        switch(this.type) {
            case "Point":
            coordinates = new ArrayList<Double>();
            break;

            case "LineString":
            coordinates = new ArrayList<List<Double>>();
            break;

            case "Polygon":
            coordinates = new ArrayList<List<List<Double>>>();
        }
    }
}
