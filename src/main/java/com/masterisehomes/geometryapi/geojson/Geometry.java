package com.masterisehomes.geometryapi.geojson;

import java.util.ArrayList;
import java.util.List;


public class Geometry extends GeoJsonDataType{
    private List<?> coordinates = new ArrayList<>(); // store List<List<Double>>

    public Geometry(String type) {
        super();
        checkType(type);
        this.type = type;

        switch(this.type) {
            case "Point":
            ;

            case "LineString":
            ;

            case "Polygon":
            ;
        }
    }
}
