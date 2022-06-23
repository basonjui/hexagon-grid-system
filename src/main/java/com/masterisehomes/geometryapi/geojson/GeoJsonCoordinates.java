package com.masterisehomes.geometryapi.geojson;

import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;
import com.masterisehomes.geometryapi.hexagon.Hexagon;
import com.masterisehomes.geometryapi.hexagon.Coordinates;

class GeoJsonCoordinates {
    List<List<?>> coordinates = new ArrayList<>();

    GeoJsonCoordinates() {
    }

    static List<List<List<Double>>> generatePolygonCoordinates(Hexagon hexagon) {
        // Generate an Array of Array of Positions
        List<List<Double>> hexagonArrayPositions = _generateArrayPositions(hexagon);
        /*
         * Wrap hexagon's vertices coordinates inside another array - this is the
         * GeoJSON coordinates structure for Polygon
         * Why? because in GeoJSON, Polygons can have polygons (as holes) within them.
         */
        return Arrays.asList(hexagonArrayPositions);
    }

    /*
     * Internal methods: to generate an Array of Positions for a Geometry
     * to be overloaded for different geometry type
     */
    private static List<List<Double>> _generateArrayPositions(Hexagon hexagon) {
        List<Coordinates> verticesCoordinates = hexagon.getGeographicVertices();

        List<List<Double>> coordinatesArray = new ArrayList<List<Double>>();
        verticesCoordinates.forEach(
                (vertexCoordinates) -> {
                    coordinatesArray.add(vertexCoordinates.toGeoJsonPosition());
                });

        return coordinatesArray;
    }
}
