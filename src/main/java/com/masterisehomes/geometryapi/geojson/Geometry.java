package com.masterisehomes.geometryapi.geojson;

import com.masterisehomes.geometryapi.hexagon.*;
import java.util.ArrayList;
import java.util.List;
import lombok.ToString;
import static com.masterisehomes.geometryapi.geojson.GeoJsonHelper.getGeoJsonCoordinates;

@ToString
public class Geometry {
    private final String type;
    private List<?> coordinates;

    public Geometry(Hexagon hexagon) {
        this.type = "Polygon";
        this.coordinates = new ArrayList<>();

        List<List<Double>> hexagonGeoJsonCoordinates = getGeoJsonCoordinates(hexagon);

        List<List<List<Double>>> tempCoordinates = new ArrayList<List<List<Double>>>();
        tempCoordinates.add(hexagonGeoJsonCoordinates);
        this.coordinates = tempCoordinates;
    }
}
