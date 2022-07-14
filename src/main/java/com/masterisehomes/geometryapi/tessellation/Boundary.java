package com.masterisehomes.geometryapi.tessellation;

import java.util.List;

import lombok.Getter;
import lombok.ToString;

import com.masterisehomes.geometryapi.hexagon.Coordinates;

/* Similar to setup() in Processing
 * However, due to abstraction, the setup data is hard-coded and not stored, so we cannot
 * retrieve those data to use as a Coordinate system to setup our Hexagon Grid Map.
 * 
 * This class aims to serve as a formal management system for the boundary aspect:
 * - boundaries of canvas 
 * - boundaries of Processing shapes
 */

@ToString
@Getter
public class Boundary {
  // Processing attributes
  private int width, height;
  private Coordinates start, end;

  // WGS84 Coordinates attributes
  private double startLongitude, endLongitude;
  private double startLatitude, endLatitude;

  // Builder pattern to take in dimension ,
  public Boundary(float x, float y, int width, int height) {
    this.start = new Coordinates(x, y);
    this.width = width;
    this.height = height;
    this.end = new Coordinates(x + width, y + height);
  }

  // WGS84 Coordinates Boundary
  public Boundary(List<Double> boundary) {
    this.startLongitude = boundary.get(0);
    this.endLongitude = boundary.get(1);
    this.startLatitude = boundary.get(2);
    this.endLatitude = boundary.get(3);
  }
}