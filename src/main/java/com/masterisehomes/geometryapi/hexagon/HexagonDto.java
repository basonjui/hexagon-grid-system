package com.masterisehomes.geometryapi.hexagon;

import java.io.Serializable;
import java.util.List;

public class HexagonDto {
    private final String type = "Polygon";
    private Coordinates centroid;
    private List<Coordinates> vertices;

    public HexagonDto() {}

    // Setters
    public void setCentroid(Coordinates centroid) {
        this.centroid = centroid;
    }

    public void setVertices(List<Coordinates> vertices) {
        this.vertices = vertices;
    }

    // Getters 
    public String getType() {
        return this.type;
    }

    public Coordinates getCentroid() {
        return this.centroid;
    }

    public List<Coordinates> getVertices() {
        return this.vertices;
    }

    public String toString() {
        return "type: " + type + ", centroid: " + centroid + ", vertices: " + vertices;
    }
}
