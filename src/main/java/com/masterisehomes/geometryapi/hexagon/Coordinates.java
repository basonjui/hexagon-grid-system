package com.masterisehomes.geometryapi.hexagon;

import lombok.Getter;
import lombok.ToString;
import java.util.Arrays;
import java.util.List;

/*
So why the Vertex class instead of using the predefined Point class?
Point is a really old class in Java, it can only work with Integer and Double.

This is a problem because the vertex() function in processing can
only work with Float data type.

Do we want to cast Double to Float for every point to draw on Processing?
So here we are.
*/

@Getter
@ToString
public class Coordinates {
  private double latitude;
  private double longitude;

  // Constructors
  public Coordinates(double latitude, double longitude) {
    this.latitude = latitude;
    this.longitude = longitude;
  }

  // Comparison methods
  public boolean isEqual(Coordinates coord) {
    double latitude = coord.getLatitude();
    double longitude = coord.getLongitude();

    if (this.latitude == latitude && this.longitude == longitude) {
      return true;
    } else {
      return false;
    }
  }

  public boolean isLarger(Coordinates coord) {
    double latitude = coord.getLatitude();
    double longitude = coord.getLongitude();

    if (this.latitude > latitude && this.longitude > longitude) {
      return true;
    } else {
      return false;
    }
  }

  public boolean isSmaller(Coordinates coord) {
    double latitude = coord.getLatitude();
    double longitude = coord.getLongitude();

    if (this.latitude < latitude && this.longitude < longitude) {
      return true;
    } else {
      return false;
    }
  }

  public boolean isIn(Boundary b) {
    Coordinates bStart = b.getStart();
    Coordinates bEnd = b.getEnd();

    if (this.isLarger(bStart) && this.isSmaller(bEnd)) {
      return true;
    } else {
      return false;
    }
  }

  // GeoJSON stuff

  /*
   * Position
   * 
   * A position is an array of coordinates in order:
   * this is the smallest unit that we can really consider ‘a place’ since it can
   * represent a point on earth.
   * 
   * GeoJSON describes an order for coordinates: they should go, in order:
   * [longitude, latitude, elevation]
   */
  public List<Double> toGeoJsonPosition() {
    /*
     * Positions (geojson.org)
     * 
     * The order of elements must follow x, y, z order
     *    (easting, northing, altitude for coordinates in a projected coordinate
     * reference system,
     *    or longitude, latitude, altitude for coordinates in a geographic coordinate
     * reference system).
     */
    List<Double> coordinates = Arrays.asList(this.longitude, this.latitude);
    return coordinates;
  }
}