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
  // geoJsonPositions = vertices in GeoJSON format
  private List<Coordinates> geoJsonPositions = new ArrayList<Coordinates>();

  public Hexagon(Coordinates centroid, double circumradius) {
    this.centroid = centroid;
    this.circumradius = circumradius;
    this.inradius = circumradius * (Math.sqrt(3)/2);
    this.generateVertices();
    this.generateGeoJsonVertices();
  }

  // Methods
  private void generateVertices() {
    double centroidX = this.centroid.getLatitude();
    double centroidY = this.centroid.getLongitude();

    /*
     * Generate Hexagon vertices with Flat-top orientation in clock-wise rotation:
     *    0   1
     *  5   .   2
     *    4   3
     */
    this.vertices.add(new Coordinates(centroidX - circumradius * 1/2, centroidY - inradius));
    this.vertices.add(new Coordinates(centroidX + circumradius * 1/2, centroidY - inradius));
    this.vertices.add(new Coordinates(centroidX + circumradius, centroidY));
    this.vertices.add(new Coordinates(centroidX + circumradius * 1/2, centroidY + inradius));
    this.vertices.add(new Coordinates(centroidX - circumradius * 1/2, centroidY + inradius));
    this.vertices.add(new Coordinates(centroidX - circumradius, centroidY));
  }

  private void generateGeoJsonVertices() {
    double centroidX = this.centroid.getLatitude();
    double centroidY = this.centroid.getLongitude();

    /*
     * GeoJSON specification
     *  The first and last positions are equivalent, and they MUST contain
     *  identical values; their representation SHOULD also be identical.
     */

    /* 
     * Keep in mind that for generateGeoJsonVertices, both the X and Y displacements 
     * are in geographic degrees.
     * 
     * And because the Earth is not a Sphere, but an Ellipse, so WGS84 models the Earth
     * as an Ellipsoid.
     * 
     * Also Latitude and Longitude are not straight cartesian axes, they are curved. 
     * So in order to calculate the distance for each degree of Latitude & Longitude,
     * we need to use Spherical Trigonometry to calculate the Arcs distances.
     * 
     * Radius x Radian = Length of Arc
     * Given the MAJOR & MINOR axes of the Earth, perhaps we can calculate something?
     * 
     * Problem statement:
     *    Input unit (from OSM) = Projected degrees (long, lat)
     *    Calculation unit (Java) = pixels (1:1)
     *    Output unit (to OSM) = Projected degrees (long, lat)
     *    ---
     * 
     * Long, Lat have special distance ratios per degree:
     *    Lat can be estimated with average
     *    But Long can varies greatly depends on the Lat degree. 
     * 
     * ---
     * 
     * HOW CAN WE FIND THE CORRECT DEGREES TO BE TRANSLATED FOR VERTICES?
     *    
     * 
    */ 
    this.geoJsonPositions.add(new Coordinates(centroidX - circumradius * 1/2 / 3, centroidY - inradius));
    this.geoJsonPositions.add(new Coordinates(centroidX + circumradius * 1/2 / 3, centroidY - inradius));
    this.geoJsonPositions.add(new Coordinates(centroidX + circumradius / 3, centroidY));
    this.geoJsonPositions.add(new Coordinates(centroidX + circumradius * 1/2 / 3, centroidY + inradius));
    this.geoJsonPositions.add(new Coordinates(centroidX - circumradius * 1/2 / 3, centroidY + inradius));
    this.geoJsonPositions.add(new Coordinates(centroidX - circumradius / 3, centroidY));
    // Closing coordinate in GeoJSON, it is the first vertex, which is indexed 0
    this.geoJsonPositions.add(geoJsonPositions.get(0));
  }
}