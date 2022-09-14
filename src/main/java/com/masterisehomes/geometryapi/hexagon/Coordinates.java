package com.masterisehomes.geometryapi.hexagon;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/* So why the Vertex class instead of using the predefined Point class?
 *
 * Point is a really old class in Java, it can only work with Integer and Double.
 *
 * This is a problem because the vertex() function in processing can
 * only work with Float data type.
 *
 * Do we want to cast Double to Float for every point to draw on Processing?
 * So here we are. 
 */

@ToString
@Getter
@Setter
public class Coordinates implements Serializable {
	private double x;
	private double y;
	private double longitude;
	private double latitude;

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
		 * Conclusion: we use the same centroid coordinates for both 
		 * Cartesian and Geographic coordinates. 
		 * 
		 * The radiuses can be convert to longitude/latitude using
		 * SphericalMercatorProjection formulas.
		 */
		this.x = longitude;
		this.y = latitude;
		this.latitude = latitude;
		this.longitude = longitude;
	}

	// Comparison methods
	public boolean equals(Coordinates coordinates) {
		final double longitude = coordinates.getLongitude();
		final double latitude = coordinates.getLatitude();

		final double x = coordinates.getX();
		final double y = coordinates.getY();

		if (equals_under_threshold(this.longitude, longitude)
				&& equals_under_threshold(this.latitude, latitude)
				&& equals_under_threshold(this.x, x)
				&& equals_under_threshold(this.y, y)) {
			return true;
		} else {
			return false;
		}
	}

	public final boolean isLarger(Coordinates gisCoordinates) {
		final double latitude = gisCoordinates.getLatitude();
		final double longitude = gisCoordinates.getLongitude();

		if (this.latitude > latitude && this.longitude > longitude) {
			return true;
		} else {
			return false;
		}
	}

	public final boolean isSmaller(Coordinates gisCoordinates) {
		final double latitude = gisCoordinates.getLatitude();
		final double longitude = gisCoordinates.getLongitude();

		if (this.latitude < latitude && this.longitude < longitude) {
			return true;
		} else {
			return false;
		}
	}

	/* GeoJSON stuff */

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
		 * (easting, northing, altitude for coordinates in a projected coordinate
		 * reference system,
		 * or longitude, latitude, altitude for coordinates in a geographic coordinate
		 * reference system).
		 */
		final List<Double> gisCoordinates = Arrays.asList(this.longitude, this.latitude);
		return gisCoordinates;
	}

	public final List<Double> toPixel() {
		final List<Double> pixelCoordinates = Arrays.asList(this.x, this.y);
		return pixelCoordinates;
	}

	/* Internal methods */
	private final boolean equals_under_threshold(double numA, double numB) {
		final double THRESHOLD = 0.000001;

		final double difference = Math.abs(numA - numB);
		if (difference < THRESHOLD) {
			return true;
		} else {
			return false;
		}
	}
}	