package com.geospatial.geometryapi.geodesy;

import java.lang.Math;

/* An algorithm to convert meters to degrees of Latitude & Longitude
 * 
 * However, this algorithm is not as good as SphericalMercatorProjection
 * provided by OpenStreetMap wiki
 * - https://wiki.openstreetmap.org/wiki/Mercator
 */
public class SphericalMetricConversion {
	private static final int metersPerLatitude = 111111;

	public static double meterToLatitude(double meter) {
		double latitudeDegree = meter / metersPerLatitude;
		return latitudeDegree;
	}

	public static double meterToLongitude(double meter, double latitude) {
		double longitudeDegree = meter / metersPerLatitude * Math.cos(Math.toRadians(latitude));
		return longitudeDegree;
	}
}