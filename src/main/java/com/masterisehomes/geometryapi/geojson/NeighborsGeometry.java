package com.masterisehomes.geometryapi.geojson;

import com.masterisehomes.geometryapi.neighbors.Neighbors;

public class NeighborsGeometry extends Geometry {
    public NeighborsGeometry(Neighbors neighbors) {
        super("MultiPolygon");
        this.coordinates = GeoJsonCoordinates.generateMultiPolygonCoordinates(neighbors);
    }
}
