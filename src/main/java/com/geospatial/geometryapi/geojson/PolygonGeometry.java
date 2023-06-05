package com.geospatial.geometryapi.geojson;

import com.geospatial.geometryapi.hexagon.Hexagon;

public class PolygonGeometry extends Geometry {
	// @Getter final protected transient Hexagon hexagon;

	public PolygonGeometry(Hexagon hexagon) {
		super("Polygon");
		// this.hexagon = hexagon;
		this.coordinates = GeoJsonCoordinates.generatePolygonCoordinates(hexagon);
	}
}