package com.masterisehomes.geometryapi.neighbors;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

import com.masterisehomes.geometryapi.hexagon.Hexagon;
import com.masterisehomes.geometryapi.hexagon.Coordinates;
import com.masterisehomes.geometryapi.geodesy.SphericalMercatorProjection;

import lombok.Getter;
import lombok.ToString;

@ToString
public class Neighbors {
	/* Fields */
	@Getter
	private Hexagon rootHexagon;
	@Getter
	private List<Coordinates> centroids;
	@Getter
	private List<Coordinates> gisCentroids;
	@Getter
	private List<Hexagon> hexagons;
	@Getter
	private List<Hexagon> gisHexagons;

	/* Constants */
	private static final double COS_30_DEG = Math.cos(Math.toRadians(30));
	private static final double SQRT_3 = Math.sqrt(3); // cos(30 deg) = SQRT_3 / 2

	/* Constructor */
	public Neighbors(Hexagon rootHexagon) {
		this.rootHexagon = rootHexagon;

		this.centroids = generateCentroids(rootHexagon);
		this.hexagons = generateHexagons(this.centroids);

		this.gisCentroids = generateGisCentroids(rootHexagon);
		this.gisHexagons = generateGisHexagons(this.gisCentroids);
	}

	/* Public methods */
	public static Coordinates generateNthCentroid(Hexagon rootHexagon, NeighborPosition position, int nthRing) {
		/* Validate nthRing */
		if (nthRing <= 0) {
			throw new InvalidParameterException("Invalid nthRing, must be >= 1, currently: " + nthRing);
		}
		
		/* Constants */
		final double centroidX = rootHexagon.getCentroid().getX();
		final double centroidY = rootHexagon.getCentroid().getY();
		final double rootInradius = rootHexagon.getInradius();
		// Calculate nthInradius (inradius of the nthRing)
		final double nthInradius = rootInradius * nthRing;

		/* Switch - case on NeighborPosition to generate a neighbor centroid */
		switch (position) {
			case ONE:
				return new Coordinates(
						centroidX,
						centroidY - 2 * nthInradius);
			case TWO:
				return new Coordinates(
						centroidX + SQRT_3 * nthInradius,
						centroidY - nthInradius);
			case THREE:
				return new Coordinates(
						centroidX + SQRT_3 * nthInradius,
						centroidY + nthInradius);
			case FOUR:
				return new Coordinates(
						centroidX,
						centroidY + 2 * nthInradius);
			case FIVE:
				return new Coordinates(
						centroidX - SQRT_3 * nthInradius,
						centroidY + nthInradius);
			case SIX:
				return new Coordinates(
						centroidX - SQRT_3 * nthInradius,
						centroidY - nthInradius);
			default:
				throw new InvalidParameterException("Only accept position 1 - 6, currently: " + position);
		}
	};

	public static Coordinates generateNthGisCentroid(Hexagon rootHexagon, NeighborPosition position, int nthRing) {
		/* Validate nthRing */
		if (nthRing <= 0) {
			throw new InvalidParameterException("Invalid nthRing, must be <= 1, currently: " + nthRing);
		}
		
		/* Constants */
		final double gisCentroidLng = rootHexagon.getCentroid().getLongitude();
		final double gisCentroidLat = rootHexagon.getCentroid().getLatitude();

		final double rootInradius = rootHexagon.getInradius();
		// Calculate nthInradius (inradius of the nthRing)
		final double nthInradius = rootInradius * nthRing;

		// Convert neighborDistance (which is currently in Meter unit) to Degrees unit
		final double nthInradiusLng = SphericalMercatorProjection.xToLongitude(nthInradius);
		final double nthInradiusLat = SphericalMercatorProjection.yToLatitude(nthInradius);

		/* Switch - case on NeighborPosition to generate a neighbor centroid */
		switch (position) {
			case ONE:
				return new Coordinates(
						gisCentroidLng,
						gisCentroidLat - 2 * nthInradiusLat);
			case TWO:
				return new Coordinates(
						gisCentroidLng + SQRT_3 * nthInradiusLng,
						gisCentroidLat - nthInradiusLat);
			case THREE:
				return new Coordinates(
						gisCentroidLng + SQRT_3 * nthInradiusLng,
						gisCentroidLat + nthInradiusLat);
			case FOUR:
				return new Coordinates(
						gisCentroidLng,
						gisCentroidLat + 2 * nthInradiusLat);
			case FIVE:
				return new Coordinates(
						gisCentroidLng - SQRT_3 * nthInradiusLng,
						gisCentroidLat + nthInradiusLat);
			case SIX:
				return new Coordinates(
						gisCentroidLng - SQRT_3 * nthInradiusLng,
						gisCentroidLat - nthInradiusLat);
			default:
				throw new InvalidParameterException("Only accept position 1 - 6, currently: " + position);
		}
	};

	public static final Hexagon generateNextHexagon(Hexagon rootHexagon, NeighborPosition position) {
		// Get rootHexagon's centroid & inradius
		final Coordinates rootCentroid = rootHexagon.getCentroid();
		final double rootInradius = rootHexagon.getInradius();
		
		final Coordinates neighborCentroid;
		final Hexagon neighborHexagon;

		switch (position) {
			case ONE:
				neighborCentroid = generateP1Centroid(rootCentroid, rootInradius);
				neighborHexagon = new Hexagon(neighborCentroid, rootHexagon, position);
				
				return neighborHexagon;

			case TWO:
				neighborCentroid = generateP2Centroid(rootCentroid, rootInradius);
				neighborHexagon = new Hexagon(neighborCentroid, rootHexagon, position);
				
				return neighborHexagon;

			case THREE:
				neighborCentroid = generateP3Centroid(rootCentroid, rootInradius);
				neighborHexagon = new Hexagon(neighborCentroid, rootHexagon, position);
				
				return neighborHexagon;

			case FOUR:
				neighborCentroid = generateP4Centroid(rootCentroid, rootInradius);
				neighborHexagon = new Hexagon(neighborCentroid, rootHexagon, position);
				
				return neighborHexagon;

			case FIVE:
				neighborCentroid = generateP5Centroid(rootCentroid, rootInradius);
				neighborHexagon = new Hexagon(neighborCentroid, rootHexagon, position);
				
				return neighborHexagon;

			case SIX:
				neighborCentroid = generateP6Centroid(rootCentroid, rootInradius);
				neighborHexagon = new Hexagon(neighborCentroid, rootHexagon, position);
				
				return neighborHexagon;

			case ZERO:
			default: {
				throw new IllegalArgumentException(
						"Only accept position 1 - 6, current position: " + position);
			}
		}
	}

	// public Coordinates generateGisCentroid(Hexagon rootHexagon, NeighborPosition position) {

	// }

	/* Generate Centroid in NeighborPosition */
	private static final Coordinates generateP1Centroid(Coordinates centroid, double inradius) {
		final double centroidX = centroid.getX();
		final double centroidY = centroid.getY();

		return new Coordinates(
				centroidX,
				centroidY - (inradius * 2));
	}

	private static final Coordinates generateP2Centroid(Coordinates centroid, double inradius) {
		final double centroidX = centroid.getX();
		final double centroidY = centroid.getY();

		return new Coordinates(
				centroidX + (inradius * 2 * COS_30_DEG), 
				centroidY - inradius);
	}

	private static final Coordinates generateP3Centroid(Coordinates centroid, double inradius) {
		final double centroidX = centroid.getX();
		final double centroidY = centroid.getY();

		return new Coordinates(
				centroidX + (inradius * 2 * COS_30_DEG),
				centroidY + inradius);
	}

	private static final Coordinates generateP4Centroid(Coordinates centroid, double inradius) {
		final double centroidX = centroid.getX();
		final double centroidY = centroid.getY();

		return new Coordinates(
				centroidX,
				centroidY + 2 * inradius);
	}

	private static final Coordinates generateP5Centroid(Coordinates centroid, double inradius) {
		final double centroidX = centroid.getX();
		final double centroidY = centroid.getY();

		return new Coordinates(
				centroidX - (inradius * 2 * COS_30_DEG),
				centroidY + inradius);
	}

	private static final Coordinates generateP6Centroid(Coordinates centroid, double inradius) {
		final double centroidX = centroid.getX();
		final double centroidY = centroid.getY();

		return new Coordinates(
				centroidX - (inradius * 2 * COS_30_DEG),
				centroidY - inradius);
	}

	/* Generate GIS Centroid in NeighborPosition */
	private static final Coordinates generateP1GisCentroid(Coordinates centroid, double inradius) {
		final double centroidLng = centroid.getLongitude();
		final double centroidLat = centroid.getLatitude();

		final double inradiusLng = SphericalMercatorProjection.xToLongitude(inradius);
		final double inradiusLat = SphericalMercatorProjection.yToLatitude(inradius);

		return new Coordinates(
				centroidLng,
				centroidLat - (inradiusLat * 2));
	}

	private static final Coordinates generateP2GisCentroid(Coordinates centroid, double inradius) {
		final double centroidLng = centroid.getLongitude();
		final double centroidLat = centroid.getLatitude();
		
		final double inradiusLng = SphericalMercatorProjection.xToLongitude(inradius);
		final double inradiusLat = SphericalMercatorProjection.yToLatitude(inradius);

		return new Coordinates(
				centroidLng + (inradiusLng * 2 * COS_30_DEG),
				centroidLat - inradiusLat);
	}

	private static final Coordinates generateP3GisCentroid(Coordinates centroid, double inradius) {
		final double centroidLng = centroid.getLongitude();
		final double centroidLat = centroid.getLatitude();

		final double inradiusLng = SphericalMercatorProjection.xToLongitude(inradius);
		final double inradiusLat = SphericalMercatorProjection.yToLatitude(inradius);

		return new Coordinates(
				centroidLng + (inradiusLng * 2 * COS_30_DEG),
				centroidLat + inradiusLat);
	}

	private static final Coordinates generateP4GisCentroid(Coordinates centroid, double inradius) {
		final double centroidLng = centroid.getLongitude();
		final double centroidLat = centroid.getLatitude();

		final double inradiusLng = SphericalMercatorProjection.xToLongitude(inradius);
		final double inradiusLat = SphericalMercatorProjection.yToLatitude(inradius);

		return new Coordinates(
				centroidLng,
				centroidLat + (inradiusLat * 2));
	}

	private static final Coordinates generateP5GisCentroid(Coordinates centroid, double inradius) {
		final double centroidLng = centroid.getLongitude();
		final double centroidLat = centroid.getLatitude();

		final double inradiusLng = SphericalMercatorProjection.xToLongitude(inradius);
		final double inradiusLat = SphericalMercatorProjection.yToLatitude(inradius);

		return new Coordinates(
				centroidLng - (inradiusLng * 2 * COS_30_DEG),
				centroidLat + inradiusLat);
	}

	private static final Coordinates generateP6GisCentroid(Coordinates centroid, double inradius) {
		final double centroidLng = centroid.getLongitude();
		final double centroidLat = centroid.getLatitude();

		final double inradiusLng = SphericalMercatorProjection.xToLongitude(inradius);
		final double inradiusLat = SphericalMercatorProjection.yToLatitude(inradius);

		return new Coordinates(
				centroidLng - (inradiusLng * 2 * COS_30_DEG),
				centroidLat - inradiusLat);
	}

	/* Generate Centroids */
	private List<Coordinates> generateCentroids(Hexagon rootHexagon) {
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
		 * We use primarily geometric approach (for visual intuitiveness).
		 */
		List<Coordinates> centroids = new ArrayList<Coordinates>();

		// Root hexagon
		centroids.add(0, rootHexagon.getCentroid());

		// Neighbors
		centroids.add(1, new Coordinates(
				centroidX,
				centroidY - 2 * inradius));

		centroids.add(2, new Coordinates(
				centroidX + SQRT_3 * inradius,
				centroidY - inradius));

		centroids.add(3, new Coordinates(
				centroidX + SQRT_3 * inradius,
				centroidY + inradius));

		centroids.add(4, new Coordinates(
				centroidX,
				centroidY + 2 * inradius));

		centroids.add(5, new Coordinates(
				centroidX - SQRT_3 * inradius,
				centroidY + inradius));

		centroids.add(6, new Coordinates(
				centroidX - SQRT_3 * inradius,
				centroidY - inradius));

		return centroids;
	}

	private List<Coordinates> generateGisCentroids(Hexagon rootHexagon) {
		final double centroidLng = rootHexagon.getCentroid().getLongitude();
		final double centroidLat = rootHexagon.getCentroid().getLatitude();
		final double inradius = rootHexagon.getInradius();

		/* Convert inradius (which is currently in Meter unit) to Degrees unit */
		final double inradiusLng = SphericalMercatorProjection.xToLongitude(inradius); // x
		final double inradiusLat = SphericalMercatorProjection.yToLatitude(inradius); // y

		/* Generate gisCentroids 0 - 6 and add to List */
		List<Coordinates> gisCentroids = new ArrayList<Coordinates>();
		// Root hexagon
		gisCentroids.add(0, rootHexagon.getCentroid());

		// Neighbors
		gisCentroids.add(1, new Coordinates(
				centroidLng,
				centroidLat - 2 * inradiusLat));

		gisCentroids.add(2, new Coordinates(
				centroidLng + SQRT_3 * inradiusLng,
				centroidLat - inradiusLat));

		gisCentroids.add(3, new Coordinates(
				centroidLng + SQRT_3 * inradiusLng,
				centroidLat + inradiusLat));

		gisCentroids.add(4, new Coordinates(
				centroidLng,
				centroidLat + 2 * inradiusLat));

		gisCentroids.add(5, new Coordinates(
				centroidLng - SQRT_3 * inradiusLng,
				centroidLat + inradiusLat));

		gisCentroids.add(6, new Coordinates(
				centroidLng - SQRT_3 * inradiusLng,
				centroidLat - inradiusLat));

		return gisCentroids;
	}

	/* Generate Hexagons */
	private List<Hexagon> generateHexagons(List<Coordinates> centroids) {
		List<Hexagon> hexagons = new ArrayList<Hexagon>();

		for (int i = 0; i < centroids.size(); i++) {
			/*
			 * We use switch - case statement on key of gisCentroids Map to determine
			 * HexagonDirection
			 */
			switch (i) {
				case 0:
					hexagons.add(new Hexagon(centroids.get(i), this.rootHexagon, 
							NeighborPosition.ZERO));
					break;
				case 1:
					hexagons.add(new Hexagon(centroids.get(i), this.rootHexagon,
							NeighborPosition.ONE));
					break;
				case 2:
					hexagons.add(new Hexagon(centroids.get(i), this.rootHexagon,
							NeighborPosition.TWO));
					break;
				case 3:
					hexagons.add(new Hexagon(centroids.get(i), this.rootHexagon,
							NeighborPosition.THREE));
					break;
				case 4:
					hexagons.add(new Hexagon(centroids.get(i), this.rootHexagon,
							NeighborPosition.FOUR));
					break;
				case 5:
					hexagons.add(new Hexagon(centroids.get(i), this.rootHexagon,
							NeighborPosition.FIVE));
					break;
				case 6:
					hexagons.add(new Hexagon(centroids.get(i), this.rootHexagon,
							NeighborPosition.SIX));
					break;
				default:
					throw new IllegalStateException(
							"Centroids index must be only 1-6 (inclusive), current index: "
									+ i);
			}
		}

		return hexagons;
	}

	private List<Hexagon> generateGisHexagons(List<Coordinates> gisCentroids) {
		List<Hexagon> gisHexagons = new ArrayList<Hexagon>();

		for (int i = 0; i < gisCentroids.size(); i++) {
			// We use switch - case statement on key of gisCentroids Map to determine
			// HexagonDirection
			switch (i) {
				case 0:
					gisHexagons.add(new Hexagon(gisCentroids.get(i), this.rootHexagon,
							NeighborPosition.ZERO));
					break;
				case 1:
					gisHexagons.add(new Hexagon(gisCentroids.get(i), this.rootHexagon,
							NeighborPosition.ONE));
					break;
				case 2:
					gisHexagons.add(new Hexagon(gisCentroids.get(i), this.rootHexagon,
							NeighborPosition.TWO));
					break;
				case 3:
					gisHexagons.add(new Hexagon(gisCentroids.get(i), this.rootHexagon,
							NeighborPosition.THREE));
					break;
				case 4:
					gisHexagons.add(new Hexagon(gisCentroids.get(i), this.rootHexagon,
							NeighborPosition.FOUR));
					break;
				case 5:
					gisHexagons.add(new Hexagon(gisCentroids.get(i), this.rootHexagon,
							NeighborPosition.FIVE));
					break;
				case 6:
					gisHexagons.add(new Hexagon(gisCentroids.get(i), this.rootHexagon,
							NeighborPosition.SIX));
					break;
				default:
					throw new IllegalStateException(
							"Centroids index must be only 1-6 (inclusive), current index: "
									+ i);
			}
		}

		return gisHexagons;
	}

	public static void main(String[] args) {
		Coordinates centroid = new Coordinates(100, 100);
		Hexagon hexagon = new Hexagon(centroid, 50);
		double inradius = hexagon.getInradius();
		Neighbors neighbors = new Neighbors(hexagon);

		/* Test generatePnthCentroid */
		List<Coordinates> gisCentroids = new ArrayList<Coordinates>();
		for (int i = 1; i < neighbors.getGisCentroids().size(); i++) {
			gisCentroids.add(neighbors.getGisCentroids().get(i));
		}

		List<Coordinates> PGisCentroids = new ArrayList<Coordinates>();
		PGisCentroids.add(Neighbors.generateP1GisCentroid(centroid, inradius));
		PGisCentroids.add(Neighbors.generateP2GisCentroid(centroid, inradius));
		PGisCentroids.add(Neighbors.generateP3GisCentroid(centroid, inradius));
		PGisCentroids.add(Neighbors.generateP4GisCentroid(centroid, inradius));
		PGisCentroids.add(Neighbors.generateP5GisCentroid(centroid, inradius));
		PGisCentroids.add(Neighbors.generateP6GisCentroid(centroid, inradius));
		
		System.out.println("Compare centroids[1-6] to P[1-6] centroids");
		for (int i = 0; i < 6; i++) {
			Coordinates centroidA = gisCentroids.get(i);
			Coordinates centroidB = PGisCentroids.get(i);

			System.out.println(
				centroidA.toGeoJsonPosition() + " equals " + centroidB.toGeoJsonPosition()
				+ ", " + centroidA.equals(centroidB)
			);
		}

		/* Test generateHexagon */
		NeighborPosition positions[] = NeighborPosition.values();

		System.out.println("\nTest generateHexagon()");
		for (NeighborPosition position : positions) {
			if (position != NeighborPosition.ZERO) {
				System.out.println("\n" + "Direction: " + position);
				Hexagon newHex = Neighbors.generateNextHexagon(hexagon, position);
				for (int i = 0; i < 5; i++) {
					System.out.println("Hexagon " + i + ": " + newHex.getCentroid().toGeoJsonPosition());
					newHex = Neighbors.generateNextHexagon(newHex, position);
				}
			}
			
		}
	}
}