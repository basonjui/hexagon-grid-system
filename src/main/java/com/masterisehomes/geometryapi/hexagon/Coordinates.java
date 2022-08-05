package com.masterisehomes.geometryapi.hexagon;

import java.util.Arrays;
import java.util.List;

import lombok.Getter;
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

@Getter
@ToString
public class Coordinates {
	private final double x;
	private final double y;
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
		double latitude = coordinates.getLatitude();
		double longitude = coordinates.getLongitude();
		double x = coordinates.getX();
		double y = coordinates.getY();

		if (this.latitude == latitude && this.longitude == longitude
				&& this.x == x && this.y == y) {
			return true;
		} else {
			return false;
		}
	}

	public boolean isLarger(Coordinates gisCoordinates) {
		double latitude = gisCoordinates.getLatitude();
		double longitude = gisCoordinates.getLongitude();

		if (this.latitude > latitude && this.longitude > longitude) {
			return true;
		} else {
			return false;
		}
	}

	public boolean isSmaller(Coordinates gisCoordinates) {
		double latitude = gisCoordinates.getLatitude();
		double longitude = gisCoordinates.getLongitude();

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
	public List<Double> toGeoJsonPosition() {
		/*
		 * The order of elements must follow x, y, z order
		 * 
		 * (easting, northing, altitude for coordinates in a projected coordinate
		 * reference system,
		 * or longitude, latitude, altitude for coordinates in a geographic coordinate
		 * reference system).
		 */
		List<Double> gisCoordinates = Arrays.asList(this.longitude, this.latitude);
		return gisCoordinates;
	}

	public List<Double> toPixel() {
		List<Double> pixelCoordinates = Arrays.asList(this.x, this.y);
		return pixelCoordinates;
	}
}