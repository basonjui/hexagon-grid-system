package com.masterisehomes.geometryapi.tessellation;

import java.lang.Math;
import java.util.List;

import java.util.ArrayList;
import java.util.Arrays;

import lombok.Getter;
import lombok.ToString;

import com.masterisehomes.geometryapi.hexagon.Coordinates;
import com.masterisehomes.geometryapi.hexagon.Hexagon;
import com.masterisehomes.geometryapi.neighbors.NeighborPosition;
import com.masterisehomes.geometryapi.neighbors.Neighbors;
import com.masterisehomes.geometryapi.geodesy.Harversine;

/**
 * TESSELLATION CONCEPTS: A HEXAGON-SHAPED GRID
 * 
 * In this Hexagon Tessellation algorithm, we will form a hexagon grid that have
 * a shape of a regular hexagon:
 * - https://math.stackexchange.com/a/2389652
 * 
 * --- RINGS OF HEXAGONS
 * Basically, the grid is made of "rings of hexagons" wrap around the Central Hexagon (root hexagon).
 * 
 * These hexagon rings have 2 special cases: Ring 0 & Ring 1
 * - Ring 0: is a conceptual ring - it is just the Central Hexagon.
 * - Ring 1: is the first visible ring, where there are 7 hexagons wrapped around the Central Hexagon.
 * 
 * But from Ring 2 onward, we can see a consistent geometric pattern of the hexagon grid, 
 * where each ring always consist of: 6 Corner Hexagons and n Edge Hexagons.
 * 
 * --- CORNER & EDGE HEXAGONS
 * When you look at a complete Hexagon-shaped Grid, you will see that the grid itself form a large Hexagon (in different orientation), 
 * that is tiled by smaller hexagons perfectly without gaps - this concept is Tessellation.
 * 
 * What interesting is, these direct Neighbors from the Central Hexagon, when extended:
 * 	1. always become the CORNERS of the Hexagon Grid (see the website above).
 * 	2. and the hexagons that fill the gaps between these Corner Hexagons, always become the EDGES of the Grid.
 * 
 * This n Edge Hexagon has a linear relationship with the n Rings, where:
 * n_edge_hexagons = n_rings - 2.
 * 
 * For examples:
 * - at Ring 2, we have 2 - 2 = 0 Edge Hexagon
 * - at Ring 9, we have 9 - 2 = 7 Edge Hexagons
 * 
 * ---
 * So in order to create a hexagon-shaped Tessellation, we need to:
 * 1. Calculate tessellationDistance (equals to largestBoundaryDistance)
 * 2. Calculate requiredCornerHexagons (given tessellationDistance &
 * hexagonMinimalDiameter)
 * 3. Calculate requiredEdgeHexagons for each ring
 */

@ToString
public class CornerEdgeTessellation {
	/* Initialization data */
	@Getter
	private final Hexagon rootHexagon;
	@Getter
	private final double circumradius;
	@Getter
	private final double inradius;
	@Getter
	private Boundary boundary;
	
	/*
	 * Centroids & Hexagons
	 *
	 * Notes: ArrayList needs to be assigned initialCapacity for better performance,
	 * it cuts the cycle to expand the Array when it is full
	 * (default initialCapacity = 10)
	 */
	@Getter
	private final List<Coordinates> centroids = new ArrayList<Coordinates>(100);
	@Getter
	private final List<Coordinates> gisCentroids = new ArrayList<Coordinates>(100);
	@Getter
	private final List<Hexagon> hexagons = new ArrayList<Hexagon>(100);
	@Getter
	private final List<Hexagon> gisHexagons = new ArrayList<Hexagon>(100000);

	/*
	 * Corner Hexagons - used to find Edge Hexagons (based on nthRing)
	 * - nthRing should equals any cornerHexagonList.size()
	 */
	private final List<Hexagon> c1Hexagons = new ArrayList<Hexagon>(100);
	private final List<Hexagon> c2Hexagons = new ArrayList<Hexagon>(100);
	private final List<Hexagon> c3Hexagons = new ArrayList<Hexagon>(100);
	private final List<Hexagon> c4Hexagons = new ArrayList<Hexagon>(100);
	private final List<Hexagon> c5Hexagons = new ArrayList<Hexagon>(100);
	private final List<Hexagon> c6Hexagons = new ArrayList<Hexagon>(100);

	private final List<Hexagon> c1GisHexagons = new ArrayList<Hexagon>(100000);
	private final List<Hexagon> c2GisHexagons = new ArrayList<Hexagon>(100000);
	private final List<Hexagon> c3GisHexagons = new ArrayList<Hexagon>(100000);
	private final List<Hexagon> c4GisHexagons = new ArrayList<Hexagon>(100000);
	private final List<Hexagon> c5GisHexagons = new ArrayList<Hexagon>(100000);
	private final List<Hexagon> c6GisHexagons = new ArrayList<Hexagon>(100000);

	/* Updaters */
	@Getter
	private int totalRings = 0; 	// keep track of hexagon rings generated
	// Internal
	private int requiredRings = 0; 	// maximum layers of hexagons in a ring required to tessellate
	private int currentRing = 0; 	// the current/latest tessellation ring created

	/* Basic stats here */
	@Getter
	private int totalHexagons = 0;
	@Getter
	private double tessellationInradius = 0;
	@Getter
	private double tessellationCircumradius = 0;

	/* Constructors */
	public CornerEdgeTessellation(Hexagon rootHexagon) {
		this.rootHexagon = rootHexagon;
		this.circumradius = rootHexagon.getCircumradius();
		this.inradius = rootHexagon.getInradius();
	}

	/*
	 * Tessellation
	 * 
	 * tessellate(boundary) is re-runnable, it deletes all previous states of
	 * AxialClockwiseTessellation before tessellate.
	 */
	public final void tessellate(Boundary boundary) {
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
		this.requiredRings = calculateRequiredRings(boundary);

		/* Populate hexagons */
		while (this.currentRing < this.requiredRings) {
			// GIS Hexagons
			populateGisHexagons(this.currentRing);

			// Pixel Hexagons
			// TODO: not yet implemented

			// Update rings
			this.totalRings++;
			this.currentRing++;
		}

		/* Calculate results */
		if (this.hexagons.isEmpty()) {
			this.totalHexagons = this.gisHexagons.size();
		} else if (this.gisHexagons.isEmpty()) {
			this.totalHexagons = this.hexagons.size();
		} else {
			final int hexSize = this.hexagons.size();
			final int gisHexSize = this.gisHexagons.size();

			assert hexSize == gisHexSize;
			this.totalHexagons = (hexSize + gisHexSize) / 2;
		}

		/* Print tessellation results */
		List<String> TESSELLATION_RESULTS = Arrays.asList(
			"Centroid",
			"Circumradius",
			"Boundary",
			"Tessellation Inradius",
			"Tessellation Circumradius",
			"Total Hexagons"
		);

		// Get values and apply padding to results
		int padding = 26;
		for (int i = 0; i < TESSELLATION_RESULTS.size(); i++) {
			String result = TESSELLATION_RESULTS.get(i);

			String value;
			switch(result) {
				case "Centroid":
					value = this.rootHexagon.getCentroid().toWKT();
					break;
				case "Circumradius":
					value = Double.toString(this.circumradius);
					break;
				case "Boundary":
					value = this.boundary.getMinCoordinates().toWKT() + ", " + this.boundary.getMaxCoordinates().toWKT();
					break;
				case "Tessellation Inradius":
					value = Double.toString(this.tessellationInradius);
					break;
				case "Tessellation Circumradius":
					value = Double.toString(this.tessellationCircumradius);
					break;
				case "Total Hexagons":
					value = Integer.toString(this.totalHexagons);
					break;
				default:
					value = String.format("ERROR: mishandled logic for case '%s'", result);
			}

			TESSELLATION_RESULTS.set(i, String.format("%-" + padding + "s", result) + ": " + value);
		}

		// Print results
		System.out.println("------ Tessellation results ------");
		for (String result : TESSELLATION_RESULTS) {
			System.out.println(result);
		}
	}

	/* Hexagons population */
	private final void populateGisHexagons(int nthRing) {
		switch (nthRing) {
			/* Handle special cases: nthRing == 0 -> 1 */
			case 0:
				// Ring 0 is just the rootHexagon
				populateGisRing0(this.rootHexagon);
				break;

			case 1:
				// Ring 1 is basically Neighbors without rootHexagon
				final Neighbors neighbors = new Neighbors(this.rootHexagon);
				populateGisRing1(neighbors);
				break;

			/* nthRing >= 2 */
			default:
				// Populate GIS Rings
				populateGisRingN(nthRing);
				break;
		}
	}

	/* Rings population */
	// private final void populateRing0(Hexagon rootHexagon) { // Ring 0 has no corners
	// 	this.hexagons.add(rootHexagon);
	// }

	private final void populateGisRing0(Hexagon rootHexagon) { // Ring 0 has no corners
		this.gisHexagons.add(rootHexagon);
	}

	private final void populateGisRing1(Neighbors neighbors) {
		final List<Hexagon> neighborGisHexagons = neighbors.getGisHexagons();

		/* Validate neighbors */
		assert neighborGisHexagons.size() == 7
				: String.format("neighborHexagons size must equals 7, currently: ",
						neighborGisHexagons.size());

		/*
		 * For each Neighbor, add it to Corners (1 - 6) based on NeighborPosition
		 * 
		 * Since we populated rootHexagon already (from populateRing0 method),
		 * we will skip position 0 of Neighbors' hexagons list.
		 */
		for (Hexagon gisHexagon : neighborGisHexagons) {
			final NeighborPosition position = gisHexagon.getPosition();

			switch (position) {
				case ZERO:
					// Ignore position ZERO, already added rootHexagon
					break;
				case ONE:
					this.c1GisHexagons.add(gisHexagon);
					break;
				case TWO:
					this.c2GisHexagons.add(gisHexagon);
					break;
				case THREE:
					this.c3GisHexagons.add(gisHexagon);
					break;
				case FOUR:
					this.c4GisHexagons.add(gisHexagon);
					break;
				case FIVE:
					this.c5GisHexagons.add(gisHexagon);
					break;
				case SIX:
					this.c6GisHexagons.add(gisHexagon);
					break;
				// Handle illegal position
				default:
					throw new IllegalStateException("Only position 1-6 are valid, currently: "
							+ position);
			}
		}

		/* Populate hexagons with Neighbors 1 - 6 */
		this.gisHexagons.addAll(neighborGisHexagons.subList(1, 7)); // 7 is exclusive, why? ask Java doc :)
	}

	private final void populateGisRingN(int currentRing) {
		/* Validate nthRing */
		assert currentRing > 1 : "nthRing must be > 1, current nthRing: " + currentRing;
		assert currentRing < this.requiredRings : String.format("nthRing must be < requiredRings (%s), current nthRing: %s",
				this.requiredRings, currentRing);

		/*
		 * Calculate previousCornerHexagonIndex and requiredEdgeHexagons
		 * 
		 * --- previousCornerHexagonIndex
		 * only starts when nthRing=2 (Since ring 0 & ring 1 have a different geometric
		 * pattern to tessellate)
		 * 
		 * --- requiredEdgeHexagons
		 * For every hexagon ring after ring 1, the requiredEdgeHexagons to fill the
		 * grid is equal to nthRing - 1
		 */
		final int previousCornerHexagonIndex = currentRing - 2;
		final int requiredEdgeHexagons = currentRing - 1;

		/*
		 * --- Generate Corner Hexagons (1-6)
		 * For each Corner, generate n Edge Hexagons (where n = requiredEdgeHexagons) 
		 */
		for (NeighborPosition position : NeighborPosition.values()) { // Iterate through all Neighbors Positions
			// Corner & Edge hexagons variables
			Hexagon previousCornerHexagon;
			Hexagon nextCornerHexagon;
			List<Hexagon> edgeHexagons;

			// Assign iterating position to the next cornerHexagonPosition
			NeighborPosition nextCornerHexagonPosition = position;
			switch (nextCornerHexagonPosition) {
				case ZERO:
					/*
					 * Skip default position ZERO, only Root Hexagon has position ZERO
					 */
					break;

				case ONE:
					// Get the latestHexagon in Corner List
					previousCornerHexagon = c1GisHexagons.get(previousCornerHexagonIndex); 

					// Generate and add nextGisCornerHexagon
					nextCornerHexagon = Neighbors.generateNeighborGisHexagon(previousCornerHexagon, nextCornerHexagonPosition);
					c1GisHexagons.add(nextCornerHexagon);
					gisHexagons.add(nextCornerHexagon);

					// Generate gisEdgeHexagons from Corner Hexagon, Corner Position & requiredEdgeHexagons
					edgeHexagons = generateGisEdgeHexagons(nextCornerHexagon, nextCornerHexagonPosition, requiredEdgeHexagons);
					gisHexagons.addAll(edgeHexagons);
					break;
					
				case TWO:
					previousCornerHexagon = c2GisHexagons.get(previousCornerHexagonIndex);

					nextCornerHexagon = Neighbors.generateNeighborGisHexagon(previousCornerHexagon, nextCornerHexagonPosition);
					c2GisHexagons.add(nextCornerHexagon);
					gisHexagons.add(nextCornerHexagon);

					edgeHexagons = generateGisEdgeHexagons(nextCornerHexagon, nextCornerHexagonPosition, requiredEdgeHexagons);
					gisHexagons.addAll(edgeHexagons);
					break;

				case THREE:
					previousCornerHexagon = c3GisHexagons.get(previousCornerHexagonIndex);

					nextCornerHexagon = Neighbors.generateNeighborGisHexagon(previousCornerHexagon, nextCornerHexagonPosition);
					c3GisHexagons.add(nextCornerHexagon);
					gisHexagons.add(nextCornerHexagon);

					edgeHexagons = generateGisEdgeHexagons(nextCornerHexagon, nextCornerHexagonPosition, requiredEdgeHexagons);
					gisHexagons.addAll(edgeHexagons);
					break;

				case FOUR:
					previousCornerHexagon = c4GisHexagons.get(previousCornerHexagonIndex);

					nextCornerHexagon = Neighbors.generateNeighborGisHexagon(previousCornerHexagon, nextCornerHexagonPosition);
					c4GisHexagons.add(nextCornerHexagon);
					gisHexagons.add(nextCornerHexagon);

					edgeHexagons = generateGisEdgeHexagons(nextCornerHexagon, nextCornerHexagonPosition, requiredEdgeHexagons);
					gisHexagons.addAll(edgeHexagons);
					break;

				case FIVE:
					previousCornerHexagon = c5GisHexagons.get(previousCornerHexagonIndex);

					nextCornerHexagon = Neighbors.generateNeighborGisHexagon(previousCornerHexagon, nextCornerHexagonPosition);
					c5GisHexagons.add(nextCornerHexagon);
					gisHexagons.add(nextCornerHexagon);

					edgeHexagons = generateGisEdgeHexagons(nextCornerHexagon, nextCornerHexagonPosition, requiredEdgeHexagons);
					gisHexagons.addAll(edgeHexagons);
					break;

				case SIX:
					previousCornerHexagon = c6GisHexagons.get(previousCornerHexagonIndex);

					nextCornerHexagon = Neighbors.generateNeighborGisHexagon(previousCornerHexagon, nextCornerHexagonPosition);
					c6GisHexagons.add(nextCornerHexagon);
					gisHexagons.add(nextCornerHexagon);

					edgeHexagons = generateGisEdgeHexagons(nextCornerHexagon, nextCornerHexagonPosition, requiredEdgeHexagons);
					gisHexagons.addAll(edgeHexagons);
					break;

				default:
					throw new IllegalStateException(
							"nextCornerHexagonPosition should never reach this state, current position: "
							+ nextCornerHexagonPosition);
			}
		}
	}

	/* Corner - Edge hexagons population */
	private final List<Hexagon> generateGisEdgeHexagons(Hexagon gisCornerHexagon, NeighborPosition cornerPosition, int quantity) {
		assert quantity >= 1:
			"(int) quantity must be larger than 1, currently: " + quantity;

		final List<Hexagon> gisEdgeHexagons = new ArrayList<Hexagon>(1000);
		assert gisEdgeHexagons.isEmpty():
			"gisEdgeHexagons List must be empty when initialized, current size: " + gisEdgeHexagons.size();

		/* Determine edgePosition based on cornerPosition */
		final NeighborPosition edgePosition;
		switch(cornerPosition) {
			case ONE:
				edgePosition = NeighborPosition.THREE;
				break;
			case TWO:
				edgePosition = NeighborPosition.FOUR;
				break;
			case THREE:
				edgePosition = NeighborPosition.FIVE;
				break;
			case FOUR:
				edgePosition = NeighborPosition.SIX;
				break;
			case FIVE:
				edgePosition = NeighborPosition.ONE;
				break;
			case SIX:
				edgePosition = NeighborPosition.TWO;
				break;

			case ZERO:
			default: {
				throw new IllegalStateException(
					"Should never reach this corner, current corner: "
					+ cornerPosition);
			}
		}

		/* Handle generation logic for different quantities */
		switch (quantity) {
			case 1:
				gisEdgeHexagons.add(
					Neighbors.generateNeighborGisHexagon(gisCornerHexagon, edgePosition));

				return gisEdgeHexagons;

			default: // Handle all cases where quantity of Edge Hexagon > 1
				while (gisEdgeHexagons.size() < quantity) {
					/*
					 * When there are more than 1 Edge hexagons, there are 2 steps:
					 * 1. Generate the first edge hexagon
					 * 2. While generated edge hexagons < quantity:
					 * 	a. get previousEdgeHexagon
					 * 	b. generate nextEdgeHexagon from previousEdgeHexxagon
					 */
					if (gisEdgeHexagons.isEmpty()) {
						gisEdgeHexagons.add(
							Neighbors.generateNeighborGisHexagon(gisCornerHexagon, edgePosition));
					} else {
						// Get previous edge hexagon in List
						Hexagon previousGisEdgeHexagon = gisEdgeHexagons.get(gisEdgeHexagons.size() - 1);

						Hexagon nextGisEdgeHexagon = Neighbors.generateNeighborGisHexagon(previousGisEdgeHexagon, edgePosition);
						gisEdgeHexagons.add(nextGisEdgeHexagon);
					}
				}

				return gisEdgeHexagons;
		}
	}

	/* Calculate RequiredRings */
	private final int calculateRequiredRings(Boundary boundary) {
		/**
		 * ERROR MARGINS
		 * 
		 * Geometrically, at the outer most ring, 1/6 the area of each hexagon can be
		 * missed.
		 * 
		 * These adjustment constants serve as safe rounded values to ensure that no POI in a
		 * given Boundary, even when accurately calculated, is missed due to the
		 * geometric property of AxialClockwiseTessellation.
		 */
		final int GRID_GEOMETRIC_ERROR_MARGIN = 1;
		final int CENTROID_PLACEMENT_ERROR_MARGIN = 1;
		final int RING_ERROR_MARGIN = GRID_GEOMETRIC_ERROR_MARGIN + CENTROID_PLACEMENT_ERROR_MARGIN;

		/*
		 * Calculate the requiredTessellationDistance from boundary by Harversine formula
		 * 
		 * is the minimum distance required to generate Grid Map that can cover boundary
		 * fully - this distance can grow up to maxBoundaryDistance, depending where the
		 * rootHexagon is placed within Boundary.
		 */
		final Coordinates minCoordinates = boundary.getMinCoordinates();
		final Coordinates maxCoordinates = boundary.getMaxCoordinates();
		final Coordinates centroidCoordinates = rootHexagon.getCentroid();

		// Calculate distance between Boundary's minCoordinates/maxCoordinates to grid centroid
		final double minCoordinatesDistance = Harversine.distance(minCoordinates, centroidCoordinates);
		final double maxCoordinatesDistance = Harversine.distance(maxCoordinates, centroidCoordinates);

		// Determine tessellationDistance (equals to the maxBoundaryDistance)
		final double largestBoundaryDistance;
		if (minCoordinatesDistance >= maxCoordinatesDistance) {
			largestBoundaryDistance = minCoordinatesDistance;
		} else {
			largestBoundaryDistance = maxCoordinatesDistance;
		}
		// this.tessellationDistance = largestBoundaryDistance;

		/*
		 * Hexagon Minimal Diameter
		 * 
		 * The distance from a hexagon edge to the opposite edge 
		 * (basically neighbor distance: 2 * inradius).
		 * 
		 * Since hexagons tile on edges in Tessellation, this is the coverage 
		 * length of a single hexagon in a grid map.
		 */
		final double hexagonMinimalDiameter = this.inradius * 2;

		/*
		 * --- PROBLEM
		 * However, keep in mind that the Grid itself is Hexagon-shaped!
		 * Therefore, it inherits all the geometric properties of a hexagon as the followings:
		 * - Hexagon Grid Centroid			: the Central Hexagon of the grid.
		 * - Hexagon Grid Circumradius (R)	: the sum of the Minimal Diameters from Central Hexagon to the outer-most Corner Hexagon.
		 * - Hexagon Grid Inradius (r)		: can be calculated with: r = R * √3/2
		 * 
		 * Because our Grid is hexagon-shaped, and a hexagon has 2 radiuses, therefore, 
		 * our grid will always cover less boundary distance at where the Edge Hexagons are 
		 * (due to them being the Inradius of the grid).
		 * -> This cause our Hexagon Grid to miss some boundary areas at Edges.
		 * 
		 * --- SOLUTION
		 * Previously, our tessellationDistance == largestBoundaryDistance. With this logic, 
		 * we are ACTUALLY setting the tessellationDistance to be the Circumradius of the Grid.
		 * 
		 * Since the tessellationDistance is the EXACT distance to cover the required Boundary, 
		 * of course the Inradius of the grid will not be able to cover some area of the boundary! 
		 * - because: Circumradius > Inradius
		 *  
		 * To solve this, we will CONSIDER the largestBoundaryDistance to be the Inradius of the grid - 
		 * which is the MINIMUM DISTANCE that the grid has to be ABLE TO COVER! 
		 * Previously, we considered it to be the Circumradius of the grid - which is the 
		 * MAXIMUM DISTANCE that the grid can cover. 
		 * 
		 * Now, given that the largestBoundaryDistance is the Inradius of the tessellation,
		 * we will now convert that Inradius -> Circumradius. 
		 * - Pseudocode:
		 * 	// Step 1: set largestBoundaryDistance to be grid's Inradius
		 * 	tessellationInradius = largestBoundaryDistance;
		 * 
		 * 	// Step 2: convert Inradius -> Circumradius
		 * 	tessellationCircumradius = tessellationInradius * 2/√3
		 * 
		 * Given a tessellationDistance, we can calculate the number of hexagons
		 * required to stack up to cover that distance. 
		 * 	totalHexagons = tessellationDistance / hexagon_length
		 */

		final double tessellationInradius = largestBoundaryDistance;
		final double tessellationCircumradius = tessellationInradius * 2 / Math.sqrt(3);

		// Set Tessellation's inradius & circumradius
		this.tessellationInradius = tessellationInradius;
		this.tessellationCircumradius = tessellationCircumradius;

		// Round-up hexagons by Math.ceil() so that we do not lose any coverage area
		final int requiredCornerHexagons = (int) Math.ceil(tessellationCircumradius / hexagonMinimalDiameter);

		/*
		 * Calculate the Required Rings
		 * 
		 * In a normal case, where Centroid is always in the exact middle of the
		 * Boundary, we would calculate the Minimum Required Rings by:
		 * 	minAxialHexagons / 2 - n (where n = 0 or 1 depends on ODD or EVEN case)
		 * 
		 * However, we need to ensure that this works even in the Worst case scenario,
		 * where the Centroid could be place at the Start or End coordinates of the
		 * Boundary.
		 * 
		 * We can do this by adding a RING_ERROR_MARGIN to requiredCornerHexagons
		 */
		final int requiredRings = requiredCornerHexagons + RING_ERROR_MARGIN;
		
		return requiredRings;
	}

	/* Reset data */
	private final void resetRings() {
		totalRings = 0;
		requiredRings = 0;
		currentRing = 0;
	}

	private final void clearCornerHexagons() {
		// Corner hexagons
		c1Hexagons.clear();
		c2Hexagons.clear();
		c3Hexagons.clear();
		c4Hexagons.clear();
		c5Hexagons.clear();
		c6Hexagons.clear();

		// Corner GIS hexagons
		c1GisHexagons.clear();
		c2GisHexagons.clear();
		c3GisHexagons.clear();
		c4GisHexagons.clear();
		c5GisHexagons.clear();
		c6GisHexagons.clear();
	}

	private final void clearCentroids() {
		centroids.clear();
		gisCentroids.clear();
	}

	private final void clearHexagons() {
		hexagons.clear();
		gisHexagons.clear();
	}
}