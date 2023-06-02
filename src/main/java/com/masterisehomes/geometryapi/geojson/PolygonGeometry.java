package com.masterisehomes.geometryapi.geojson;

import com.masterisehomes.geometryapi.hexagon.Hexagon;
// import lombok.Getter;

public class PolygonGeometry extends Geometry {
	// @Getter final protected transient Hexagon hexagon;

	public PolygonGeometry(Hexagon hexagon) {
		super("Polygon");
		// this.hexagon = hexagon;
		this.coordinates = GeoJsonCoordinates.generatePolygonCoordinates(hexagon);
	}
}