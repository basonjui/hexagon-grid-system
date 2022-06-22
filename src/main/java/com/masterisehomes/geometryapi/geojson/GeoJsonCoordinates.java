package com.masterisehomes.geometryapi.geojson;

import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;
import com.masterisehomes.geometryapi.hexagon.*;

class GeoJsonCoordinates {
    List<List<?>> coordinates = new ArrayList<>();

    GeoJsonCoordinates() {
    }

    static List<List<List<Double>>> generatePolygonCoordinates(Hexagon hexagon) {
        // Generate an Array of Array of Positions
        List<List<Double>> hexagonCoordinatesArray = _generateArrayPositions(hexagon);
        /*
         * Wrap hexagon's vertices coordinates inside another array - this is the
         * GeoJSON coordinates structure for Polygon
         * Why? because in GeoJSON, Polygons can have polygons (as holes) within them.
         */
        return Arrays.asList(hexagonCoordinatesArray);
    }

    private static List<List<Double>> _generateArrayPositions(Hexagon hexagon) {
        List<Coordinates> verticesCoordinates = hexagon.getGeoJsonPositions();

        List<List<Double>> coordinatesArray = new ArrayList<List<Double>>();
        verticesCoordinates.forEach(
            (vertexCoordinates) -> {
                coordinatesArray.add(vertexCoordinates.toGeoJsonPosition());
            }
        );

        return coordinatesArray;
    }
}
