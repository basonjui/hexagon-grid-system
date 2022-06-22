package com.masterisehomes.geometryapi.neighbors;

import com.masterisehomes.geometryapi.hexagon.Hexagon;

import lombok.ToString;

import com.masterisehomes.geometryapi.hexagon.Coordinates;
import java.util.Hashtable;

@ToString
public class Neighbors {
  private Hashtable<Integer, Coordinates> centroids;
  private Hexagon hexagon;

  public Neighbors(Hexagon hexagon) {
    this.hexagon = hexagon;
    this.centroids = generateCentroids();
  }

  public Hashtable<Integer, Coordinates> generateCentroids() {
    final double SQRT_3 = Math.sqrt(3);
    double centerX = hexagon.getCentroid().getX();
    double centerY = hexagon.getCentroid().getY();
    double inradius = hexagon.getInradius();

    /*
     * Neighbors are ordered in a clock-wise rotation, this aims to achieve some
     * simple sense of direction for each root (original center) Hexagon to expand
     * upon required.
     * 
     * Neighbor 1 starts at the Flat-top of the root Hexagon:
     *    1
     * 6/‾‾‾\2
     * 5\___/3
     *    4
     * 
     * We calculate neighbor coordinates using their relationship to Hexagon center.
     * There are 2 approaches: geometric vs trigonometric.
     * 
     * We mostly used Trigonometry.
     */

    // Calculate and put neighbor centroids into Hashtable
    centroids = new Hashtable<Integer, Coordinates>();

    centroids.put(
        1, new Coordinates(centerX, centerY - 2 * inradius));
    centroids.put(
        2, new Coordinates(centerX + SQRT_3 * inradius, centerY - inradius));
    centroids.put(
        3, new Coordinates(centerX + SQRT_3 * inradius, centerY + inradius));
    centroids.put(
        4, new Coordinates(centerX, centerY + 2 * inradius));
    centroids.put(
        5, new Coordinates(centerX - SQRT_3 * inradius, centerY + inradius));
    centroids.put(
        6, new Coordinates(centerX - SQRT_3 * inradius, centerY - inradius));

    return centroids;
  }

  // Getters
  public Hashtable<Integer, Coordinates> getCentroids() {
    return this.centroids;
  }
}
