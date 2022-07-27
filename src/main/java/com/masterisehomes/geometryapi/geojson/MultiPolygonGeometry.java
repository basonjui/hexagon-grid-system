package com.masterisehomes.geometryapi.geojson;

import com.masterisehomes.geometryapi.neighbors.Neighbors;

public class MultiPolygonGeometry extends Geometry {
	public MultiPolygonGeometry(Neighbors neighbors) {
		super("MultiPolygon");
		this.coordinates = GeoJsonCoordinates.generateMultiPolygonCoordinates(neighbors);
	}
}
