package com.masterisehomes.geometryapi.geojson;

import com.masterisehomes.geometryapi.hexagon.Hexagon;

public class PolygonGeometry extends Geometry {
	public PolygonGeometry(Hexagon hexagon) {
		super("Polygon");
		this.coordinates = GeoJsonCoordinates.generatePolygonCoordinates(hexagon);
	}
}
