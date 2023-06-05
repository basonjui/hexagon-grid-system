package com.geospatial.geometryapi.geojson;

import com.geospatial.geometryapi.neighbors.Neighbors;

public class MultiPolygonGeometry extends Geometry {
	public MultiPolygonGeometry(Neighbors neighbors) {
		super("MultiPolygon");
		this.coordinates = GeoJsonCoordinates.generateMultiPolygonCoordinates(neighbors);
	}
}