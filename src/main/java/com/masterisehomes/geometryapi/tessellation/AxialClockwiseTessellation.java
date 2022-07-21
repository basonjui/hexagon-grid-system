package com.masterisehomes.geometryapi.tessellation;

import java.lang.Math;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
    private final Coordinates origin;
    @Getter
    private final double circumradius;
    @Getter
    private final double inradius;
    @Getter
    private final Hexagon rootHexagon;
    @Getter
    private Boundary boundary;

    // Directional centroids - generate centroids in hexagon's axial directions
    private List<Coordinates> d1Centroids = new ArrayList<Coordinates>(100);
    private List<Coordinates> d2Centroids = new ArrayList<Coordinates>(100);
    private List<Coordinates> d3Centroids = new ArrayList<Coordinates>(100);
    private List<Coordinates> d4Centroids = new ArrayList<Coordinates>(100);
    private List<Coordinates> d5Centroids = new ArrayList<Coordinates>(100);
    private List<Coordinates> d6Centroids = new ArrayList<Coordinates>(100);

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
    // The below updaters are used internally only
    private int maxRings = 0; // maximum layers of hexagons in a ring required to tessellate
    private int nthRing = 0; // the latest nth rings that tessellate generated

    public AxialClockwiseTessellation(Coordinates origin, double circumradius) {
        this.origin = origin;
        this.circumradius = circumradius;
        this.inradius = circumradius * Math.sqrt(3) / 2;
        this.rootHexagon = new Hexagon(origin, circumradius);
    }

    public AxialClockwiseTessellation(Hexagon rootHexagon) {
        this.rootHexagon = rootHexagon;
        this.origin = rootHexagon.getCentroid();
        this.circumradius = rootHexagon.getCircumradius();
        this.inradius = rootHexagon.getInradius();
    }

    public void tessellate(Boundary boundary) {
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

        // Loop tessellation logic until nthRing == maxRing

        // Check nthRing
        switch (this.nthRing) {
            case 0:
                //
                break;
        }
    }

    // Reset data
    private void clearHexagonRings() {
        this.totalRings = 0;
        this.maxRings = 0;
        this.nthRing = 0;
    }

    private void clearDirectionalCentroids() {
        this.d1Centroids.clear();
        this.d2Centroids.clear();
        this.d3Centroids.clear();
        this.d4Centroids.clear();
        this.d5Centroids.clear();
        this.d6Centroids.clear();
    }

    private void clearCentroids() {
        this.centroids.clear();
        this.gisCentroids.clear();
    }

    private void clearHexagons() {
        this.hexagons.clear();
        this.gisHexagons.clear();
    }

    // Calculations
    private int calculateMaxRings(Boundary boundary) {
        // Get boundary coordinates
        double minLat = boundary.getMinLatitude();
        double minLng = boundary.getMinLongitude();
        double maxLat = boundary.getMaxLatitude();
        double maxLng = boundary.getMaxLongitude();

        // Calculate the Great-circle Distance between the MIN and MAX coordinates
        double maxDistance = Harversine.distance(minLat, minLng, maxLat, maxLng);

        /*
         * Hexagon's height - distance between hexagon neighbors is:
         * = inradius * 2
         * 
         * Given maxDistance,
         * the maximum number of hexagons stack up in any Hexagonal direction is:
         * = maxDistance / inradius * 2
         * 
         * However, we need to use Math.ceil() to round it up to nearest int
         */
        double hexagonDistance = this.inradius * 2;
        int maximumRings = (int) Math.ceil(maxDistance / hexagonDistance);

        return maximumRings;
    }

    public static void main(String[] args) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        Coordinates origin = new Coordinates(10, 10);

        Hexagon hexagon = new Hexagon(origin, 5000);
        Neighbors neighbors = new Neighbors(hexagon);

        AxialClockwiseTessellation tessellation = new AxialClockwiseTessellation(hexagon);

        Double[] boundaryCoords = new Double[] { 10.0, 10.0, 10.5, 10.5 };
        Boundary boundary = new Boundary(Arrays.asList(boundaryCoords));
        int maxRings = tessellation.calculateMaxRings(boundary);

        // Test harversine
        double greatCircleDistance = Harversine.distance(boundary.getMinLatitude(), boundary.getMinLongitude(),
                boundary.getMaxLatitude(), boundary.getMaxLongitude());

        // System.out.println(neighbors.getGisCentroids());
        // System.out.println(gson.toJson(tessellation));
        // System.out.println(boundary);
        System.out.println("Great-circle distance: " + greatCircleDistance);
        System.out.println("Max hexagon rings: " + maxRings);
        System.out.println("inradius (meters): " + hexagon.getInradius());
    }
}