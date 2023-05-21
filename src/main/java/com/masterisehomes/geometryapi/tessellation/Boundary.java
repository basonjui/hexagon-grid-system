package com.masterisehomes.geometryapi.tessellation;

import com.masterisehomes.geometryapi.geodesy.Harversine;
import com.masterisehomes.geometryapi.hexagon.Coordinates;

import lombok.Getter;
import lombok.ToString;

/* 
 * Boundary
 * 
 * This class borrows the concept of Bounding Diagonal in PostGIS:
 * 	- "The diagonal is a 2-point LineString with the minimum values of 
 * each dimension in its start point and the maximum values in its end point".
 * 
 * Basically, Boundary contains 2 Coordinates: 
 * 	1. minCoordinates: the smallest values of longitude, latitude that the
 * Boundary is supposed to cover.
 * 	2. maxCoordinates: the largest values of longitude latitude...
 */

@ToString
public class Boundary {
	@Getter
	@ToString.Exclude
	private final Coordinates minCoordinates, maxCoordinates;
	@Getter
	private final double minLongitude, minLatitude;
	@Getter
	private final double maxLongitude, maxLatitude;

	public Boundary(Coordinates minCoordinates, Coordinates maxCoordinates) {
		this.minCoordinates = minCoordinates;
		this.maxCoordinates = maxCoordinates;

		this.minLatitude = minCoordinates.getLatitude();
		this.minLongitude = minCoordinates.getLongitude();
		this.maxLatitude = maxCoordinates.getLatitude();
		this.maxLongitude = maxCoordinates.getLongitude();
	}

	/* Calculate Great-circle distance */
	public double greatCircleDistance() {
		double greatCircleDistance = Harversine.distance(
			minLatitude, minLongitude,
			maxLatitude, maxLongitude);

		return greatCircleDistance;
	}

	/* Comparison methods */
	public boolean contains(Coordinates c) {
		/* 
		 * To check if Boundary contains a pair of Coordinates c,
		 * - we check if: `minCoordinates <= c <= maxCoordinates`
		 * 
		 * To do this, we use isSmallerOrEquals() and isLargerOrEquals methods from
		 * Coordinates class. 
		 * 
		 * These methods simplify the comparison by handling the
		 * processes of comparing longitude, latitude for each coordinate; as well as
		 * handling the complexity of comparing "equality" of `double` data type in Java,
		 * due to limitations in double precisions.
		 */
		if (minCoordinates.isSmallerOrEquals(c) && maxCoordinates.isLargerOrEquals(c)) {
			return true;
		} else {
			return false;
		}
	}
}