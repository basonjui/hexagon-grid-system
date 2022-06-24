package com.masterisehomes.geometryapi.geojson;

import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.HashMap;
import com.masterisehomes.geometryapi.hexagon.Hexagon;
import com.masterisehomes.geometryapi.neighbors.Neighbors;
import com.masterisehomes.geometryapi.hexagon.Coordinates;

class GeoJsonCoordinates {
    GeoJsonCoordinates() {
    }

    static List<List<List<List<Double>>>> generateMultiPolygonCoordinates(Neighbors neighbors) {
        // TODO: For each hexagon in hexagons (neighbor) -> generatePolygonCoordinates
        HashMap<Integer, Hexagon> hexagons = neighbors.getHexagons();
        // List<List<List<Double>>> hexagonCoordinates = generatePolygonCoordinates(neighbors.getRootHexagon());

        // TODO: Then, Add PolygonCoordinates to multiPolygonCoordinates 
        List<List<List<List<Double>>>> multiPolygonCoordinates = new ArrayList<>();

        return multiPolygonCoordinates;
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

    // Internal methods: handle data processing in this class (private)
    private static List<List<Double>> _generateArrayPositions(Hexagon hexagon) {
        List<Coordinates> vertices = hexagon.getGisVertices();
        List<List<Double>> positions = new ArrayList<List<Double>>();

        vertices.forEach((vertex) -> {
            positions.add(
                vertex.toGeoJsonPosition() // Convert vertex's Coordinates -> Position
            );
        });

        return positions;
    }
}