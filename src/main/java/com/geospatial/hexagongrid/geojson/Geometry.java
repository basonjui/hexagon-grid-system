package com.geospatial.hexagongrid.geojson;

import java.util.List;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
abstract class Geometry {
	final protected String type;
	protected List<?> coordinates;

	Geometry(String type) {
		if (type.equals("Polygon")) {
			this.type = "Polygon";
		} else if (type.equals("MultiPolygon")) {
			this.type = "MultiPolygon";
		} else {
			throw new IllegalArgumentException("Unsupported Geometry type.");
		}
	}
}