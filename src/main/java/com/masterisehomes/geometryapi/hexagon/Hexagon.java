package com.masterisehomes.geometryapi.hexagon;

import java.util.List;
import java.util.ArrayList;
import java.lang.Math;

import lombok.Getter;
import lombok.ToString;

import com.masterisehomes.geometryapi.geodesy.SphericalMercatorProjection;
import com.masterisehomes.geometryapi.index.CubeCoordinatesIndex;
import com.masterisehomes.geometryapi.index.HexagonDirection;

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
  private final CubeCoordinatesIndex creatorCCI;
  private final CubeCoordinatesIndex CCI;


  public Hexagon(Coordinates centroid, double circumradius) {
    this.centroid = centroid;
    this.circumradius = circumradius;
    this.inradius = circumradius * Math.sqrt(3)/2;

    this.vertices = generateVertices(centroid);
    this.gisVertices = generateGisVertices(centroid);

    
    this.direction = HexagonDirection.NONE;
    this.creatorCCI = null;
    this.CCI = new CubeCoordinatesIndex(creatorCCI, direction);
  }

  public Hexagon(Coordinates centroid, double circumradius, CubeCoordinatesIndex creatorCCI, HexagonDirection direction) {
    this.centroid = centroid;
    this.circumradius = circumradius;
    this.inradius = circumradius * Math.sqrt(3)/2;

    this.vertices = generateVertices(centroid);
    this.gisVertices = generateGisVertices(centroid);

    
    this.direction = direction;
    this.creatorCCI = creatorCCI;
    this.CCI = new CubeCoordinatesIndex(creatorCCI, direction);
  }

  // Methods
  private List<Coordinates> generateVertices(Coordinates centroid) {
    double centroidX = centroid.getX();
    double centroidY = centroid.getY();

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
    double centroidLong = centroid.getLongitude();
    double centroidLat = centroid.getLatitude();

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
    gisCoordinatesList.add(new Coordinates(centroidLong - circumradiusLong * 1/2, centroidLat - inradiusLat));
    gisCoordinatesList.add(new Coordinates(centroidLong + circumradiusLong * 1/2, centroidLat - inradiusLat));
    gisCoordinatesList.add(new Coordinates(centroidLong + circumradiusLong, centroidLat));
    gisCoordinatesList.add(new Coordinates(centroidLong + circumradiusLong * 1/2, centroidLat + inradiusLat));
    gisCoordinatesList.add(new Coordinates(centroidLong - circumradiusLong * 1/2, centroidLat + inradiusLat));
    gisCoordinatesList.add(new Coordinates(centroidLong - circumradiusLong, centroidLat));
    // Closing coordinate in GeoJSON, it is the first vertex, which is indexed 0
    gisCoordinatesList.add(gisCoordinatesList.get(0));

    return gisCoordinatesList;
  }

  // Getters
  public String getCCIString() {
    return String.format("Hexagon%s=(direction=%s, creatorCCI=%s, CCI=%s)", this.getDirection(), this.direction, this.creatorCCI, this.CCI);
  }

  public static void main(String[] args) {
    Coordinates centroid = new Coordinates(100, 100);
    Hexagon hex0 = new Hexagon(centroid, 5000);
    Hexagon hex1 = new Hexagon(centroid, hex0.getCircumradius(), hex0.getCCI(), HexagonDirection.ONE);
    // TODO: Maybe try to implement a Hexagon constructor as the following: new Hexagon(Hexagon previousHexagon, HexagonDirection direction)

    System.out.println(hex0.getCCIString());
    System.out.println(hex1.getCCIString());
  }

}