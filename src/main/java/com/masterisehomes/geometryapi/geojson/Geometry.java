package com.masterisehomes.geometryapi.geojson;

import java.util.ArrayList;
import java.util.List;

import com.masterisehomes.geometryapi.hexagon.*;


public class Geometry extends GeoJsonDataType{
    private List<?> coordinates;

    public Geometry(String type) {
        super();
        checkType(type);
        this.type = type;

        switch(this.type) {
            case "Point":
            {
                this.coordinates = new ArrayList<Double>();
                break;
            }

            case "LineString":
            {
                this.coordinates = new ArrayList<List<Double>>();
                break;
            }

            case "Polygon":
            {
                this.coordinates = new ArrayList<List<List<Double>>>();
                break;
            }
        }
    }

    public Geometry(Hexagon hexagon) {
        super();
        this.type = "Polygon";
        this.coordinates = new ArrayList<>();

        GeoJsonHelper helper = new GeoJsonHelper();
        List<List<Double>> hexagonGeoJsonCoordinates = helper.getGeoJsonCoordinates(hexagon);
        this.coordinates.add((hexagonGeoJsonCoordinates));
    }

    // Setters
    public void setCoordinates() {

    }

    public static void main(String[] args) {
        Coordinates coord = new Coordinates(100, 100);

        Geometry test = new Geometry("Point");
    }
}
