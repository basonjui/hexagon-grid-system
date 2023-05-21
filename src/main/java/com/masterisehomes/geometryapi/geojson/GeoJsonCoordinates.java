package com.masterisehomes.geometryapi.geojson;

import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;

import com.masterisehomes.geometryapi.hexagon.Hexagon;
import com.masterisehomes.geometryapi.neighbors.Neighbors;
import com.masterisehomes.geometryapi.hexagon.Coordinates;

class GeoJsonCoordinates {
	GeoJsonCoordinates() {
	}

	/* Neighbors */
	final static List<List<List<List<Double>>>> generateMultiPolygonCoordinates(Neighbors neighbors) {
		final List<Hexagon> hexagons = neighbors.getGisHexagons();
		final List<List<List<List<Double>>>> multiPolygonCoordinates = new ArrayList<>();

		hexagons.forEach((hexagon) -> {
			multiPolygonCoordinates.add(generatePolygonCoordinates(hexagon));
		});

		return multiPolygonCoordinates;
	}

	/* Hexagon */
	final static List<List<List<Double>>> generatePolygonCoordinates(Hexagon hexagon) {
		// Generate an Array of Array of Positions
		final List<List<Double>> hexagonArrayPositions = generateArrayPositions(hexagon);
		/*
		 * Wrap hexagon's vertices coordinates inside another array - this is the
		 * GeoJSON coordinates structure for Polygon
		 * Why? because in GeoJSON, Polygons can have polygons (as holes) within them.
		 */
		final List<List<List<Double>>> polygonCoordinates = Arrays.asList(hexagonArrayPositions);

		return polygonCoordinates;
	}

	// Internal methods: handle data processing in this class (private)
	private final static List<List<Double>> generateArrayPositions(Hexagon hexagon) {
		final List<Coordinates> gisVertices = hexagon.getGisVertices();
		final List<List<Double>> positions = new ArrayList<List<Double>>();

		gisVertices.forEach((gisVertex) -> {
			// Convert vertex's Coordinates -> Position
			positions.add(gisVertex.toGeoJSON());
		});

		return positions;
	}
}