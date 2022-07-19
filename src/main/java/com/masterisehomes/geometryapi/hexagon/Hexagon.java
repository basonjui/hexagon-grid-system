package com.masterisehomes.geometryapi.hexagon;

import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;
import java.lang.Math;
import java.util.Map;

import lombok.Getter;
import lombok.ToString;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import com.masterisehomes.geometryapi.geodesy.SphericalMercatorProjection;
import com.masterisehomes.geometryapi.geojson.GeoJsonManager;
import com.masterisehomes.geometryapi.index.CubeCoordinatesIndex;
import com.masterisehomes.geometryapi.index.HexagonDirection;
import com.masterisehomes.geometryapi.neighbors.Neighbors;

@ToString
@Getter
public class Hexagon {
  private Coordinates centroid;
  private double circumradius;
  private double inradius;

  private List<Coordinates> vertices;
  private List<Coordinates> gisVertices;

  // Cube Coordinates Indexing
  private final HexagonDirection direction;
  private final CubeCoordinatesIndex previousCCI;
  private final CubeCoordinatesIndex CCI;


  public Hexagon(Coordinates centroid, double circumradius) {
    this.centroid = centroid;
    this.circumradius = circumradius;
    this.inradius = circumradius * Math.sqrt(3)/2;

    this.vertices = generateVertices(centroid);
    this.gisVertices = generateGisVertices(centroid);
    
    this.direction = HexagonDirection.ZERO;
    this.previousCCI = null;
    this.CCI = new CubeCoordinatesIndex(this.previousCCI, this.direction);
  }

  public Hexagon(Coordinates centroid, Hexagon rootHexagon, HexagonDirection direction) {
    this.centroid = centroid;
    this.circumradius = rootHexagon.getCircumradius();
    this.inradius = this.circumradius * Math.sqrt(3)/2;

    this.vertices = generateVertices(centroid);
    this.gisVertices = generateGisVertices(centroid);

    this.direction = direction;
    if (direction == HexagonDirection.ZERO) {
      this.previousCCI = null;
    } else {
      this.previousCCI = rootHexagon.getCCI();
    }
    this.CCI = new CubeCoordinatesIndex(this.previousCCI, direction);
  }

  // Methods
  private List<Coordinates> generateVertices(Coordinates centroid) {
    final double centroidX = centroid.getX();
    final double centroidY = centroid.getY();

    /*
     * Generate Hexagon vertices with Flat-top orientation in clock-wise rotation:
     *    0   1
     *  5   .   2
     *    4   3
     */
    List<Coordinates> coordinatesList = new ArrayList<Coordinates>();

    coordinatesList.add(new Coordinates(centroidX - circumradius * 1/2, centroidY - inradius));
    coordinatesList.add(new Coordinates(centroidX + circumradius * 1/2, centroidY - inradius));
    coordinatesList.add(new Coordinates(centroidX + circumradius, centroidY));
    coordinatesList.add(new Coordinates(centroidX + circumradius * 1/2, centroidY + inradius));
    coordinatesList.add(new Coordinates(centroidX - circumradius * 1/2, centroidY + inradius));
    coordinatesList.add(new Coordinates(centroidX - circumradius, centroidY));

    return coordinatesList;
  }

  private List<Coordinates> generateGisVertices(Coordinates centroid) {
    final double centroidLng = centroid.getLongitude();
    final double centroidLat = centroid.getLatitude();

    /* 
     *  circumradius is either in meters or in pixels, but longitude and 
     *  latitude are in degrees
     * 
     *  so we need to convert the displacement into degrees of latitude & longitude
     *  (where meters/longitude is dependent on current latitude).
    */
    double circumradiusLong = SphericalMercatorProjection.xToLongitude(this.circumradius);
    double inradiusLat = SphericalMercatorProjection.yToLatitude(this.inradius);

    /* Use SphericalMetricConversion algorithm
    double circumradiusInLongitude = SphericalMetricConversion.meterToLongitude(this.circumradius, latitude);
    double inradiusInLatitude = SphericalMetricConversion.meterToLatitude(this.inradius); */
    
    List<Coordinates> gisCoordinatesList = new ArrayList<Coordinates>();
    /* GeoJSON specification:
     * - The first and last positions are equivalent, and they MUST contain
     * identical values; their representation SHOULD also be identical.
     */
    gisCoordinatesList.add(new Coordinates(centroidLng - circumradiusLong * 1/2, centroidLat - inradiusLat));
    gisCoordinatesList.add(new Coordinates(centroidLng + circumradiusLong * 1/2, centroidLat - inradiusLat));
    gisCoordinatesList.add(new Coordinates(centroidLng + circumradiusLong, centroidLat));
    gisCoordinatesList.add(new Coordinates(centroidLng + circumradiusLong * 1/2, centroidLat + inradiusLat));
    gisCoordinatesList.add(new Coordinates(centroidLng - circumradiusLong * 1/2, centroidLat + inradiusLat));
    gisCoordinatesList.add(new Coordinates(centroidLng - circumradiusLong, centroidLat));
    // Closing coordinate in GeoJSON, it is the first vertex, which is indexed 0
    gisCoordinatesList.add(gisCoordinatesList.get(0));

    return gisCoordinatesList;
  }

  // Getters
  public String getIndex() {
    return String.format("Hexagon%s=(direction=%s, previousCCI=%s, CCI=%s)", this.getDirection(), this.direction, this.previousCCI, this.CCI);
  }

  public static void main(String[] args) {
    Coordinates centroid = new Coordinates(10, 11);
    Hexagon hex0 = new Hexagon(centroid, 5000);
    Neighbors neighbors = new Neighbors(hex0);

    // Generate indexes from Hexagon class
    Hexagon hex1 = new Hexagon(neighbors.getGisCentroids().get(1), hex0, HexagonDirection.ONE);
    Hexagon hex2 = new Hexagon(neighbors.getGisCentroids().get(2), hex0, HexagonDirection.TWO);
    Hexagon hex3 = new Hexagon(neighbors.getGisCentroids().get(3), hex0, HexagonDirection.THREE);
    Hexagon hex4 = new Hexagon(neighbors.getGisCentroids().get(5), hex0, HexagonDirection.FOUR);
    Hexagon hex5 = new Hexagon(neighbors.getGisCentroids().get(5), hex0, HexagonDirection.FIVE);
    Hexagon hex6 = new Hexagon(neighbors.getGisCentroids().get(6), hex0, HexagonDirection.SIX);

    List<Object> hexList = Arrays.asList(
      hex0, 
      hex1, 
      hex2, 
      hex3, 
      hex4, 
      hex5,
      hex6
    );

    // Generate indexes from Neighbors
    GeoJsonManager manager = new GeoJsonManager(neighbors);

    Gson gson = new Gson();

    System.out.println(gson.toJson(manager.getFeatureCollection()));
  }

}