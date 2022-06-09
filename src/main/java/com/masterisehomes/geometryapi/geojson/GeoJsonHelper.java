package com.masterisehomes.geometryapi.geojson;

import java.util.List;
import java.util.ArrayList;
import com.masterisehomes.geometryapi.hexagon.*;

public class GeoJsonHelper {
    public GeoJsonHelper() {}

    // Hexagon helpers
    public static List<List<Double>> getGeoJsonCoordinates(Hexagon hexagon) {
        List<Coordinates> vertices = hexagon.getGeoJsonVertices();
        
        List<List<Double>> geoJsonCoordinates = new ArrayList<List<Double>>();
        vertices.forEach((vertex) -> geoJsonCoordinates.add(vertex.toGeoJsonFormat()));
        return geoJsonCoordinates;
    } 
}
