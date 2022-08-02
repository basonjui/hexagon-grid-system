package com.masterisehomes.geometryapi.tessellation;

import java.lang.Math;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

import lombok.Getter;
import lombok.ToString;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import com.masterisehomes.geometryapi.hexagon.Coordinates;
import com.masterisehomes.geometryapi.hexagon.Hexagon;
import com.masterisehomes.geometryapi.neighbors.NeighborPosition;
import com.masterisehomes.geometryapi.neighbors.Neighbors;
import com.masterisehomes.geometryapi.geodesy.Harversine;

@ToString
public class AxialClockwiseTessellation {
	// Initialization data
	@Getter
	private final Hexagon rootHexagon;
	@Getter
	private final double circumradius;
	@Getter
	private final double inradius;
	@Getter
	private Boundary boundary;

	/*
	 * CORNER HEXAGONS & EDGE HEXAGONS
	 * ---
	 * 
	 * From a Central Hexagon, you can find 6 immediate Neighbor Hexagons that
	 * fit to the central hexagon on its EDGES - given a centroid & inradius.
	 * 
	 * If you keep extending these 6 Neighbors using their centroids and the same
	 * inradius (distance), you will be able to extend the hexagons infinitely in
	 * 6 diagonal directions (or 3 axes).
	 * 
	 * However, the more you extend, the more hexagons you will miss in between the
	 * diagonal directions, in a systematic way.
	 * 
	 * This gap can be perfectly filled with the right number of hexagons (same
	 * size) to form a Hexagonal Grid Map.
	 * -
	 * https://math.stackexchange.com/questions/2389139/determining-neighbors-in-a-
	 * geometric-hexagon-pattern
	 * 
	 * When you look at a complete Hexagon Grid Map, you will see that the grid map
	 * itself form a large Hexagon (in different orientation), that is tiled by
	 * smaller hexagons perfectly without gaps - this concept is Tessellation.
	 * 
	 * What interesting is, these direct Neighbors from the Central Hexagon, when
	 * extended,
	 * - always become the CORNERS of the Hexagon Grid Map (see the website
	 * above).
	 * - and the hexagons that fill the gaps between these Corner Hexagons, always
	 * become the EDGES of the Grid Map.
	 * 
	 * It is much easier to see this when you look at Hexagon Grid Maps as rings
	 * of hexagons around the Central Hexagons.
	 * The more rings wrap around the Central Hexagons, the more EDGE HEXAGONS
	 * exist between the CORNER HEXAGONS in a special 1:1 ratio.
	 * 
	 * From the 2nd ring onward, the geometric property is:
	 * +1 ring = +1 EDGE HEXAGON
	 */

	/*
	 * Corner Hexagons - used to find Edge Hexagons (based on nthRing)
	 * - nthRing should equals any cornerHexagonList.size()
	 */
	private List<Hexagon> c1Hexagons = new ArrayList<Hexagon>(100);
	private List<Hexagon> c2Hexagons = new ArrayList<Hexagon>(100);
	private List<Hexagon> c3Hexagons = new ArrayList<Hexagon>(100);
	private List<Hexagon> c4Hexagons = new ArrayList<Hexagon>(100);
	private List<Hexagon> c5Hexagons = new ArrayList<Hexagon>(100);
	private List<Hexagon> c6Hexagons = new ArrayList<Hexagon>(100);

	private List<Hexagon> c1GisHexagons = new ArrayList<Hexagon>(100);
	private List<Hexagon> c2GisHexagons = new ArrayList<Hexagon>(100);
	private List<Hexagon> c3GisHexagons = new ArrayList<Hexagon>(100);
	private List<Hexagon> c4GisHexagons = new ArrayList<Hexagon>(100);
	private List<Hexagon> c5GisHexagons = new ArrayList<Hexagon>(100);
	private List<Hexagon> c6GisHexagons = new ArrayList<Hexagon>(100);

	/*
	 * Output data
	 *
	 * Notes: ArrayList needs to be assigned initialCapacity for better performance,
	 * it cuts the cycle to expand the Array when it is full
	 * (default initialCapacity = 10)
	 */
	@Getter
	private List<Coordinates> centroids = new ArrayList<Coordinates>(100);
	@Getter
	private List<Coordinates> gisCentroids = new ArrayList<Coordinates>(100);
	@Getter
	private List<Hexagon> hexagons = new ArrayList<Hexagon>(100);
	@Getter
	private List<Hexagon> gisHexagons = new ArrayList<Hexagon>(100);

	/* Updaters */
	@Getter
	private int totalRings = 0; // keep track of hexagon rings generated
	/* The below updaters are used internally only */
	private int maxRings = 0; // maximum layers of hexagons in a ring required to tessellate
	private int nthRing = 0; // the latest nth rings that tessellate generated

	/* Constructors */
	public AxialClockwiseTessellation(Hexagon rootHexagon) {
		this.rootHexagon = rootHexagon;
		this.circumradius = rootHexagon.getCircumradius();
		this.inradius = rootHexagon.getInradius();
	}

	/* Tessellation */
	public void populateGisHexagons(Boundary boundary) {
		/*
		 * tessellate method is re-runnable
		 * 
		 * Every time this method is run, it does the following actions:
		 * 1. takes in a new Boundary as parameter
		 * 2. clears all the generated centroids & hexagons ArrayList
		 * 3. reset updaters (totalRings, nthRing)
		 * 3. populate new centroids & hexagons with new Boundary
		 */

		// Set boundary to instance
		this.boundary = boundary;

		/*
		 * Clear all tessellation data (in case already generated):
		 * - corner hexagons
		 * - hexagons
		 * - centroids
		 * - tesselllation rings
		 */
		this.clearCornerHexagons();
		this.clearHexagons();
		this.clearCentroids();
		this.resetRings();

		/* Set the maximum amount of tessellation rings */
		this.maxRings = calculateMaxRings(boundary);

		/* Initialize EDGE centroids counter */
		int requiredEdgeHexagons = 0;

		/* Loop tessellation logic until nthRing == maxRing */
		while (this.nthRing <= this.maxRings) {
			switch (this.nthRing) {
				/* Handle special cases: nthRing == 0 -> 1 */
				case 0:
					// Ring 0 is just the rootHexagon (hence "Centroid")
					populateGisRing0(this.rootHexagon);
					break;

				case 1:
					// Ring 1 is basically Neighbors without rootHexagon
					Neighbors neighbors = new Neighbors(this.rootHexagon);
					// TODO: not implemented
					populateGisRing1(neighbors);
					break;
				
				/* nthRing >= 2 */
				default: 
					// Calculate requiredEdgeCentroids
					requiredEdgeHexagons = this.nthRing - 1;

					/*
					 * Axial Clock-wise Tessellation algorithm steps
					 * 
					 * 1. Generate next Corner Centroids (nthRing from origin centroid) c1 - c6
					 * 2. Store Corner Centroids
					 * 3. Store Gis/Pixel Centroids
					 * 4. Start with c5, calculate n Edge Cenroids of Corner Centroids clock-wise
					 * (n = requiredEdgeCentroids)
					 * 5. Store Edge Centroids
					 * 6. Store Gis/Pixel Centroids
					 */

					break;
			}

			// Update nthRing each iteration
			this.nthRing++;
		}

	}

	/* Hexagon rings population */
	private void populateRing0(Hexagon rootHexagon) { // Ring 0 has no corners
		this.hexagons.add(rootHexagon);
	}

	private void populateGisRing0(Hexagon rootHexagon) { // Ring 0 has no corners
		this.gisHexagons.add(rootHexagon);
	}

	private void populateRing1(Neighbors neighbors) {
		List<Hexagon> neighborHexagons = neighbors.getHexagons();

		/* Validate neighbors */
		assert neighborHexagons.size() == 7
				: String.format("neighborHexagons size must equals 7, currently: ",
						neighborHexagons.size());

		/*
		 * For each Neighbor, add it to Corners (1 - 6) based on NeighborPosition 
		 * 
		 * Since we populated rootHexagon already (from populateRing0 method),
		 * we will skip position 0 of Neighbors' hexagons list.
		 */
		for (Hexagon hexagon : neighborHexagons) {
			NeighborPosition position = hexagon.getPosition();

			switch (position) {
				case ZERO:
					// Ignore position ZERO, already added rootHexagon
					break;
				case ONE:
					this.c1Hexagons.add(hexagon);
					break;
				case TWO:
					this.c2Hexagons.add(hexagon);
					break;
				case THREE:
					this.c4Hexagons.add(hexagon);
					break;
				case FOUR:
					this.c4Hexagons.add(hexagon);
					break;
				case FIVE:
					this.c5Hexagons.add(hexagon);
					break;
				case SIX:
					this.c6Hexagons.add(hexagon);
					break;
				// Handle illegal position
				default:
					throw new IllegalStateException("Only position 1-6 are valid, currently: "
							+ position);
			}
		}
		
		/* Populate hexagons with Neighbors 1 - 6 */
		this.hexagons.addAll(neighborHexagons.subList(1, 7)); // 7 is exclusive, why? ask Java doc :)
	}

	private void populateGisRing1(Neighbors neighbors) {
		List<Hexagon> neighborsGisHexagons = neighbors.getGisHexagons();

		/* Validate neighbors */

		assert neighborsGisHexagons.size() == 7
				: String.format("neighborsGisHexagons size must equals 7, currently: ",
						neighborsGisHexagons.size());
		final int NEIGHBORS_SIZE = 7;

		/* Populate Corner Hexagon lists using Neighbors
		 * 
		 * Since we populated rootHexagon already (from populateRing0 method),
		 * we will skip index 0 of Neighbors' hexagons list.
		*/
		// TODO: not implemented
		
		
		// Populate Tessellation's hexagons & gisHexagons
		this.gisHexagons.addAll(neighborsGisHexagons.subList(1, 7));
	}

	private void populateRingNth() {

	}

	private void populateGisRingNth() {

	}

	/* Corner hexagons population */
	

	/* TESSELLATE */
	private void tessellate() {
		/*
		 * GOALS: produce hexagons
		 * 1. generate hexagons 0 (CCI = 0,0,0)
		 * 2. generate hexagon 1 - 6 for each nthRing (with CCI)
		 * 
		 * Given maxRings:
		 * 	While nthRing <= maxRings:
		 * 		Case nthRing == 0:
		 * 			1. Add rootHexagon to hexagons[]
		 * 
		 * 		Case nthRing == 1:
		 * 			1. Generate neighbors from rootHexagon
		 * 			2. Add Neighbors' hexagons 1 - 6 to hexagons[]
		 * 
		 * 		Case nthRing >= 2:
		 * 			For each nthRing (until maxRings):
		 * 				1. For each direction (1 - 6):
		 * 					a. Calculate c,r,s displacement (dpm == 1 * nthRing)
		 * 					b. Generate CORNER centroid
		 * 					c. Generate CORNER Hexagon (centroid, rootHexagon, direction, displacement)
		 * 
		 * 				2. For requiredEdgeHexagons:
		 * 					a. Generate EDGE centroid
		 * 					b. Generate EDGE hexagon
		 */
	}

	/* Reset data */
	private void resetRings() {
		this.totalRings = 0;
		this.maxRings = 0;
		this.nthRing = 0;
	}

	private void clearCornerHexagons() {
		// Corner hexagons
		this.c1Hexagons.clear();
		this.c2Hexagons.clear();
		this.c3Hexagons.clear();
		this.c4Hexagons.clear();
		this.c5Hexagons.clear();
		this.c6Hexagons.clear();

		// Corner GIS hexagons
		this.c1GisHexagons.clear();
		this.c2GisHexagons.clear();
		this.c3GisHexagons.clear();
		this.c4GisHexagons.clear();
		this.c5GisHexagons.clear();
		this.c6GisHexagons.clear();
	}

	private void clearCentroids() {
		this.centroids.clear();
		this.gisCentroids.clear();
	}

	private void clearHexagons() {
		this.hexagons.clear();
		this.gisHexagons.clear();
	}

	/* Calculations */
	private int calculateMaxRings(Boundary boundary) {
		// Get boundary coordinates
		double minLat = boundary.getMinLatitude();
		double minLng = boundary.getMinLongitude();
		double maxLat = boundary.getMaxLatitude();
		double maxLng = boundary.getMaxLongitude();

		// Calculate the Great-circle Distance between the MIN and MAX coordinates
		double maxBoundaryDistance = Harversine.distance(minLat, minLng, maxLat, maxLng);

		/*
		 * Hexagon's height - distance between hexagon neighbors is:
		 * = inradius * 2
		 * 
		 * Given maxDistance,
		 * the maximum number of hexagons stack up in any axial direction is:
		 * = maxDistance / inradius * 2
		 * 
		 * However, we need to use Math.ceil() to round it up to nearest int
		 */
		double hexagonDistance = this.inradius * 2;

		/*
		 * In Hexagons grids, we can look at it with 3 primary axes (the 6 neighbor
		 * directions):
		 * - maxAxialHexagons is the maximum amount of hexagons that can stack up (from
		 * edges) in those 3 axes to cover the grid map largest diameter.
		 */
		int maxAxialHexagons = (int) Math.ceil(maxBoundaryDistance / hexagonDistance);

		/*
		 * We then arrive at 2 cases: odd vs even maxAxialHexagons
		 * 
		 * But to form a regular Hexagon Grids, the maxAxialHexagons must always be odd
		 * in order to divide to an even amount of Hexagons on each side of the axis
		 * -> because it has to subtract rootHexagon from the axis:
		 * 
		 * 	maxAxialHexagons = side_1_hexagons + rootHexagon + side_2_hexagons
		 */
		int maximumRings;
		if (maxAxialHexagons % 2 == 0) {
			/*
			 * If even: we can think of it as the rootHexagon is already subtracted
			 * 
			 * Then, because each ring of hexagons consist of 2 hexagons (1 on each side
			 * of the axis), we divide by 2 to get the number of hexagon rings.
			 */
			maximumRings = maxAxialHexagons / 2;
		} else {
			/* If odd: subtract rootHexagon from maxAxialHexagons and divide by 2 */
			maximumRings = (maxAxialHexagons - 1) / 2;
		}

		return maximumRings;
	}

	public static void main(String[] args) {
		Gson gson = new GsonBuilder().setPrettyPrinting().create();

		Coordinates origin = new Coordinates(10, 10);

		Hexagon hexagon = new Hexagon(origin, 5000);
		Neighbors neighbors = new Neighbors(hexagon);

		AxialClockwiseTessellation tessellation = new AxialClockwiseTessellation(hexagon);

		Boundary boundary = new Boundary(Arrays.asList(10.0, 10.0, 10.5, 10.5));
		int maxRings = tessellation.calculateMaxRings(boundary);

		// Test harversine
		double greatCircleDistance = Harversine.distance(boundary.getMinLatitude(), boundary.getMinLongitude(),
				boundary.getMaxLatitude(), boundary.getMaxLongitude());

		System.out.println("Great-circle distance: " + greatCircleDistance);
		System.out.println("Max hexagon rings: " + maxRings);
		System.out.println("inradius: " + hexagon.getInradius());

		tessellation.populateRing0(hexagon);
		tessellation.populateRing1(neighbors);
		
		System.out.println("Tessellation ring 0 + 1:");
		tessellation.getHexagons().forEach((hex) -> {
			System.out.println(hex.getCentroid());
		});

		System.out.println("\nNeighbors:");
		neighbors.getHexagons().forEach((hex) -> {
			System.out.println(hex.getCentroid());
		});
	}
}