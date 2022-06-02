package com.masterisehomes.geometryapi.hexagon;

import lombok.Getter;
import lombok.ToString;

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
}