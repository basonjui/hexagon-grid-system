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

    /* Corner Centroids - numbered the same way as Neighbors directions */
    private Map<Integer, List<Coordinates>> cornerCentroids = new LinkedHashMap<Integer, List<Coordinates>>();
    private List<Coordinates> c1Centroids = new ArrayList<Coordinates>(100);
    private List<Coordinates> c2Centroids = new ArrayList<Coordinates>(100);
    private List<Coordinates> c3Centroids = new ArrayList<Coordinates>(100);
    private List<Coordinates> c4Centroids = new ArrayList<Coordinates>(100);
    private List<Coordinates> c5Centroids = new ArrayList<Coordinates>(100);
    private List<Coordinates> c6Centroids = new ArrayList<Coordinates>(100);

    private Map<Integer, List<Coordinates>> cornerGisCentroids = new LinkedHashMap<Integer, List<Coordinates>>();
    private List<Coordinates> c1GisCentroids = new ArrayList<Coordinates>(100);
    private List<Coordinates> c2GisCentroids = new ArrayList<Coordinates>(100);
    private List<Coordinates> c3GisCentroids = new ArrayList<Coordinates>(100);
    private List<Coordinates> c4GisCentroids = new ArrayList<Coordinates>(100);
    private List<Coordinates> c5GisCentroids = new ArrayList<Coordinates>(100);
    private List<Coordinates> c6GisCentroids = new ArrayList<Coordinates>(100);

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
    public void populateGisCentroids(Boundary boundary) {
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
         * - directional centroids
         * - centroids
         * - hexagons
         * - updaters
         */
        this.clearDirectionalCentroids();
        this.clearCentroids();
        this.clearHexagons(); // both hexagons and gisHexagons
        this.clearHexagonRings();

        // Set the maximum amount of tessellation rings
        this.maxRings = calculateMaxRings(boundary);

        // Initialize EDGE centroids counter
        int requiredEdgeCentroids = 0;

        // Loop tessellation logic until nthRing == maxRing
        while (this.nthRing <= this.maxRings) {

            switch (this.nthRing) {
                /* Handle special cases: 0 - 1 */
                case 0:
                    // Ring 0 is just the rootHexagon (hence "Centroid")
                    populateRing0Centroid(this.rootHexagon, "gis");
                    break;

                case 1:
                    Neighbors neighbors = new Neighbors(this.rootHexagon);

                    // Ring 1 is basically Neighbors without rootHexagon
                    populateRing1Centroids(neighbors, "gis");
                    break;

                default: // nthRing >= 2
                    // Calculate requiredEdgeCentroids
                    requiredEdgeCentroids = this.nthRing - 1;

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

    /*
     * Data population
     */
    private void populateRing0Centroid(Hexagon rootHexagon, String type) {
        Coordinates rootCentroid = rootHexagon.getCentroid();

        if (type == "gis") {
            this.gisCentroids.add(rootCentroid);
        } else {
            this.centroids.add(rootCentroid);
        }
    }

    private void populateRing1Centroids(Neighbors neighbors, String type) {
        if (type == "gis") {
            // Get GIS centroids Map
            Map<Integer, Coordinates> neighborsGisCentroids = neighbors.getGisCentroids();

            // Exclude neighbors' rootHexagon centroid
            for (int i = 1; i <= 6; i++) {
                // populate Corner Centroids
                switch (i) {
                    case 1:
                        this.c1GisCentroids.add(neighborsGisCentroids.get(i));
                        break;
                    case 2:
                        this.c2GisCentroids.add(neighborsGisCentroids.get(i));
                        break;
                    case 3:
                        this.c3GisCentroids.add(neighborsGisCentroids.get(i));
                        break;
                    case 4:
                        this.c4GisCentroids.add(neighborsGisCentroids.get(i));
                        break;
                    case 5:
                        this.c5GisCentroids.add(neighborsGisCentroids.get(i));
                        break;
                    case 6:
                        this.c6GisCentroids.add(neighborsGisCentroids.get(i));
                        break;
                    default:
                        throw new IllegalStateException(
                                "Should not reach this code, check logic where nthRing == 1");
                }

                // populate this.gisCentroids
                this.gisCentroids.add(neighborsGisCentroids.get(i));
            }
        } else { // Pixel; by default if not "gis" type
            Map<Integer, Coordinates> neighborsCentroids = neighbors.getCentroids();

            // Exclude neighbors' rootHexagon centroid
            for (int i = 1; i <= 6; i++) {
                // populate Corner Centroids
                switch (i) {
                    case 1:
                        this.c1Centroids.add(neighborsCentroids.get(i));
                        break;
                    case 2:
                        this.c2Centroids.add(neighborsCentroids.get(i));
                        break;
                    case 3:
                        this.c3Centroids.add(neighborsCentroids.get(i));
                        break;
                    case 4:
                        this.c4Centroids.add(neighborsCentroids.get(i));
                        break;
                    case 5:
                        this.c5Centroids.add(neighborsCentroids.get(i));
                        break;
                    case 6:
                        this.c6Centroids.add(neighborsCentroids.get(i));
                        break;
                    default:
                        throw new IllegalStateException(
                                "Should not reach this code, check logic where nthRing == 1");
                }

                // populate this.centroids
                this.centroids.add(neighborsCentroids.get(i));
            }
        }
    }

    /* Reset data */
    private void clearHexagonRings() {
        this.totalRings = 0;
        this.maxRings = 0;
        this.nthRing = 0;
    }

    private void clearDirectionalCentroids() {
        this.c1Centroids.clear();
        this.c2Centroids.clear();
        this.c3Centroids.clear();
        this.c4Centroids.clear();
        this.c5Centroids.clear();
        this.c6Centroids.clear();
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
         * edges)
         * in those 3 axes to cover the grid map largest diameter.
         */
        int maxAxialHexagons = (int) Math.ceil(maxBoundaryDistance / hexagonDistance);

        /*
         * We then arrive at 2 cases: odd vs even maxAxialHexagons
         * 
         * But to form a regular Hexagon Grids, the maxAxialHexagons must always be odd
         * in order to divide to an even amount of Hexagons on each side of the axis
         * -> because it has to subtract rootHexagon from the axis:
         * maxAxialHexagons = side_1_hexagons + rootHexagon + side_2_hexagons
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

        AxialClockwiseTessellation tessellation = new AxialClockwiseTessellation(hexagon);

        Boundary boundary = new Boundary(Arrays.asList(10.0, 10.0, 10.5, 10.5));
        int maxRings = tessellation.calculateMaxRings(boundary);

        // Test harversine
        double greatCircleDistance = Harversine.distance(boundary.getMinLatitude(), boundary.getMinLongitude(),
                boundary.getMaxLatitude(), boundary.getMaxLongitude());

        // System.out.println("Great-circle distance: " + greatCircleDistance);
        // System.out.println("Max hexagon rings: " + maxRings);
        // System.out.println("inradius: " + hexagon.getInradius());

        // tessellation.generateGisCentroids(boundary);
        // System.out.println(gson.toJson(tessellation));

        for (int i = 1; i <= 6; i++) {
            int direction = i;
            Coordinates neighborCentroid = Neighbors.generateCentroid(origin, hexagon.getInradius(), direction);
            System.out.println(neighborCentroid);
        }

    }
}