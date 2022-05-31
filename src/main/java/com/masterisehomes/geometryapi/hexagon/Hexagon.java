package com.masterisehomes.geometryapi.hexagon;

import java.util.Hashtable;
import java.lang.Math;

public class Hexagon {
  protected Coordinates centroid;
  protected double circumradius;
  protected double inradius;
  protected Hashtable<Integer, Coordinates> vertices;
  
  // Store 6 neighbors (object)
  //protected Hashtable<Integer, Coordinates> neighborCentroids; 
  
  // CONSTANTS
  protected final double SQRT_3 = Math.sqrt(3);

  public Hexagon(Coordinates centroid, float circumradius) {
    this.centroid = centroid;
    this.circumradius = circumradius;
    this.inradius = circumradius * SQRT_3 / 2;
    this.vertices = generateVertices(centroid);
    //this.neighborCentroids = generateNeighborCentroids(centroid);
  }
  
  
  // Methods
  private Hashtable<Integer, Coordinates> generateVertices(Coordinates centroid) {
    Hashtable<Integer, Coordinates> vertices = new Hashtable<Integer, Coordinates>();
    double centroidX = centroid.getX();
    double centroidY = centroid.getY();
    
    /* Generate Hexagon vertices with Flat-top orientation in clock-wise rotation:
         1   2
       6   .   3
         5   4       
    */
    vertices.put(
      1, new Coordinates(centroidX - circumradius/2, centroidY - circumradius * SQRT_3 / 2)
    );    
    vertices.put(
      2, new Coordinates(centroidX + circumradius/2, centroidY - circumradius * SQRT_3 / 2)
    );    
    vertices.put(
      3, new Coordinates(centroidX + circumradius, centroidY)
    );    
    vertices.put(
      4, new Coordinates(centroidX + circumradius/2, centroidY + circumradius * SQRT_3 / 2)
    );    
    vertices.put(
      5, new Coordinates(centroidX - circumradius/2, centroidY + circumradius * SQRT_3 / 2)
    );    
    vertices.put(
      6, new Coordinates(centroidX - circumradius, centroidY)
    ); 
    
    return vertices;
  }
  
  
  // Getters
  public Coordinates getCentroid() {
    return this.centroid;
  }

  public double getCircumradius() {
    return this.circumradius;
  }

  public double getInradius() {
    return this.inradius;
  }
  
  public Hashtable<Integer, Coordinates> getVertices() {
    return this.vertices;
  }
  
  //Hashtable<Integer, Coordinates> getNeighborCentroids() {
  //  return this.neighborCentroids; 
  //}
  
  public String toString() {
    return String.format("Hexagon[centroid: %s, circumradius: %s]", this.centroid, this.circumradius);
  } 
}