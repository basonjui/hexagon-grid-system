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
    private final List<Coordinates> boundaries;

    // Computations
    @Getter
    private final Hexagon rootHexagon;
    @Getter
    private final Neighbors neighbors;
    // TODO: provide a sense of direction for Tessellation
    private final Map<Integer, Coordinates> neighborsCentroids;
    private final Map<Integer, Coordinates> neighborsGisCentroids;

    // Storage
    /*
     * Notes: ArrayList needs to be assigned initialCapacity for better performance,
     * it cuts the cyle to expand the Array when it is full (default capacity = 10)
     */
    @Getter
    private List<Coordinates> centroids = new ArrayList<Coordinates>(100);
    @Getter
    private List<Coordinates> gisCentroids = new ArrayList<Coordinates>(100);
    @Getter
    private List<Hexagon> hexagons = new ArrayList<Hexagon>(100);
    @Getter
    private List<Hexagon> gisHexagons = new ArrayList<Hexagon>(100);

    // Updater
    @Getter
    private int rings = 0;
    @Getter
    private int nthRing = 0;

    public AxialClockwiseTessellation(Coordinates origin, double circumradius, List<Coordinates> boundaries) {
        this.boundaries = boundaries;
        this.origin = origin;
        this.circumradius = circumradius;

        this.rootHexagon = new Hexagon(this.origin, this.circumradius);
        this.neighbors = new Neighbors(rootHexagon);
        this.neighborsCentroids = this.neighbors.getCentroids();
        this.neighborsGisCentroids = this.neighbors.getGisCentroids();
    }

    public AxialClockwiseTessellation(Hexagon rootHexagon, List<Coordinates> boundaries) {
        this.rootHexagon = rootHexagon;
        this.boundaries = boundaries;

        this.origin = this.rootHexagon.getCentroid();
        this.circumradius = this.rootHexagon.getCircumradius();
        this.neighbors = new Neighbors(this.rootHexagon);
        this.neighborsCentroids = this.neighbors.getCentroids();
        this.neighborsGisCentroids = this.neighbors.getGisCentroids();
    }
}