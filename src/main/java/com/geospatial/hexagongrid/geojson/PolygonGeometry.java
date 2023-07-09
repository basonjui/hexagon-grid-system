package com.geospatial.hexagongrid.geojson;

import com.geospatial.hexagongrid.hexagon.Hexagon;

public class PolygonGeometry extends Geometry {
	// @Getter final protected transient Hexagon hexagon;

	public PolygonGeometry(Hexagon hexagon) {
		super("Polygon");
		// this.hexagon = hexagon;
		this.coordinates = GeoJsonCoordinates.generatePolygonCoordinates(hexagon);
	}
}