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
    private Coordinates originCenter;
    private Boundary boundary;
    private ConcurrentHashMap<Integer, Coordinates> centersD1, centersD2, centersD3, centersD4, centersD5,
            centersD6;

    // Constructors
    public Tessellation(Coordinates originCenter, int circumradius, Boundary boundary) {
        this.originHexagon = new Hexagon(originCenter, circumradius);
        this.originCenter = originHexagon.getCenter();
        this.boundary = boundary;
    }

    // Methods
    public String _generateDirectionalCenters(int neighborDirection) {
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
         * centers of hexagons that can be extended directly from originCenter center.
         */
        final double originX = originCenter.getLatitude();
        final double originY = originCenter.getLongitude();
        final double inradius = originHexagon.getInradius();
        final double SQRT_3 = Math.sqrt(3);
        Coordinates previousCenter = originCenter;
        Coordinates nextCenter;

        int centersCount = 0;
        int multiplier = 1;
        switch (neighborDirection) {
            case 1:
                centersD1 = new ConcurrentHashMap<>();
                while (previousCenter.isIn(boundary)) {
                    centersCount++;
                    nextCenter = new Coordinates(originX, originY + (2 * inradius * multiplier));
                    centersD1.put(centersCount, nextCenter);
                    // Updater
                    previousCenter = nextCenter;
                    multiplier++;
                }
                break;

            case 2:
                centersD2 = new ConcurrentHashMap<>();
                while (previousCenter.isIn(boundary)) {
                    centersCount++;
                    nextCenter = new Coordinates(
                            originX + (SQRT_3 * inradius * multiplier), originY - (inradius * multiplier));
                    centersD2.put(centersCount, nextCenter);
                    // Updater
                    previousCenter = nextCenter;
                    multiplier++;
                }
                break;

            case 3:
                centersD3 = new ConcurrentHashMap<>();
                while (previousCenter.isIn(boundary)) {
                    centersCount++;
                    nextCenter = new Coordinates(
                            originX + (SQRT_3 * inradius * multiplier), originY + (inradius * multiplier));
                    centersD3.put(centersCount, nextCenter);
                    // Updater
                    previousCenter = nextCenter;
                    multiplier++;
                }
                break;

            case 4:
                centersD4 = new ConcurrentHashMap<>();
                while (previousCenter.isIn(boundary)) {
                    centersCount++;
                    nextCenter = new Coordinates(originX, originY + (2 * inradius * multiplier));
                    centersD4.put(centersCount, nextCenter);
                    // Updater
                    previousCenter = nextCenter;
                    multiplier++;
                }
                break;

            case 5:
                centersD5 = new ConcurrentHashMap<>();
                while (previousCenter.isIn(boundary)) {
                    centersCount++;
                    nextCenter = new Coordinates(
                            originX - (SQRT_3 * inradius * multiplier), originY + (inradius * multiplier));
                    centersD5.put(centersCount, nextCenter);
                    // Updater
                    previousCenter = nextCenter;
                    multiplier++;
                }
                break;

            case 6:
                centersD6 = new ConcurrentHashMap<>();
                while (previousCenter.isIn(boundary)) {
                    centersCount++;
                    nextCenter = new Coordinates(
                            originX - (SQRT_3 * inradius * multiplier), originY - (inradius * multiplier));
                    centersD6.put(centersCount, nextCenter);
                    // Updater
                    previousCenter = nextCenter;
                    multiplier++;
                }
                break;

        }
        return String.format("Total centers generated: %s", centersCount);
    }

    // Getters
    public int getTotalHexagons() {
        return this.totalHexagons;
    }

    public Coordinates getOrigin() {
        return this.originCenter;
    }

    public Boundary getBoundary() {
        return this.boundary;
    }

    // String representation
    public String toString() {
        return String.format(
                "Tessellation[%s, %s\n, %s, totalHexagons: %s]",
                this.originHexagon, this.originCenter,
                this.boundary, this.totalHexagons);
    }
}
