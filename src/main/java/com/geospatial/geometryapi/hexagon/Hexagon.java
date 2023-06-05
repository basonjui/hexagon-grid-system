package com.geospatial.geometryapi.hexagon;

import java.util.List;

import com.geospatial.geometryapi.geodesy.SphericalMercatorProjection;
import com.geospatial.geometryapi.index.CubeCoordinatesIndex;
import com.geospatial.geometryapi.neighbors.NeighborPosition;

import java.util.ArrayList;
import java.io.Serializable;
import java.lang.Math;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Hexagon implements Serializable {
	private final Coordinates centroid;
	private final double circumradius;
	private final double inradius;

	// private List<Coordinates> vertices;
	private final List<Coordinates> gisVertices;

	private final NeighborPosition position;
	private final CubeCoordinatesIndex previousCCI;
	private final CubeCoordinatesIndex CCI;

	private final static double SQRT_3 = Math.sqrt(3);

	/* Constructors */
	public Hexagon(Coordinates centroid, double circumradius) {
		this.centroid = centroid;
		this.circumradius = circumradius;
		this.inradius = circumradius * SQRT_3 / 2;

		// this.vertices = generateVertices(centroid);
		this.gisVertices = generateGisVertices(centroid);

		this.position = NeighborPosition.ZERO;
		this.previousCCI = null;
		this.CCI = new CubeCoordinatesIndex(previousCCI, position);
	}

	// Construct a new Hexagon adjacent to previousHexagon in the respective NeighborPosition
	public Hexagon(Hexagon previousHexagon, Coordinates centroid, NeighborPosition position) {
		this.centroid = centroid;
		this.circumradius = previousHexagon.getCircumradius();
		this.inradius = circumradius * SQRT_3 / 2;
		// this.vertices = generateVertices(centroid);
		this.gisVertices = generateGisVertices(centroid);

		// Check position of hexagon, should never be null or ZERO
		if (position == null || position == NeighborPosition.ZERO) {
			String errMsg = """
					Hexagon position argument cannot be null or ZERO. Provided arguments:
					previousHexagon=%s, centroid=%s, position=%s
					""";
			throw new IllegalArgumentException(String.format(errMsg, previousHexagon, centroid, position));
		}
		
		this.position = position;
		this.previousCCI = previousHexagon.getCCI();
		// Calculate new Hexagon CCI
		this.CCI = new CubeCoordinatesIndex(previousCCI, position);
	}

	/* Methods */
	@Deprecated
	private List<Coordinates> generateVertices(Coordinates centroid) {
		final double centroidX = centroid.getX();
		final double centroidY = centroid.getY();

		/*
		 * Generate Hexagon vertices with Flat-top orientation in clock-wise rotation:
		 *   0   1
		 * 5   .   2
		 *   4   3
		 */
		List<Coordinates> vertices = new ArrayList<Coordinates>();
		vertices.add(new Coordinates(centroidX - circumradius / 2, centroidY - inradius));
		vertices.add(new Coordinates(centroidX + circumradius / 2, centroidY - inradius));
		vertices.add(new Coordinates(centroidX + circumradius, centroidY));
		vertices.add(new Coordinates(centroidX + circumradius / 2, centroidY + inradius));
		vertices.add(new Coordinates(centroidX - circumradius / 2, centroidY + inradius));
		vertices.add(new Coordinates(centroidX - circumradius, centroidY));

		return vertices;
	}

	private List<Coordinates> generateGisVertices(Coordinates centroid) {
		final double centroidLng = centroid.getLongitude();
		final double centroidLat = centroid.getLatitude();

		/*
		 * circumradius is either in meters or in pixels, but longitude and
		 * latitude are in degrees
		 * 
		 * so we need to convert the displacement into degrees of latitude & longitude
		 * (where meters/longitude is dependent on current latitude).
		 */
		final double circumradiusLng = SphericalMercatorProjection.xToLongitude(this.circumradius);
		final double inradiusLat = SphericalMercatorProjection.yToLatitude(this.inradius);

		/*
		 * GeoJSON specification:
		 * - The first and last positions are equivalent, and they MUST contain
		 * identical values; their representation SHOULD also be identical.
		 */
		final List<Coordinates> gisVertices = new ArrayList<Coordinates>();
		gisVertices.add(new Coordinates(centroidLng - circumradiusLng / 2, centroidLat - inradiusLat));
		gisVertices.add(new Coordinates(centroidLng + circumradiusLng / 2, centroidLat - inradiusLat));
		gisVertices.add(new Coordinates(centroidLng + circumradiusLng, centroidLat));
		gisVertices.add(new Coordinates(centroidLng + circumradiusLng / 2, centroidLat + inradiusLat));
		gisVertices.add(new Coordinates(centroidLng - circumradiusLng / 2, centroidLat + inradiusLat));
		gisVertices.add(new Coordinates(centroidLng - circumradiusLng, centroidLat));
		// Closing coordinate in GeoJSON, it is the same as first vertex, which is index 0
		gisVertices.add(gisVertices.get(0));

		return gisVertices;
	}
}