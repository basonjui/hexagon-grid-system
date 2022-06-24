package com.masterisehomes.geometryapi.neighbors;

import com.masterisehomes.geometryapi.hexagon.Hexagon;
import com.masterisehomes.geometryapi.hexagon.Coordinates;
import com.masterisehomes.geometryapi.geodesy.SphericalMercatorProjection;
import lombok.Getter;
import lombok.ToString;
import java.util.HashMap;
import java.util.List;

@ToString
public class Neighbors {
  @Getter private Hexagon hexagon;
  @Getter private HashMap<Integer, Coordinates> centroids;
  @Getter private HashMap<Integer, Coordinates> gisCentroids;
  @Getter private List<Hexagon> hexagons;

  public Neighbors(Hexagon hexagon) {
    this.hexagon = hexagon;
    this.centroids = generateCentroids(hexagon);
    this.gisCentroids = generateGisCentroids(hexagon);
  }

  private HashMap<Integer, Coordinates> generateCentroids(Hexagon hexagon) {
    final double SQRT_3 = Math.sqrt(3);
    final double centroidX = hexagon.getCentroid().getX();
    final double inradius = hexagon.getInradius();
    final double centroidY = hexagon.getCentroid().getY();

    /*
     * Neighbors are ordered in a clock-wise rotation, this aims to achieve some
     * simple sense of direction for each root (original centroid) Hexagon to expand
     * upon required.
     * 
     * Neighbor 1 starts at the Flat-top of the root Hexagon:
     *    1
     * 6/‾‾‾\2
     * 5\___/3
     *    4
     * 
     * We calculate neighbor coordinates using their relationship to Hexagon centroid.
     * There are 2 approaches: geometric vs trigonometric.
     * 
     * We mostly used Trigonometry.
     */

    // Calculate neighbor centroids and put them into HashMap
    HashMap<Integer, Coordinates> hmap = new HashMap<Integer, Coordinates>();

    hmap.put(1, new Coordinates(
      centroidX, 
      centroidY - 2 * inradius
    ));

    hmap.put(2, new Coordinates(
      centroidX + SQRT_3 * inradius, 
      centroidY - inradius
    ));

    hmap.put(3, new Coordinates(
      centroidX + SQRT_3 * inradius, 
      centroidY + inradius
    ));

    hmap.put(4, new Coordinates(
      centroidX, 
      centroidY + 2 * inradius
    ));

    hmap.put(5, new Coordinates(
      centroidX - SQRT_3 * inradius, 
      centroidY + inradius
    ));

    hmap.put(6, new Coordinates(
      centroidX - SQRT_3 * inradius, 
      centroidY - inradius
    ));

    return hmap;
  }

  
  private HashMap<Integer, Coordinates> generateGisCentroids(Hexagon hexagon) {
    final double SQRT_3 = Math.sqrt(3);
    final double centroidLong = hexagon.getCentroid().getLongitude();
    final double centroidLat = hexagon.getCentroid().getLatitude();
    final double inradius = hexagon.getInradius();
    // Convert inradius (which is currently in Meter unit) to Degrees unit
    final double inradiusLong = SphericalMercatorProjection.xToLongitude(inradius);  // x
    final double inradiusLat = SphericalMercatorProjection.yToLatitude(inradius);    // y

    HashMap<Integer, Coordinates> hmap = new HashMap<Integer, Coordinates>();

    hmap.put(1, new Coordinates(
      centroidLong, 
      centroidLat - 2 * inradiusLat
    ));

    hmap.put(2, new Coordinates(
      centroidLong + SQRT_3 * inradiusLong, 
      centroidLat - inradiusLat
    ));

    hmap.put(3, new Coordinates(
      centroidLong + SQRT_3 * inradiusLong, 
      centroidLat + inradiusLat
    ));

    hmap.put(4, new Coordinates(
      centroidLong, 
      centroidLat + 2 * inradiusLat
    ));

    hmap.put(5, new Coordinates(
      centroidLong - SQRT_3 * inradiusLong, 
      centroidLat + inradiusLat
    ));

    hmap.put(6, new Coordinates(
      centroidLong - SQRT_3 * inradiusLong, 
      centroidLat - inradiusLat
    ));

    return hmap;
  }
}
