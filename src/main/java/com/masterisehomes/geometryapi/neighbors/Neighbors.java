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

	/* Constructor */
	public Neighbors(Hexagon rootHexagon) {
		this.rootHexagon = rootHexagon;

		this.centroids = generateCentroids(rootHexagon);
		this.hexagons = generateHexagons(this.centroids);

		this.gisCentroids = generateGisCentroids(rootHexagon);
		this.gisHexagons = generateGisHexagons(this.gisCentroids);
	}

	/* Public methods */
	public static Coordinates generateCentroid(Hexagon rootHexagon, NeighborPosition position, int nthNeighbor) {
		/* Validate nthNeighbor */
		if (nthNeighbor <= 0) {
			throw new InvalidParameterException("Invalid nthNeighbor, must be >= 1, currently: " + nthNeighbor);
		}
		
		/* Constants */
		final double SQRT_3 = Math.sqrt(3);
		final double centroidX = rootHexagon.getCentroid().getX();
		final double centroidY = rootHexagon.getCentroid().getY();
		final double rootInradius = rootHexagon.getInradius();
		// Calculate nthInradius (inradius of the nthNeighbor)
		final double nthInradius = rootInradius * nthNeighbor;

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

	public static Coordinates generateGisCentroid(Hexagon rootHexagon, NeighborPosition position, int nthNeighbor) {
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

	/* Internal methods */
	private List<Coordinates> generateCentroids(Hexagon rootHexagon) {
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
		final double SQRT_3 = Math.sqrt(3);
		final double centroidLng = rootHexagon.getCentroid().getLongitude();
		final double centroidLat = rootHexagon.getCentroid().getLatitude();
		final double inradius = rootHexagon.getInradius();

		// Convert inradius (which is currently in Meter unit) to Degrees unit
		final double inradiusLng = SphericalMercatorProjection.xToLongitude(inradius); // x
		final double inradiusLat = SphericalMercatorProjection.yToLatitude(inradius); // y

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
		Neighbors neighbors = new Neighbors(hexagon);

		for (int i = 0; i < neighbors.getHexagons().size(); i++) {
			System.out.println(
				neighbors.getHexagons().get(i).getPosition()
			);
		}
	}
}