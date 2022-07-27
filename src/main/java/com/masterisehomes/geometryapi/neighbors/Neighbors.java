package com.masterisehomes.geometryapi.neighbors;

import java.security.InvalidParameterException;
import java.util.LinkedHashMap;
import java.util.Map;

import com.masterisehomes.geometryapi.hexagon.Hexagon;
import com.masterisehomes.geometryapi.index.HexagonalDirection;
import com.masterisehomes.geometryapi.hexagon.Coordinates;
import com.masterisehomes.geometryapi.geodesy.SphericalMercatorProjection;

import lombok.Getter;
import lombok.ToString;

@ToString
public class Neighbors {
	@Getter
	private Hexagon rootHexagon;
	@Getter
	private Map<Integer, Coordinates> centroids;
	@Getter
	private Map<Integer, Coordinates> gisCentroids;
	@Getter
	private Map<Integer, Hexagon> hexagons;
	@Getter
	private Map<Integer, Hexagon> gisHexagons;

	/* Constructor */
	public Neighbors(Hexagon rootHexagon) {
		this.rootHexagon = rootHexagon;

		this.centroids = generateCentroids(rootHexagon);
		this.hexagons = generateHexagons(this.centroids);

		this.gisCentroids = generateGisCentroids(rootHexagon);
		this.gisHexagons = generateGisHexagons(this.gisCentroids);
	}

	/* Public methods */
	public static Coordinates generateCentroid(Hexagon rootHexagon, int direction, int nthNeighbor) {
		/* Validate nthNeighbor */
		if (nthNeighbor <= 0) {
			throw new InvalidParameterException("Invalid nthNeighbor, must be <= 1, currently: " + nthNeighbor);
		}
		
		/* Constants */
		final double SQRT_3 = Math.sqrt(3);
		final double centroidX = rootHexagon.getCentroid().getX();
		final double centroidY = rootHexagon.getCentroid().getY();
		final double rootInradius = rootHexagon.getInradius();
		// Calculate nthInradius (inradius of the nthNeighbor)
		final double nthInradius = rootInradius * nthNeighbor;

		switch (direction) {
			case 1:
				return new Coordinates(
						centroidX,
						centroidY - 2 * nthInradius);
			case 2:
				return new Coordinates(
						centroidX + SQRT_3 * nthInradius,
						centroidY - nthInradius);
			case 3:
				return new Coordinates(
						centroidX + SQRT_3 * nthInradius,
						centroidY + nthInradius);
			case 4:
				return new Coordinates(
						centroidX,
						centroidY + 2 * nthInradius);
			case 5:
				return new Coordinates(
						centroidX - SQRT_3 * nthInradius,
						centroidY + nthInradius);
			case 6:
				return new Coordinates(
						centroidX - SQRT_3 * nthInradius,
						centroidY - nthInradius);
			default:
				throw new InvalidParameterException("Invalid Hexagonal direction: " + direction);
		}
	};

	public static Coordinates generateGisCentroid(Hexagon rootHexagon, int direction, int nthNeighbor) {
		/* Validate nthNeighbor */
		if (nthNeighbor <= 0) {
			throw new InvalidParameterException("Invalid nthNeighbor, must be <= 1, currently: " + nthNeighbor);
		}
		
		/* Constants */
		final double SQRT_3 = Math.sqrt(3);
		final double gisCentroidLng = rootHexagon.getCentroid().getLongitude();
		final double gisCentroidLat = rootHexagon.getCentroid().getLatitude();
		final double rootInradius = rootHexagon.getInradius();
		// Calculate nthInradius (inradius of the nthNeighbor)
		final double nthInradius = rootInradius * nthNeighbor;

		/* Convert neighborDistance (which is currently in Meter unit) to Degrees unit */
		final double nthInradiusLng = SphericalMercatorProjection.xToLongitude(nthInradius);
		final double nthInradiusLat = SphericalMercatorProjection.yToLatitude(nthInradius);

		switch (direction) {
			case 1:
				return new Coordinates(
						gisCentroidLng,
						gisCentroidLat - 2 * nthInradiusLat);
			case 2:
				return new Coordinates(
						gisCentroidLng + SQRT_3 * nthInradiusLng,
						gisCentroidLat - nthInradiusLat);
			case 3:
				return new Coordinates(
						gisCentroidLng + SQRT_3 * nthInradiusLng,
						gisCentroidLat + nthInradiusLat);
			case 4:
				return new Coordinates(
						gisCentroidLng,
						gisCentroidLat + 2 * nthInradiusLat);
			case 5:
				return new Coordinates(
						gisCentroidLng - SQRT_3 * nthInradiusLng,
						gisCentroidLat + nthInradiusLat);
			case 6:
				return new Coordinates(
						gisCentroidLng - SQRT_3 * nthInradiusLng,
						gisCentroidLat - nthInradiusLat);
			default:
				throw new InvalidParameterException("Invalid Hexagonal direction: " + direction);
		}
	};

	/* Internal methods */
	private Map<Integer, Coordinates> generateCentroids(Hexagon rootHexagon) {
		final double SQRT_3 = Math.sqrt(3);
		final double centroidX = rootHexagon.getCentroid().getX();
		final double centroidY = rootHexagon.getCentroid().getY();
		final double inradius = rootHexagon.getInradius();

		/*
		 * Neighbors are ordered in a clock-wise rotation, this aims to achieve some
		 * simple sense of direction for each root (center centroid) Hexagon to expand
		 * upon required.
		 * 
		 * Neighbor 1 starts at the Flat-top of the root Hexagon:
		 *    1
		 * 6/‾‾‾\2
		 * 5\___/3
		 *    4
		 * 
		 * *Update:
		 * - Neighbors will now include the centroids of rootHexagon
		 * which is at key 0 in centroids hashmap.
		 * 
		 * We calculate neighbor coordinates using their relationship to Hexagon
		 * centroid.
		 * There are 2 approaches: geometric vs trigonometric.
		 * 
		 * We mostly used Trigonometry.
		 */

		// Calculate neighbor centroids and put them into a Map
		Map<Integer, Coordinates> centroids = new LinkedHashMap<Integer, Coordinates>();

		centroids.put(0, rootHexagon.getCentroid());

		centroids.put(1, new Coordinates(
				centroidX,
				centroidY - 2 * inradius));

		centroids.put(2, new Coordinates(
				centroidX + SQRT_3 * inradius,
				centroidY - inradius));

		centroids.put(3, new Coordinates(
				centroidX + SQRT_3 * inradius,
				centroidY + inradius));

		centroids.put(4, new Coordinates(
				centroidX,
				centroidY + 2 * inradius));

		centroids.put(5, new Coordinates(
				centroidX - SQRT_3 * inradius,
				centroidY + inradius));

		centroids.put(6, new Coordinates(
				centroidX - SQRT_3 * inradius,
				centroidY - inradius));

		return centroids;
	}

	private Map<Integer, Coordinates> generateGisCentroids(Hexagon rootHexagon) {
		final double SQRT_3 = Math.sqrt(3);
		final double centroidLng = rootHexagon.getCentroid().getLongitude();
		final double centroidLat = rootHexagon.getCentroid().getLatitude();
		final double inradius = rootHexagon.getInradius();

		// Convert inradius (which is currently in Meter unit) to Degrees unit
		final double inradiusLng = SphericalMercatorProjection.xToLongitude(inradius); // x
		final double inradiusLat = SphericalMercatorProjection.yToLatitude(inradius); // y

		Map<Integer, Coordinates> gisCentroids = new LinkedHashMap<Integer, Coordinates>();

		gisCentroids.put(0, rootHexagon.getCentroid());

		gisCentroids.put(1, new Coordinates(
				centroidLng,
				centroidLat - 2 * inradiusLat));

		gisCentroids.put(2, new Coordinates(
				centroidLng + SQRT_3 * inradiusLng,
				centroidLat - inradiusLat));

		gisCentroids.put(3, new Coordinates(
				centroidLng + SQRT_3 * inradiusLng,
				centroidLat + inradiusLat));

		gisCentroids.put(4, new Coordinates(
				centroidLng,
				centroidLat + 2 * inradiusLat));

		gisCentroids.put(5, new Coordinates(
				centroidLng - SQRT_3 * inradiusLng,
				centroidLat + inradiusLat));

		gisCentroids.put(6, new Coordinates(
				centroidLng - SQRT_3 * inradiusLng,
				centroidLat - inradiusLat));

		return gisCentroids;
	}

	private Map<Integer, Hexagon> generateHexagons(Map<Integer, Coordinates> centroids) {
		Map<Integer, Hexagon> hexagons = new LinkedHashMap<Integer, Hexagon>();

		centroids.forEach((key, centroid) -> {
			/*
			 * We use switch - case statement on key of gisCentroids Map to determine
			 * HexagonDirection
			 */
			switch (key) {
				case 0:
					hexagons.put(key,
							new Hexagon(centroid, this.rootHexagon, HexagonalDirection.NONE));
					break;
				case 1:
					hexagons.put(key,
							new Hexagon(centroid, this.rootHexagon, HexagonalDirection.ONE));
					break;
				case 2:
					hexagons.put(key,
							new Hexagon(centroid, this.rootHexagon, HexagonalDirection.TWO));
					break;
				case 3:
					hexagons.put(key,
							new Hexagon(centroid, this.rootHexagon, HexagonalDirection.THREE));
					break;
				case 4:
					hexagons.put(key,
							new Hexagon(centroid, this.rootHexagon, HexagonalDirection.FOUR));
					break;
				case 5:
					hexagons.put(key,
							new Hexagon(centroid, this.rootHexagon, HexagonalDirection.FIVE));
					break;
				case 6:
					hexagons.put(key,
							new Hexagon(centroid, this.rootHexagon, HexagonalDirection.SIX));
					break;
			}
		});

		return hexagons;
	}

	private Map<Integer, Hexagon> generateGisHexagons(Map<Integer, Coordinates> gisCentroids) {
		Map<Integer, Hexagon> gisHexagons = new LinkedHashMap<Integer, Hexagon>();

		gisCentroids.forEach((key, gisCentroid) -> {
			// We use switch - case statement on key of gisCentroids Map to determine
			// HexagonDirection
			switch (key) {
				case 0:
					gisHexagons.put(key,
							new Hexagon(gisCentroid, this.rootHexagon, HexagonalDirection.NONE));
					break;
				case 1:
					gisHexagons.put(key,
							new Hexagon(gisCentroid, this.rootHexagon, HexagonalDirection.ONE));
					break;
				case 2:
					gisHexagons.put(key,
							new Hexagon(gisCentroid, this.rootHexagon, HexagonalDirection.TWO));
					break;
				case 3:
					gisHexagons.put(key,
							new Hexagon(gisCentroid, this.rootHexagon, HexagonalDirection.THREE));
					break;
				case 4:
					gisHexagons.put(key,
							new Hexagon(gisCentroid, this.rootHexagon, HexagonalDirection.FOUR));
					break;
				case 5:
					gisHexagons.put(key,
							new Hexagon(gisCentroid, this.rootHexagon, HexagonalDirection.FIVE));
					break;
				case 6:
					gisHexagons.put(key,
							new Hexagon(gisCentroid, this.rootHexagon, HexagonalDirection.SIX));
					break;
			}
		});

		return gisHexagons;
	}
}