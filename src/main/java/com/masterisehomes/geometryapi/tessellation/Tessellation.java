package com.masterisehomes.geometryapi.tessellation;

import com.masterisehomes.geometryapi.hexagon.*;
import java.util.concurrent.*;
import java.util.List;
import java.util.ArrayList;


public class Tessellation {
    // Properties
    private final Hexagon originHexagon;
    private List<Hexagon> hexagons = new ArrayList<Hexagon>();
    private int totalHexagons = hexagons.size();
    private Coordinates origin;
    private Boundary boundary;
    private ConcurrentHashMap<Integer, Coordinates> centroidsD1, centroidsD2, centroidsD3, centroidsD4, centroidsD5,
            centroidsD6;

    // Constructors
    Tessellation(Coordinates origin, int circumradius, Boundary boundary) {
        this.originHexagon = new Hexagon(origin, circumradius);
        this.origin = originHexagon.getCentroid();
        this.boundary = boundary;
    }

    // Methods
    public String _generateDirectionalCentroids(int neighborDirection) {
        /*
         * neighborDirections - there are 6 of them.
         * 
         * Neighbors are ordered in a clock-wise rotation, this aims to achieve some
         * simple sense of direction for each root (original center) Hexagon to expand
         * upon required.
         * 1
         * 6/‾‾‾\2
         * 5\___/3
         * 4
         * 
         * This function uses inradius and coordinates of the originHexagon, to find all
         * centroids of hexagons that can be extended directly from origin centroid.
         */
        final double originX = origin.getX();
        final double originY = origin.getY();
        final double inradius = originHexagon.getInradius();
        final double SQRT_3 = Math.sqrt(3);
        Coordinates previousCentroid = origin;
        Coordinates nextCentroid;

        int centroidsCount = 0;
        int multiplier = 1;
        switch (neighborDirection) {
            case 1:
                centroidsD1 = new ConcurrentHashMap<>();
                while (previousCentroid.isIn(boundary)) {
                    centroidsCount++;
                    nextCentroid = new Coordinates(originX, originY + (2 * inradius * multiplier));
                    centroidsD1.put(centroidsCount, nextCentroid);
                    // Updater
                    previousCentroid = nextCentroid;
                    multiplier++;
                }
                break;

            case 2:
                centroidsD2 = new ConcurrentHashMap<>();
                while (previousCentroid.isIn(boundary)) {
                    centroidsCount++;
                    nextCentroid = new Coordinates(
                            originX + (SQRT_3 * inradius * multiplier), originY - (inradius * multiplier));
                    centroidsD2.put(centroidsCount, nextCentroid);
                    // Updater
                    previousCentroid = nextCentroid;
                    multiplier++;
                }
                break;

            case 3:
                centroidsD3 = new ConcurrentHashMap<>();
                while (previousCentroid.isIn(boundary)) {
                    centroidsCount++;
                    nextCentroid = new Coordinates(
                            originX + (SQRT_3 * inradius * multiplier), originY + (inradius * multiplier));
                    centroidsD3.put(centroidsCount, nextCentroid);
                    // Updater
                    previousCentroid = nextCentroid;
                    multiplier++;
                }
                break;

            case 4:
                centroidsD4 = new ConcurrentHashMap<>();
                while (previousCentroid.isIn(boundary)) {
                    centroidsCount++;
                    nextCentroid = new Coordinates(originX, originY + (2 * inradius * multiplier));
                    centroidsD4.put(centroidsCount, nextCentroid);
                    // Updater
                    previousCentroid = nextCentroid;
                    multiplier++;
                }
                break;

            case 5:
                centroidsD5 = new ConcurrentHashMap<>();
                while (previousCentroid.isIn(boundary)) {
                    centroidsCount++;
                    nextCentroid = new Coordinates(
                            originX - (SQRT_3 * inradius * multiplier), originY + (inradius * multiplier));
                    centroidsD5.put(centroidsCount, nextCentroid);
                    // Updater
                    previousCentroid = nextCentroid;
                    multiplier++;
                }
                break;

            case 6:
                centroidsD6 = new ConcurrentHashMap<>();
                while (previousCentroid.isIn(boundary)) {
                    centroidsCount++;
                    nextCentroid = new Coordinates(
                            originX - (SQRT_3 * inradius * multiplier), originY - (inradius * multiplier));
                    centroidsD6.put(centroidsCount, nextCentroid);
                    // Updater
                    previousCentroid = nextCentroid;
                    multiplier++;
                }
                break;

        }
        return String.format("Total centroids generated: %s", centroidsCount);
    }

    // Getters
    public int getTotalHexagons() {
        return this.totalHexagons;
    }

    public Coordinates getOrigin() {
        return this.origin;
    }

    public Boundary getBoundary() {
        return this.boundary;
    }

    // String representation
    public String toString() {
        return String.format(
                "Tessellation[%s, %s\n, %s, totalHexagons: %s]",
                this.originHexagon, this.origin,
                this.boundary, this.totalHexagons);
    }
}