package com.masterisehomes.geometryapi.geojson;

import java.util.ArrayList;
import java.util.List;

import com.masterisehomes.geometryapi.hexagon.*;
import lombok.ToString;

@ToString
public class Geometry {
    private String type;
    private List<?> coordinates;

    // public Geometry(String type) {
    //     super();
    //     checkType(type);
    //     this.type = type;

    //     switch(this.type) {
    //         case "Point":
    //         {
    //             List<Double> tempCoordinates = new ArrayList<Double>();
    //             this.coordinates = tempCoordinates;
    //             break;
    //         }

    //         case "LineString":
    //         {
    //             List<List<Double>> tempCoordinates =  new ArrayList<List<Double>>();
    //             this.coordinates = tempCoordinates;
    //             break;
    //         }

    //         case "Polygon":
    //         {
    //             List<List<List<Double>>> tempCoordinates = new ArrayList<List<List<Double>>>();
    //             this.coordinates = tempCoordinates;
    //             break;
    //         }
    //     }
    // }

    public Geometry(Hexagon hexagon) {
        // super();
        this.type = "Polygon";
        this.coordinates = new ArrayList<>();

        GeoJsonHelper helper = new GeoJsonHelper();
        List<List<Double>> hexagonGeoJsonCoordinates = helper.getGeoJsonCoordinates(hexagon);

        List<List<List<Double>>> tempCoordinates = new ArrayList<List<List<Double>>>();
        tempCoordinates.add(hexagonGeoJsonCoordinates);
        this.coordinates = tempCoordinates;
    }

    // Setters
    public void setCoordinates() {

    }
}
