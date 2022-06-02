package com.masterisehomes.geometryapi.hexagon;

import java.util.List;
import java.util.ArrayList;
import java.lang.Math;

public class Hexagon {
  private Coordinates centroid;
  private double circumradius;
  private double inradius;
  private List<Coordinates> vertices = new ArrayList<Coordinates>();

  public Hexagon(Coordinates centroid, float circumradius) {
    this.centroid = centroid;
    this.circumradius = circumradius;
    this.inradius = circumradius * Math.sqrt(3) / 2;
    this.vertices = generateVertices(centroid);
  }

  // Methods
  private List<Coordinates> generateVertices(Coordinates centroid) {
    final double SQRT_3 = Math.sqrt(3);
    double centroidX = centroid.getLatitude();
    double centroidY = centroid.getLongitude();

    /*
     * Generate Hexagon vertices with Flat-top orientation in clock-wise rotation:
     *    0   1
     *  5   .   2
     *    4   3
     */
    vertices.add(new Coordinates(centroidX - circumradius / 2, centroidY - circumradius * SQRT_3 / 2));
    vertices.add(new Coordinates(centroidX + circumradius / 2, centroidY - circumradius * SQRT_3 / 2));
    vertices.add(new Coordinates(centroidX + circumradius, centroidY));
    vertices.add(new Coordinates(centroidX + circumradius / 2, centroidY + circumradius * SQRT_3 / 2));
    vertices.add(new Coordinates(centroidX - circumradius / 2, centroidY + circumradius * SQRT_3 / 2));
    vertices.add(new Coordinates(centroidX - circumradius, centroidY));

    return vertices;
  }

  // Getters
  public Coordinates getCentroid() {
    return this.centroid;
  }

  public double getCircumradius() {
    return this.circumradius;
  }

  public double getInradius() {
    return this.inradius;
  }

  public List<Coordinates> getVertices() {
    return this.vertices;
  }

  public String toString() {
    return String.format("Hexagon[centroid: %s, circumradius: %s, vertices: %s]", 
      this.centroid, this.circumradius, 
      this.vertices
    );
  }
}