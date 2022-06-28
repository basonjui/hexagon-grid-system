package com.masterisehomes.geometryapi.neighbors;

import com.masterisehomes.geometryapi.hexagon.Hexagon;
import com.masterisehomes.geometryapi.hexagon.Coordinates;
import com.masterisehomes.geometryapi.geodesy.SphericalMercatorProjection;
import lombok.Getter;
import lombok.ToString;
import java.util.HashMap;

@ToString
public class Neighbors {
  @Getter
  private Hexagon rootHexagon;
  @Getter
  private HashMap<Integer, Coordinates> centroids;
  @Getter
  private HashMap<Integer, Coordinates> gisCentroids;
  @Getter
  private HashMap<Integer, Hexagon> hexagons;
  @Getter
  private HashMap<Integer, Hexagon> gisHexagons;

  public Neighbors(Hexagon rootHexagon) {
    this.rootHexagon = rootHexagon;
    this.centroids = generateCentroids(rootHexagon);
    this.hexagons = generateHexagons(this.centroids);
    this.gisCentroids = generateGisCentroids(rootHexagon);
    this.gisHexagons = generateGisHexagons(this.gisCentroids);
  }

  private HashMap<Integer, Coordinates> generateCentroids(Hexagon rootHexagon) {
    final double SQRT_3 = Math.sqrt(3);
    final double centroidX = rootHexagon.getCentroid().getX();
    final double centroidY = rootHexagon.getCentroid().getY();
    final double inradius = rootHexagon.getInradius();

    /*
     * Neighbors are ordered in a clock-wise rotation, this aims to achieve some
     * simple sense of direction for each root (center centroid) Hexagon to expand
     * upon required.
     * 
     * Neighbor 1 starts at the Flat-top of the root Hexagon:
     *    1
     * 6/‾‾‾\2  0
     * 5\___/3
     *    4
     * 
     * *Update:
     *  - Neighbors will now include the centroids of rootHexagon
     *    which is at key 0 in centroids hashmap.
     * 
     * We calculate neighbor coordinates using their relationship to Hexagon centroid.
     * There are 2 approaches: geometric vs trigonometric.
     * 
     * We mostly used Trigonometry.
     */

    // Calculate neighbor centroids and put them into HashMap
    HashMap<Integer, Coordinates> centroids = new HashMap<Integer, Coordinates>();

    centroids.put(0, rootHexagon.getCentroid());

    centroids.put(1, new Coordinates(
      centroidX, 
      centroidY - 2 * inradius
    ));

    centroids.put(2, new Coordinates(
      centroidX + SQRT_3 * inradius, 
      centroidY - inradius
    ));

    centroids.put(3, new Coordinates(
      centroidX + SQRT_3 * inradius, 
      centroidY + inradius
    ));

    centroids.put(4, new Coordinates(
      centroidX, 
      centroidY + 2 * inradius
    ));

    centroids.put(5, new Coordinates(
      centroidX - SQRT_3 * inradius, 
      centroidY + inradius
    ));

    centroids.put(6, new Coordinates(
      centroidX - SQRT_3 * inradius, 
      centroidY - inradius
    ));

    return centroids;
  }

  
  private HashMap<Integer, Coordinates> generateGisCentroids(Hexagon rootHexagon) {
    final double SQRT_3 = Math.sqrt(3);
    final double centroidLong = rootHexagon.getCentroid().getLongitude();
    final double centroidLat = rootHexagon.getCentroid().getLatitude();
    final double inradius = rootHexagon.getInradius();
    // Convert inradius (which is currently in Meter unit) to Degrees unit
    final double inradiusLong = SphericalMercatorProjection.xToLongitude(inradius);  // x
    final double inradiusLat = SphericalMercatorProjection.yToLatitude(inradius);    // y

    HashMap<Integer, Coordinates> gisCentroids = new HashMap<Integer, Coordinates>();

    gisCentroids.put(0, rootHexagon.getCentroid());
    
    gisCentroids.put(1, new Coordinates(
      centroidLong, 
      centroidLat - 2 * inradiusLat
    ));

    gisCentroids.put(2, new Coordinates(
      centroidLong + SQRT_3 * inradiusLong, 
      centroidLat - inradiusLat
    ));

    gisCentroids.put(3, new Coordinates(
      centroidLong + SQRT_3 * inradiusLong, 
      centroidLat + inradiusLat
    ));

    gisCentroids.put(4, new Coordinates(
      centroidLong, 
      centroidLat + 2 * inradiusLat
    ));

    gisCentroids.put(5, new Coordinates(
      centroidLong - SQRT_3 * inradiusLong, 
      centroidLat + inradiusLat
    ));

    gisCentroids.put(6, new Coordinates(
      centroidLong - SQRT_3 * inradiusLong, 
      centroidLat - inradiusLat
    ));

    return gisCentroids;
  }

  private HashMap<Integer, Hexagon> generateHexagons(HashMap<Integer, Coordinates> centroids) {
    HashMap<Integer, Hexagon> hexagons = new HashMap<Integer, Hexagon>();

    centroids.forEach((key, centroid) -> {
      hexagons.put(key, new Hexagon(centroid, rootHexagon.getCircumradius()));
    });

    return hexagons;
  }

  private HashMap<Integer, Hexagon> generateGisHexagons(HashMap<Integer, Coordinates> gisCentroids) {
    HashMap<Integer, Hexagon> gisHexagons = new HashMap<Integer, Hexagon>();

    gisCentroids.forEach((key, gisCentroid) -> {
      gisHexagons.put(key, new Hexagon(gisCentroid, rootHexagon.getCircumradius()));
    });

    return gisHexagons;
  }
}
