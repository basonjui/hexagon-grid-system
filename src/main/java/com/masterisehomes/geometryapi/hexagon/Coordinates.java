package com.masterisehomes.geometryapi.hexagon;

 /*
 So why the Vertex class instead of using the predefined Point class?

 Point is a really old class in Java,
 it can only work with Integer and Double.

 This is a problem because the vertex() function in processing can
 only work with Float data type.
 
 Do we want to cast Double to Float for every point to draw on Processing?
 So here we are.
 */

public class Coordinates {
    private double x;
    private double y;
    
    // Constructors
    Coordinates(double x, double y) {
      this.x = x;
      this.y = y;
    }
    
    // Comparison methods
    public boolean isEqual(Coordinates coord) {
      double x = coord.getX();
      double y = coord.getY();
      
      if (this.x == x && this.y == y) {
        return true;
      } else {
      return false;
      }
    }
    
    public boolean isLarger(Coordinates coord) {
      double x = coord.getX();
      double y = coord.getY();
      
      if (this.x > x && this.y > y) {
        return true;
      } else {
        return false;
      }
    }
    
    public boolean isSmaller(Coordinates coord) {
      double x = coord.getX();
      double y = coord.getY();
      
      if (this.x < x && this.y < y) {
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
    
    // Getters
    public double getX() {
      return this.x;
    }
  
    public double getY() {
      return this.y;
    }
    
    // String representation
    public String toString() {
      return String.format("Coordinates[x=%s, y=%s]", this.x, this.y);
    }
  }