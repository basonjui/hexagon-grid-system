package com.geospatial.hexagongrid.geojson;

import java.util.List;

import com.geospatial.hexagongrid.hexagon.Coordinates;
import com.geospatial.hexagongrid.hexagon.Hexagon;
import com.geospatial.hexagongrid.neighbors.Neighbors;

import java.util.Arrays;
import java.util.ArrayList;

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
		final List<List<Double>> hexagonPositions = generatePositions(hexagon);
		/*
		 * Wrap hexagon's vertices coordinates inside another array - this is the
		 * GeoJSON coordinates structure for Polygon
		 * Why? because in GeoJSON, Polygons can have polygons (as holes) within them.
		 */
		final List<List<List<Double>>> polygonCoordinates = Arrays.asList(hexagonPositions);

		return polygonCoordinates;
	}

	// Internal methods: handle data processing in this class (private)
	private final static List<List<Double>> generatePositions(Hexagon hexagon) {
		final List<Coordinates> gisVertices = hexagon.getGisVertices();
		final List<List<Double>> positions = new ArrayList<List<Double>>();

		gisVertices.forEach((gisVertex) -> {
			// Convert vertex's Coordinates -> Position
			positions.add(gisVertex.toGeoJsonPosition());
		});

		return positions;
	}
}