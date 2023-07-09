package com.geospatial.hexagongrid.geojson;

import com.geospatial.hexagongrid.neighbors.Neighbors;

public class MultiPolygonGeometry extends Geometry {
	public MultiPolygonGeometry(Neighbors neighbors) {
		super("MultiPolygon");
		this.coordinates = GeoJsonCoordinates.generateMultiPolygonCoordinates(neighbors);
	}
}