package com.masterisehomes.geometryapi.hexagon;

import java.util.List;
import java.util.ArrayList;
import java.lang.Math;

import lombok.Getter;
import lombok.ToString;

import com.masterisehomes.geometryapi.geodesy.SphericalMercatorProjection;
import com.masterisehomes.geometryapi.index.CubeCoordinateIndex;
import com.masterisehomes.geometryapi.neighbors.NeighborPosition;

@ToString
@Getter
public class Hexagon {
	private final Coordinates centroid;
	private final double circumradius;
	private final double inradius;

	private List<Coordinates> vertices;
	private final List<Coordinates> gisVertices;

	// Cube Coordinates Indexing
	private final NeighborPosition position;
	private final CubeCoordinateIndex previousCCI;
	private final CubeCoordinateIndex CCI;

	/* Constructors */
	public Hexagon(Coordinates centroid, double circumradius) {
		this.centroid = centroid;
		this.circumradius = circumradius;
		this.inradius = circumradius * Math.sqrt(3) / 2;

		// this.vertices = generateVertices(centroid);
		this.gisVertices = generateGisVertices(centroid);

		this.position = NeighborPosition.ZERO;
		this.previousCCI = null;
		this.CCI = new CubeCoordinateIndex(this.previousCCI, this.position);
	}

	// Construct a new Hexagon from a rootHexagon
	public Hexagon(Coordinates centroid, Hexagon rootHexagon, NeighborPosition position) {
		this.centroid = centroid;
		this.circumradius = rootHexagon.getCircumradius();
		this.inradius = this.circumradius * Math.sqrt(3) / 2;

		// this.vertices = generateVertices(centroid);
		this.gisVertices = generateGisVertices(centroid);

		assert position != null : "Position cannot be null.";
		switch (position) {
			case ZERO:
				this.position = position;
				this.previousCCI = null;
				break;
			default:
				this.position = position;
				this.previousCCI = rootHexagon.getCCI();
				break;
		}

		this.CCI = new CubeCoordinateIndex(this.previousCCI, position);
	}

	/* Methods */
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
		List<Coordinates> gisCoordinates = new ArrayList<Coordinates>();
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

	// Getters
	public String getIndex() {
		// We use the Hexagon's position as its name when print out
		String name = this.position.toString();

		return String.format("Hexagon%s=(position=%s, previousCCI=%s, CCI=%s)",
				name, this.position, this.previousCCI, this.CCI);
	}
}