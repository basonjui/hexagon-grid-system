package com.masterisehomes.geometryapi.hexagon;

import java.util.List;
import java.util.ArrayList;
import java.lang.Math;

import lombok.Getter;
import lombok.ToString;

import com.masterisehomes.geometryapi.geodesy.SphericalMercatorProjection;
import com.masterisehomes.geometryapi.index.CubeCoordinateIndex;
import com.masterisehomes.geometryapi.neighbors.NeighborDirection;

@ToString
@Getter
public class Hexagon {
	private Coordinates centroid;
	private double circumradius;
	private double inradius;

	private List<Coordinates> vertices;
	private List<Coordinates> gisVertices;

	// Cube Coordinates Indexing
	private final NeighborDirection direction;
	private final CubeCoordinateIndex previousCCI;
	private final CubeCoordinateIndex CCI;

	public Hexagon(Coordinates centroid, double circumradius) {
		this.centroid = centroid;
		this.circumradius = circumradius;
		this.inradius = circumradius * Math.sqrt(3) / 2;

		this.vertices = generateVertices(centroid);
		this.gisVertices = generateGisVertices(centroid);

		this.direction = null;
		this.previousCCI = null;
		this.CCI = new CubeCoordinateIndex();
	}

	// Construct a new Hexagon from a rootHexagon
	public Hexagon(Coordinates centroid, Hexagon rootHexagon, NeighborDirection direction) {
		this.centroid = centroid;
		this.circumradius = rootHexagon.getCircumradius();
		this.inradius = this.circumradius * Math.sqrt(3) / 2;

		this.vertices = generateVertices(centroid);
		this.gisVertices = generateGisVertices(centroid);

		assert direction != null : "Direction cannot be null.";
		this.direction = direction;
		this.previousCCI = rootHexagon.getCCI();

		this.CCI = new CubeCoordinateIndex(this.previousCCI, direction);
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
		 * Use SphericalMetricConversion algorithm
		 * 	double circumradiusInLongitude = SphericalMetricConversion.meterToLongitude(this.circumradius, latitude);
		 * 	double inradiusInLatitude = SphericalMetricConversion.meterToLatitude(this.inradius);
		 */

		List<Coordinates> gisCoordinates = new ArrayList<Coordinates>();
		
		/*
		 * GeoJSON specification:
		 * - The first and last positions are equivalent, and they MUST contain
		 * identical values; their representation SHOULD also be identical.
		 */
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
		if (this.direction == null) {
			return String.format("HexagonROOT=(direction=%s, previousCCI=%s, CCI=%s)",
					this.direction, this.previousCCI, this.CCI);
		} else {
			String name = this.direction.toString();
			return String.format("Hexagon%s=(direction=%s, previousCCI=%s, CCI=%s)",
					name, this.direction, this.previousCCI, this.CCI);
		}
	}
}