package com.masterisehomes.geometryapi.tessellation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.ToString;

import com.masterisehomes.geometryapi.hexagon.Coordinates;
import com.masterisehomes.geometryapi.hexagon.Hexagon;
import com.masterisehomes.geometryapi.neighbors.Neighbors;

@ToString
public class AxialClockwiseTessellation {
    // Inputs
    @Getter
    private final Coordinates origin;
    @Getter
    private final double circumradius;
    @Getter
    private Boundary boundary;

    // Computations
    @Getter
    private final Hexagon rootHexagon;
    @Getter
    private final Neighbors neighbors;
    // The axial origin of our Tessellation algorithm, Neighbors is nthRing: 0
    private final Map<Integer, Coordinates> neighborsCentroids;
    private final Map<Integer, Coordinates> neighborsGisCentroids;

    // Storage
    /*
     * Notes: ArrayList needs to be assigned initialCapacity for better performance,
     * it cuts the cyle to expand the Array when it is full (default initialCapacity = 10)
     */
    @Getter
    private List<Coordinates> tessellationCentroids = new ArrayList<Coordinates>(100);
    @Getter
    private List<Coordinates> tessellationGisCentroids = new ArrayList<Coordinates>(100);
    @Getter
    private List<Hexagon> tessellationHexagons = new ArrayList<Hexagon>(100);
    @Getter
    private List<Hexagon> tessellationGisHexagons = new ArrayList<Hexagon>(100);

    // Updater
    @Getter
    private int rings = tessellationCentroids.size();
    @Getter
    private int nthRing = 0;

    public AxialClockwiseTessellation(Coordinates origin, double circumradius) {
        this.origin = origin;
        this.circumradius = circumradius;

        this.rootHexagon = new Hexagon(this.origin, this.circumradius);
        this.neighbors = new Neighbors(this.rootHexagon);
        this.neighborsCentroids = this.neighbors.getCentroids();
        this.neighborsGisCentroids = this.neighbors.getGisCentroids();
    }

    public AxialClockwiseTessellation(Hexagon rootHexagon) {
        this.rootHexagon = rootHexagon;

        this.origin = this.rootHexagon.getCentroid();
        this.circumradius = this.rootHexagon.getCircumradius();
        this.neighbors = new Neighbors(this.rootHexagon);
        this.neighborsCentroids = this.neighbors.getCentroids();
        this.neighborsGisCentroids = this.neighbors.getGisCentroids();
    }

    public void tessellate(Boundary boundary) {
        // Set boundary
        this.boundary = boundary;

        // Check nthRing
        switch(this.nthRing) {
            case 0:
            // 
            break;
        }
    }

    public static void main(String[] args) {
        Coordinates origin = new Coordinates(10, 10);
        Hexagon hexagon = new Hexagon(origin, 5000);
        AxialClockwiseTessellation tessellation = new AxialClockwiseTessellation(hexagon);
        
        Neighbors neighbors = new Neighbors(hexagon);
        System.out.println(neighbors.getGisCentroids());
    }
}