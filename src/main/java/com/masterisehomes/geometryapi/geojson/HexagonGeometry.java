package com.masterisehomes.geometryapi.geojson;

import com.masterisehomes.geometryapi.hexagon.Hexagon;

public class HexagonGeometry extends Geometry {
    public HexagonGeometry(Hexagon hexagon) {
        super("Polygon");
        this.coordinates = GeoJsonCoordinates.generatePolygonCoordinates(hexagon);
    }
}
