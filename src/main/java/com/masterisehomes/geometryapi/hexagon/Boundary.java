package com.masterisehomes.geometryapi.hexagon;

import lombok.Getter;
import lombok.ToString;

/* Similar to setup() in Processing
However, due to abstraction, the setup data is hard-coded and not stored, so we cannot
retrieve those data to use as a Coordinate system to setup our Hexagon Grid Map.

This class aims to serve as a formal management system for the boundary aspect:
- boundaries of canvas
- boundaries of Processing shapes
*/

@Getter
@ToString
public class Boundary {
    private final int width, height;
    private Coordinates start, end;
    
    // Builder pattern to take in dimension , 
    public Boundary(float x, float y, int width, int height) {
      this.start = new Coordinates(x, y);
      this.width = width;
      this.height = height;
      this.end = new Coordinates(x+width, y+height);
    }
  }
