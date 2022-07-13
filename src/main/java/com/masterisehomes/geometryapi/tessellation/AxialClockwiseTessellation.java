package com.masterisehomes.geometryapi.tessellation;

import java.util.List;

import lombok.Getter;

import com.masterisehomes.geometryapi.hexagon.Coordinates;
import com.masterisehomes.geometryapi.hexagon.Hexagon;
import com.masterisehomes.geometryapi.neighbors.Neighbors;

public class AxialClockwiseTessellation {
    @Getter
    private Coordinates origin;
    @Getter
    private double circumradius;
    @Getter
    private List<Coordinates> boundaries;
    @Getter
    private Hexagon rootHexagon;
    @Getter
    private Neighbors neighbors;

    public AxialClockwiseTessellation(Coordinates origin, double circumradius, List<Coordinates> boundaries) {
        this.boundaries = boundaries;
        this.rootHexagon = new Hexagon(origin, circumradius);
        this.neighbors = new Neighbors(rootHexagon);
    }

    public AxialClockwiseTessellation(Hexagon rootHexagon, List<Coordinates> boundaries) {
        this.rootHexagon = rootHexagon;
        this.boundaries = boundaries;
    }
}