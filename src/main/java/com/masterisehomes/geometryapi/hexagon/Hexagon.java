package com.masterisehomes.geometryapi.hexagon;

import java.util.List;
import java.util.ArrayList;
import java.io.Serializable;
import java.lang.Math;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import com.masterisehomes.geometryapi.geodesy.SphericalMercatorProjection;
import com.masterisehomes.geometryapi.index.CubeCoordinatesIndex;
import com.masterisehomes.geometryapi.neighbors.NeighborPosition;

@Getter
@Setter
@ToString
public class Hexagon implements Serializable {
	private Coordinates centroid;
	private double circumradius;
	private double inradius;

	private List<Coordinates> vertices;
	private List<Coordinates> gisVertices;

	private NeighborPosition position;
	private CubeCoordinatesIndex previousCCI;
	private CubeCoordinatesIndex CCI;

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

	// Construct a new Hexagon adjacent to parentHexagon in the respective NeighborPosition
	public Hexagon(Coordinates centroid, Hexagon parentHexagon, NeighborPosition position) {
		this.centroid = centroid;
		this.circumradius = parentHexagon.getCircumradius();
		this.inradius = this.circumradius * SQRT_3 / 2;

		// this.vertices = generateVertices(centroid);
		this.gisVertices = generateGisVertices(centroid);

		// Check position of hexagon, should never be null
		if (position == null) {
			throw new IllegalArgumentException(
				String.format("""
					Position cannot be null, hexagon with %s has position=%s
				""", CCI, position));
		}

		switch (position) {
			case ZERO:
				this.position = position;
				this.previousCCI = null;
				break;

			default:
				this.position = position;
				this.previousCCI = parentHexagon.getCCI();
				break;
		}

		this.CCI = new CubeCoordinatesIndex(this.previousCCI, position);
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
		List<Coordinates> coordinates = new ArrayList<Coordinates>();

		coordinates.add(new Coordinates(centroidX - circumradius / 2, centroidY - inradius));
		coordinates.add(new Coordinates(centroidX + circumradius / 2, centroidY - inradius));
		coordinates.add(new Coordinates(centroidX + circumradius, centroidY));
		coordinates.add(new Coordinates(centroidX + circumradius / 2, centroidY + inradius));
		coordinates.add(new Coordinates(centroidX - circumradius / 2, centroidY + inradius));
		coordinates.add(new Coordinates(centroidX - circumradius, centroidY));

		return coordinates;
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
		final List<Coordinates> gisCoordinates = new ArrayList<Coordinates>();
		gisCoordinates.add(new Coordinates(centroidLng - circumradiusLng / 2, centroidLat - inradiusLat));
		gisCoordinates.add(new Coordinates(centroidLng + circumradiusLng / 2, centroidLat - inradiusLat));
		gisCoordinates.add(new Coordinates(centroidLng + circumradiusLng, centroidLat));
		gisCoordinates.add(new Coordinates(centroidLng + circumradiusLng / 2, centroidLat + inradiusLat));
		gisCoordinates.add(new Coordinates(centroidLng - circumradiusLng / 2, centroidLat + inradiusLat));
		gisCoordinates.add(new Coordinates(centroidLng - circumradiusLng, centroidLat));
		// Closing coordinate in GeoJSON, it is the same as first vertex, which is indexed 0
		gisCoordinates.add(gisCoordinates.get(0));

		return gisCoordinates;
	}
}