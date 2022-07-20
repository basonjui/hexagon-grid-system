package com.masterisehomes.geometryapi.tessellation;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.ToString;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.masterisehomes.geometryapi.geodesy.SphericalMercatorProjection;
import com.masterisehomes.geometryapi.hexagon.Coordinates;
import com.masterisehomes.geometryapi.hexagon.Hexagon;
import com.masterisehomes.geometryapi.neighbors.Neighbors;

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

    // Updaters
    @Getter
    private int totalRings;
    @Getter
    private int nthRing;

    public AxialClockwiseTessellation(Coordinates origin, double circumradius) {
        this.origin = origin;
        this.circumradius = circumradius;
        this.inradius = circumradius * Math.sqrt(3)/2;
        this.rootHexagon = new Hexagon(this.origin, this.circumradius);
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
         *   1. takes in a new Boundary as parameter
         *   2. clears all the generated centroids & hexagons ArrayList
         *   3. reset updaters (totalRings, nthRing)
         *   3. populate new centroids & hexagons with new Boundary
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
        this.resetUpdaters();

        // Check nthRing
        switch (this.nthRing) {
            case 0:
                //
                break;
        }
    }

    // Reset data
    private void resetUpdaters() {
        this.totalRings = 0;
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
    private int calculateMaxNthRing(Boundary boundary) {
        int maxNthRing = 0;

        // distance between centroids == 2 * inradius

        // 1. we convert inradius to respective Latitude & Longitude first

        // 2. then, calculate the Max Longitude & Latitude displacements

        // 3. perform operations: 
        // - maxLngDisplacement % inradiusLng = maxNthRingLng
        // - maxLatDisplacement % inradiusLat = maxNthRingLat

        // 4. compare maxNthRingLng & maxNthRingLat

        // 5. maxNthRing = the larger ring above
        double inradiusLng = SphericalMercatorProjection.xToLongitude(this.inradius);
        double inradiusLat = SphericalMercatorProjection.yToLatitude(this.inradius);



        return maxNthRing;
    }

    public static void main(String[] args) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        Coordinates origin = new Coordinates(10, 10);
        Hexagon hexagon = new Hexagon(origin, 5000);
        AxialClockwiseTessellation tessellation = new AxialClockwiseTessellation(hexagon);

        Neighbors neighbors = new Neighbors(hexagon);
        System.out.println(neighbors.getGisCentroids());
        System.out.println(gson.toJson(tessellation));
    }
}