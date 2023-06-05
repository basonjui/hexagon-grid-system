package com.geospatial.geometryapi.hexagon;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Coordinates implements Serializable {
	@ToString.Exclude private transient double x;
	@ToString.Exclude private transient double y;
	private final double longitude;
	private final double latitude;

	/* Constructors */
	public Coordinates(double longitude, double latitude) {
		/*
		 * There is no possible way to convert a longitude, latitude coordinates
		 * to Cartesian coordinates. Due to Cartesian coordinates can extend
		 * infinitely, but longitude and latitude are finite.
		 * 
		 * The Spherical Mercator can approximate distances (meters) into
		 * degrees using Trigonometry, but they cannot use to determine
		 * points.
		 * 
		 * --- Conclusion: 
		 * 1. We use the same centroid coordinates for both 
		 * Cartesian and Geographic coordinates. 
		 * 
		 * 2. The radiuses can be convert to longitude/latitude using
		 * SphericalMercatorProjection formulas.
		 */
		this.x = longitude;
		this.y = latitude;
		this.latitude = latitude;
		this.longitude = longitude;
	}

	// Comparison methods
	public boolean equals(Coordinates c) {
		final double longitude = c.getLongitude();
		final double latitude = c.getLatitude();

		final double x = c.getX();
		final double y = c.getY();

		if (equalsUnderThreshold(this.longitude, longitude)
				&& equalsUnderThreshold(this.latitude, latitude)
				&& equalsUnderThreshold(this.x, x)
				&& equalsUnderThreshold(this.y, y)) {
			return true;
		} else {
			return false;
		}
	}

	public final boolean isLarger(Coordinates c) {
		final double lat = c.getLatitude();
		final double lng = c.getLongitude();

		if (latitude > lat && longitude > lng) {
			return true;
		} else {
			return false;
		}
	}

	public final boolean isLargerOrEquals(Coordinates c) {
		if (this.isLarger(c) || this.equals(c)) {
			return true;
		} else {
			return false;
		}
	}

	public final boolean isSmaller(Coordinates c) {
		final double lat = c.getLatitude();
		final double lng = c.getLongitude();

		if (latitude < lat && longitude < lng) {
			return true;
		} else {
			return false;
		}
	}

	public final boolean isSmallerOrEquals(Coordinates c) {
		if (this.isSmaller(c) || this.equals(c)) {
			return true;
		} else {
			return false;
		}
	}

	/* Various representations */

	/*
	 * Position
	 * 
	 * A position is an array of coordinates in order:
	 * this is the smallest unit that we can really consider ‘a place’ since it can
	 * represent a point on earth.
	 * 
	 * GeoJSON describes an order for coordinates: they should go, in order:
	 * [longitude, latitude, elevation]
	 */
	public final List<Double> toGeoJsonPosition() {
		/*
		 * The order of elements must follow x, y, z order
		 * 
		 * - easting, northing, altitude for coordinates in a projected coordinate
		 * reference system,
		 * - or longitude, latitude, altitude for coordinates in a geographic coordinate
		 * reference system.
		 */
		final List<Double> positionCoordinates = Arrays.asList(longitude, latitude);
		return positionCoordinates;
	}

	public final List<Double> toPixel() {
		final List<Double> pixelCoordinates = Arrays.asList(x, y);
		return pixelCoordinates;
	}

	public final String toWKT() {
		final String wktCoordinates = String.format("%s %s", longitude, latitude);
		return wktCoordinates;
	}

	/* Internal methods */
	private final boolean equalsUnderThreshold(double numA, double numB) {
		final double THRESHOLD = 0.000001;

		final double difference = Math.abs(numA - numB);
		if (difference < THRESHOLD) {
			return true;
		} else {
			return false;
		}
	}
}	