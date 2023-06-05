package com.geospatial.geometryapi.geodesy;

import java.lang.Math;

import com.geospatial.geometryapi.hexagon.Coordinates;

public class Harversine {
	/*
	 * Earth's radius at the equator (WGS-84)
	 * https://en.wikipedia.org/wiki/World_Geodetic_System#Definition
	 */
	public static final double R = 6378137.0; // meters

	/*
	 * Calculate the Great-Circle Distance between two coordinates - which is the
	 * shortest distance over Earth's surface (fly distance)
	 */
	public static double distance(double lat1, double lng1, double lat2, double lng2) {
		// Convert latitudes to Radians
		final double phi_1 = Math.toRadians(lat1);
		final double phi_2 = Math.toRadians(lat2);

		// Distance between latitudes and longitudes
		final double delta_phi = Math.toRadians(lat2 - lat1);
		final double delta_lambda = Math.toRadians(lng2 - lng1);

		// Apply Harversine formula
		final double a = Math.pow(Math.sin(delta_phi / 2), 2)
				+ Math.pow(Math.sin(delta_lambda / 2), 2) * Math.cos(phi_1) * Math.cos(phi_2);
		final double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
		final double distance = R * c; // meters

		return distance;
	}

	public static double distance(Coordinates coordinates1, Coordinates coordinates2) {
		final double lat1 = coordinates1.getLatitude();
		final double lng1 = coordinates1.getLongitude();
		final double lat2 = coordinates2.getLatitude();
		final double lng2 = coordinates2.getLongitude();

		// Convert latitudes to Radians
		final double phi_1 = Math.toRadians(lat1);
		final double phi_2 = Math.toRadians(lat2);

		// Distance between latitudes and longitudes
		final double delta_phi = Math.toRadians(lat2 - lat1);
		final double delta_lambda = Math.toRadians(lng2 - lng1);

		// Apply Harversine formula
		final double a = Math.pow(Math.sin(delta_phi / 2), 2)
				+ Math.pow(Math.sin(delta_lambda / 2), 2) * Math.cos(phi_1) * Math.cos(phi_2);
		final double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
		final double distance = R * c; // meters

		return distance;
	}
}
