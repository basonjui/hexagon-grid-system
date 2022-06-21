package com.masterisehomes.geometryapi.geojson;

import com.masterisehomes.geometryapi.hexagon.*;
import java.util.ArrayList;
import java.util.List;
import lombok.ToString;
import static com.masterisehomes.geometryapi.geojson.CoordinatesConversion.getGeoJsonArrayArrayPositions;

@ToString
public class Geometry {
    private final String type;
    private List<?> coordinates;

    public Geometry(Hexagon hexagon) {
        this.type = "Polygon";
        this.coordinates = new ArrayList<>();

        List<List<List<Double>>> hexagonGeoJsonCoordinates = getGeoJsonArrayArrayPositions(hexagon);
        this.coordinates = hexagonGeoJsonCoordinates;
    }
}
