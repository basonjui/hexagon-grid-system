package com.masterisehomes.geometryapi.tessellation;

import lombok.Getter;
import lombok.ToString;

import org.apache.commons.lang3.builder.ToStringExclude;

import com.masterisehomes.geometryapi.geodesy.Harversine;
import com.masterisehomes.geometryapi.hexagon.Coordinates;

/* Similar to setup() in Processing
 * However, due to abstraction, the setup data is hard-coded and not stored, so we cannot
 * retrieve those data to use as a Coordinate system to setup our Hexagon Grid Map.
 * 
 * This class aims to serve as a formal management system for the boundary aspect:
 * - boundaries of canvas 
 * - boundaries of Processing shapes
 */

@ToString
public class Boundary {
	@Getter
	@ToStringExclude
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
	public boolean containsCentroid(Coordinates centroid) {
		double centroidLat = centroid.getLatitude();
		double centroidLng = centroid.getLongitude();

		if (this.containsLat(centroidLat) && this.containsLng(centroidLng)) {
			return true;
		} else {
			return false;
		}
	}

	// Internal methods
	private boolean containsLat(double lat) {
		if (lat >= this.minLatitude && lat <= this.maxLatitude) {
			return true;
		} else {
			return false;
		}
	}

	private boolean containsLng(double lng) {
		if (lng >= this.minLongitude && lng <= this.maxLongitude) {
			return true;
		} else {
			return false;
		}
	}
}