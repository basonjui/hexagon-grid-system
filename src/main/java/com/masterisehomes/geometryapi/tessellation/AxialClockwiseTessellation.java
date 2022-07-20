package com.masterisehomes.geometryapi.tessellation;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.ToString;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

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
    private final Hexagon rootHexagon;
    @Getter
    private Boundary boundary;

    // Computational data
    private List<Coordinates> d1Centroids = new ArrayList<>(100);
    private List<Coordinates> d2Centroids = new ArrayList<>(100);
    private List<Coordinates> d3Centroids = new ArrayList<>(100);
    private List<Coordinates> d4Centroids = new ArrayList<>(100);
    private List<Coordinates> d5Centroids = new ArrayList<>(100);
    private List<Coordinates> d6Centroids = new ArrayList<>(100);

    /*
     * Output data
     *
     * Notes: ArrayList needs to be assigned initialCapacity for better performance,
     * it cuts the cycle to expand the Array when it is full (default initialCapacity
     * = 10)
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

    // Flags
    private boolean tessellated = false;

    public AxialClockwiseTessellation(Coordinates origin, double circumradius) {
        this.origin = origin;
        this.circumradius = circumradius;
        this.rootHexagon = new Hexagon(this.origin, this.circumradius);
    }

    public AxialClockwiseTessellation(Hexagon rootHexagon) {
        this.rootHexagon = rootHexagon;
        this.origin = rootHexagon.getCentroid();
        this.circumradius = rootHexagon.getCircumradius();
    }

    // this method is re-runnable and clear the ArrayList each run
    public void tessellate(Boundary boundary) {
            // Set boundarythis method should only run once
            this.boundary = boundary;

            // Check nthRing
            switch (this.nthRing) {
                case 0:
                    //
                    break;
            }
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