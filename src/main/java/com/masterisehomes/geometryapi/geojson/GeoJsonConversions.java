package com.masterisehomes.geometryapi.geojson;

import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;
import com.masterisehomes.geometryapi.hexagon.*;

public class GeoJsonConversions {
    public GeoJsonConversions() {
    }

    // Geometry - Positions Arrays conversion
    public static List<List<List<Double>>> _getGeoJsonArrayArrayPositions(Hexagon hexagon) {
        List<List<Double>> hexagonCoordinatesArray = _getGeoJsonArrayPositions(hexagon);
        // Wrap hexagon's vertices coordinates inside another array - this is the
        // GeoJSON coordinates structure for Polygon
        // Why? because in GeoJSON, Polygons can have polygons (as holes) within them.
        return Arrays.asList(hexagonCoordinatesArray);
    }

    // Internal methods
    private static List<List<Double>> _getGeoJsonArrayPositions(Hexagon hexagon) {
        List<Coordinates> verticesCoordinates = hexagon.getGeoJsonPositions();

        List<List<Double>> coordinatesArray = new ArrayList<List<Double>>();
        verticesCoordinates.forEach((vertexCoordinates) -> coordinatesArray.add(vertexCoordinates.toGeoJsonPosition()));
        return coordinatesArray;
    }

    // Geometry - Pixel to Longitude, Latitude conversions

}
