package com.masterisehomes.geometryapi.neighbors;

import com.masterisehomes.geometryapi.hexagon.Hexagon;
import com.masterisehomes.geometryapi.hexagon.Coordinates;
import java.util.Hashtable;

public class Neighbors {
    private Hashtable<Integer, Coordinates> centers;
    private Hexagon hexagon;
    
    public Neighbors(Hexagon hexagon) {
      this.hexagon = hexagon;
      this.centers = generatecenters();
    }
    
    public Hashtable<Integer, Coordinates> generatecenters() {
      final double SQRT_3 = Math.sqrt(3);
      double centerX = hexagon.getCenter().getLatitude();
      double centerY = hexagon.getCenter().getLongitude();
      double inradius = hexagon.getInradius();
      
      /* Neighbors are ordered in a clock-wise rotation, this aims to achieve some 
      simple sense of direction for each root (original center) Hexagon to expand upon required.
      
      Neighbor 1 starts at the Flat-top of the root Hexagon:
           1 
        6/‾‾‾\2
        5\___/3
           4
      
      --
      We calculate neighbor coordinates using their relationship to Hexagon center.
      There are 2 approaches: geometric vs trigonometric.
      
      We mostly used Trigonometry. */
      
      
      // Calculate and put neighbor centers into Hashtable
      centers = new Hashtable<Integer, Coordinates>();
      
      centers.put(
        1, new Coordinates(centerX, centerY - 2 * inradius)
      );
      centers.put(
        2, new Coordinates(centerX + SQRT_3 * inradius, centerY - inradius)
      );    
      centers.put(
        3, new Coordinates(centerX + SQRT_3 * inradius, centerY + inradius)
      );   
      centers.put(
        4, new Coordinates(centerX, centerY + 2 * inradius)
      );
      centers.put(
        5, new Coordinates(centerX - SQRT_3 * inradius, centerY + inradius)
      );
      centers.put(
        6, new Coordinates(centerX - SQRT_3 * inradius, centerY - inradius)
      );
      
      return centers;
    }
    
    // Getters
    public Hashtable<Integer, Coordinates> getcenters() {
      return this.centers;
    }
    
    public String toString() {
      return String.format("Neighbor[hexagoncenters: %s, neighbourcenters: %s]", this.hexagon.getCenter(), this.centers);
    } 
  }
