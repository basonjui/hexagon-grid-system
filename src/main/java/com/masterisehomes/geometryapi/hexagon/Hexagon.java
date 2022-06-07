package com.masterisehomes.geometryapi.hexagon;

import java.util.List;
import java.util.ArrayList;
import java.lang.Math;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class Hexagon {
  private Coordinates centroid;
  private double circumradius;
  private double inradius;
  private List<Coordinates> vertices = new ArrayList<Coordinates>();
  private List<Coordinates> geoJsonVertices = new ArrayList<Coordinates>();

  public Hexagon(Coordinates centroid, float circumradius) {
    this.centroid = centroid;
    this.circumradius = circumradius;
    this.inradius = circumradius * Math.sqrt(3) / 2;
    this.vertices = generateVertices();
    this.geoJsonVertices = generateGeoJsonVertices();
  }

  // Methods
  private List<Coordinates> generateVertices() {
    final double SQRT_3 = Math.sqrt(3);
    double centroidX = this.centroid.getLatitude();
    double centroidY = this.centroid.getLongitude();

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

  private List<Coordinates> generateGeoJsonVertices() {
    final double SQRT_3 = Math.sqrt(3);
    double centroidX = this.centroid.getLatitude();
    double centroidY = this.centroid.getLongitude();

    /* 
      GeoJSON specification
      - The first and last positions are equivalent, and they MUST contain
      identical values; their representation SHOULD also be identical.
     */
    vertices.add(new Coordinates(centroidX - circumradius / 2, centroidY - circumradius * SQRT_3 / 2));
    vertices.add(new Coordinates(centroidX + circumradius / 2, centroidY - circumradius * SQRT_3 / 2));
    vertices.add(new Coordinates(centroidX + circumradius, centroidY));
    vertices.add(new Coordinates(centroidX + circumradius / 2, centroidY + circumradius * SQRT_3 / 2));
    vertices.add(new Coordinates(centroidX - circumradius / 2, centroidY + circumradius * SQRT_3 / 2));
    vertices.add(new Coordinates(centroidX - circumradius, centroidY));
    vertices.add(new Coordinates(centroidX - circumradius / 2, centroidY - circumradius * SQRT_3 / 2));

    return vertices;
  }

  
}