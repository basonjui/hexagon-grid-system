package com.masterisehomes.geometryapi.hexagon;

import java.util.List;
import java.util.ArrayList;
import java.lang.Math;
import lombok.Getter;
import lombok.ToString;
import com.masterisehomes.geometryapi.geodesy.SphericalMercatorProjection;

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
    this.inradius = circumradius * Math.sqrt(3)/2;
    this.generateVertices();
    this.generateGeoJsonVertices();
  }

  // Methods
  private void generateVertices() {
    double centroidX = this.centroid.getX();
    double centroidY = this.centroid.getY();

    /*
     * Generate Hexagon vertices with Flat-top orientation in clock-wise rotation:
     *    0   1
     *  5   .   2
     *    4   3
     */
    this.vertices.add(new Coordinates(centroidX - circumradius*1/2, centroidY - inradius));
    this.vertices.add(new Coordinates(centroidX + circumradius*1/2, centroidY - inradius));
    this.vertices.add(new Coordinates(centroidX + circumradius, centroidY));
    this.vertices.add(new Coordinates(centroidX + circumradius*1/2, centroidY + inradius));
    this.vertices.add(new Coordinates(centroidX - circumradius*1/2, centroidY + inradius));
    this.vertices.add(new Coordinates(centroidX - circumradius, centroidY));
  }

  private void generateGeoJsonVertices() {
    double centroidLong = this.centroid.getLongitude();
    double centroidLat = this.centroid.getLatitude();

    /* HOW CAN WE FIND THE CORRECT DEGREES TO BE TRANSLATED FOR VERTICES?
     * 
     *  circumradius is either in meters or in pixels, but longitude and 
     *  latitude are in degrees
     * 
     *  so we need to convert the displacement into degrees of lat & long
     *  (which long is dependent on lat).
    */

    double circumradiusInLongitude = SphericalMercatorProjection.xToLongitude(this.circumradius);
    double inradiusInLatitude = SphericalMercatorProjection.yToLatitude(this.inradius);

    // Use SphericalMetricConversion algorithm
    // double circumradiusInLongitude = SphericalMetricConversion.meterToLongitude(this.circumradius, latitude);
    // double inradiusInLatitude = SphericalMetricConversion.meterToLatitude(this.inradius);

    /*
     * GeoJSON specification
     *  The first and last positions are equivalent, and they MUST contain
     *  identical values; their representation SHOULD also be identical.
     */
    this.geoJsonPositions.add(new Coordinates(centroidLong - circumradiusInLongitude * 1/2, centroidLat - inradiusInLatitude));
    this.geoJsonPositions.add(new Coordinates(centroidLong + circumradiusInLongitude * 1/2, centroidLat - inradiusInLatitude));
    this.geoJsonPositions.add(new Coordinates(centroidLong + circumradiusInLongitude, centroidLat));
    this.geoJsonPositions.add(new Coordinates(centroidLong + circumradiusInLongitude * 1/2, centroidLat + inradiusInLatitude));
    this.geoJsonPositions.add(new Coordinates(centroidLong - circumradiusInLongitude * 1/2, centroidLat + inradiusInLatitude));
    this.geoJsonPositions.add(new Coordinates(centroidLong - circumradiusInLongitude, centroidLat));
    // Closing coordinate in GeoJSON, it is the first vertex, which is indexed 0
    this.geoJsonPositions.add(geoJsonPositions.get(0));
  }
}