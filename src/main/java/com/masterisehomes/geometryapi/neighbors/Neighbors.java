package com.masterisehomes.geometryapi.neighbors;

import java.security.InvalidParameterException;
import java.util.LinkedHashMap;
import java.util.Map;

import com.masterisehomes.geometryapi.hexagon.Hexagon;
import com.masterisehomes.geometryapi.index.HexagonDirection;
import com.masterisehomes.geometryapi.hexagon.Coordinates;
import com.masterisehomes.geometryapi.geodesy.SphericalMercatorProjection;

import lombok.Getter;
import lombok.ToString;

@ToString
public class Neighbors {
    @Getter
    private Hexagon rootHexagon;
    @Getter
    private Map<Integer, Coordinates> centroids;
    @Getter
    private Map<Integer, Coordinates> gisCentroids;
    @Getter
    private Map<Integer, Hexagon> hexagons;
    @Getter
    private Map<Integer, Hexagon> gisHexagons;

    /* Constructor */
    public Neighbors(Hexagon rootHexagon) {
        this.rootHexagon = rootHexagon;

        this.centroids = generateCentroids(rootHexagon);
        this.hexagons = generateHexagons(this.centroids);

        this.gisCentroids = generateGisCentroids(rootHexagon);
        this.gisHexagons = generateGisHexagons(this.gisCentroids);
    }

    /* Internal methods */
    private Map<Integer, Coordinates> generateCentroids(Hexagon rootHexagon) {
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
         * 6/‾‾‾\2
         * 5\___/3
         *    4
         * 
         * *Update:
         * - Neighbors will now include the centroids of rootHexagon
         * which is at key 0 in centroids hashmap.
         * 
         * We calculate neighbor coordinates using their relationship to Hexagon
         * centroid.
         * There are 2 approaches: geometric vs trigonometric.
         * 
         * We mostly used Trigonometry.
         */

        // Calculate neighbor centroids and put them into a Map
        Map<Integer, Coordinates> centroids = new LinkedHashMap<Integer, Coordinates>();

        centroids.put(0, rootHexagon.getCentroid());

        centroids.put(1, new Coordinates(
                centroidX,
                centroidY - 2 * inradius));

        centroids.put(2, new Coordinates(
                centroidX + SQRT_3 * inradius,
                centroidY - inradius));

        centroids.put(3, new Coordinates(
                centroidX + SQRT_3 * inradius,
                centroidY + inradius));

        centroids.put(4, new Coordinates(
                centroidX,
                centroidY + 2 * inradius));

        centroids.put(5, new Coordinates(
                centroidX - SQRT_3 * inradius,
                centroidY + inradius));

        centroids.put(6, new Coordinates(
                centroidX - SQRT_3 * inradius,
                centroidY - inradius));

        return centroids;
    }

    private Map<Integer, Coordinates> generateGisCentroids(Hexagon rootHexagon) {
        final double SQRT_3 = Math.sqrt(3);
        final double centroidLng = rootHexagon.getCentroid().getLongitude();
        final double centroidLat = rootHexagon.getCentroid().getLatitude();
        final double inradius = rootHexagon.getInradius();

        // Convert inradius (which is currently in Meter unit) to Degrees unit
        final double inradiusLng = SphericalMercatorProjection.xToLongitude(inradius); // x
        final double inradiusLat = SphericalMercatorProjection.yToLatitude(inradius); // y

        Map<Integer, Coordinates> gisCentroids = new LinkedHashMap<Integer, Coordinates>();

        gisCentroids.put(0, rootHexagon.getCentroid());

        gisCentroids.put(1, new Coordinates(
                centroidLng,
                centroidLat - 2 * inradiusLat));

        gisCentroids.put(2, new Coordinates(
                centroidLng + SQRT_3 * inradiusLng,
                centroidLat - inradiusLat));

        gisCentroids.put(3, new Coordinates(
                centroidLng + SQRT_3 * inradiusLng,
                centroidLat + inradiusLat));

        gisCentroids.put(4, new Coordinates(
                centroidLng,
                centroidLat + 2 * inradiusLat));

        gisCentroids.put(5, new Coordinates(
                centroidLng - SQRT_3 * inradiusLng,
                centroidLat + inradiusLat));

        gisCentroids.put(6, new Coordinates(
                centroidLng - SQRT_3 * inradiusLng,
                centroidLat - inradiusLat));

        return gisCentroids;
    }

    private Map<Integer, Hexagon> generateHexagons(Map<Integer, Coordinates> centroids) {
        Map<Integer, Hexagon> hexagons = new LinkedHashMap<Integer, Hexagon>();

        centroids.forEach((key, centroid) -> {
            /*
             * We use switch - case statement on key of gisCentroids Map to determine
             * HexagonDirection
             */
            switch (key) {
                case 0:
                    hexagons.put(key,
                            new Hexagon(centroid, this.rootHexagon,
                                    HexagonDirection.ZERO));
                    break;

                case 1:
                    hexagons.put(key,
                            new Hexagon(centroid, this.rootHexagon,
                                    HexagonDirection.ONE));
                    break;

                case 2:
                    hexagons.put(key,
                            new Hexagon(centroid, this.rootHexagon,
                                    HexagonDirection.TWO));
                    break;

                case 3:
                    hexagons.put(key,
                            new Hexagon(centroid, this.rootHexagon,
                                    HexagonDirection.THREE));
                    break;

                case 4:
                    hexagons.put(key,
                            new Hexagon(centroid, this.rootHexagon,
                                    HexagonDirection.FOUR));
                    break;

                case 5:
                    hexagons.put(key,
                            new Hexagon(centroid, this.rootHexagon,
                                    HexagonDirection.FIVE));
                    break;

                case 6:
                    hexagons.put(key,
                            new Hexagon(centroid, this.rootHexagon,
                                    HexagonDirection.SIX));
                    break;
            }
        });

        return hexagons;
    }

    private Map<Integer, Hexagon> generateGisHexagons(Map<Integer, Coordinates> gisCentroids) {
        Map<Integer, Hexagon> gisHexagons = new LinkedHashMap<Integer, Hexagon>();

        gisCentroids.forEach((key, gisCentroid) -> {
            // We use switch - case statement on key of gisCentroids Map to determine
            // HexagonDirection
            switch (key) {
                case 0:
                    gisHexagons.put(key,
                            new Hexagon(gisCentroid, this.rootHexagon,
                                    HexagonDirection.ZERO));
                    break;

                case 1:
                    gisHexagons.put(key,
                            new Hexagon(gisCentroid, this.rootHexagon,
                                    HexagonDirection.ONE));
                    break;

                case 2:
                    gisHexagons.put(key,
                            new Hexagon(gisCentroid, this.rootHexagon,
                                    HexagonDirection.TWO));
                    break;

                case 3:
                    gisHexagons.put(key,
                            new Hexagon(gisCentroid, this.rootHexagon,
                                    HexagonDirection.THREE));
                    break;

                case 4:
                    gisHexagons.put(key,
                            new Hexagon(gisCentroid, this.rootHexagon,
                                    HexagonDirection.FOUR));
                    break;

                case 5:
                    gisHexagons.put(key,
                            new Hexagon(gisCentroid, this.rootHexagon,
                                    HexagonDirection.FIVE));
                    break;

                case 6:
                    gisHexagons.put(key,
                            new Hexagon(gisCentroid, this.rootHexagon,
                                    HexagonDirection.SIX));
                    break;
            }
        });

        return gisHexagons;
    }

    /* Public methods */
    public static Coordinates generateCentroid(Coordinates rootCentroid, double rootInradius, int direction) {
        final double SQRT_3 = Math.sqrt(3);
        final double rootCentroidX = rootCentroid.getX();
        final double rootCentroidY = rootCentroid.getY();

        switch (direction) {
            case 1:
                return new Coordinates(
                    rootCentroidX, 
                    rootCentroidY - 2 * rootInradius);

            case 2:
                return new Coordinates(
                    rootCentroidX + SQRT_3 * rootInradius, 
                    rootCentroidY - 2 * rootInradius);

            case 3:
                return new Coordinates(
                    rootCentroidX + SQRT_3 * rootInradius,
                    rootCentroidY + rootInradius);

            case 4:
                return new Coordinates(
                    rootCentroidX,
                    rootCentroidY + 2 * rootInradius);

            case 5:
                return new Coordinates(
                    rootCentroidX - SQRT_3 * rootInradius,
                    rootCentroidY + rootInradius);

            case 6:
                return new Coordinates(
                    rootCentroidX - SQRT_3 * rootInradius,
                    rootCentroidY - rootInradius);

            default:
                throw new InvalidParameterException("Invalid Hexagonal direction: " + direction);
        }
    };

    public static Coordinates generateGisCentroid(Coordinates rootGisCentroid, double rootInradius, int direction) {
        final double SQRT_3 = Math.sqrt(3);
        final double gisCentroidLng = rootGisCentroid.getLongitude();
        final double gisCentroidLat = rootGisCentroid.getLatitude();

        // Convert inradius (which is currently in Meter unit) to Degrees unit
        final double inradiusLng = SphericalMercatorProjection.xToLongitude(rootInradius);
        final double inradiusLat = SphericalMercatorProjection.yToLatitude(rootInradius);

        switch (direction) {
            case 1:
                return new Coordinates(
                    gisCentroidLng, 
                    gisCentroidLat - 2 * inradiusLat);

            case 2:
                return new Coordinates(
                    gisCentroidLng + SQRT_3 * inradiusLng, 
                    gisCentroidLat - 2 * inradiusLat);

            case 3:
                return new Coordinates(
                    gisCentroidLng + SQRT_3 * inradiusLng,
                    gisCentroidLat + inradiusLat);

            case 4:
                return new Coordinates(
                    gisCentroidLng,
                    gisCentroidLat + 2 * inradiusLat);

            case 5:
                return new Coordinates(
                    gisCentroidLng - SQRT_3 * inradiusLng,
                    gisCentroidLat + inradiusLat);

            case 6:
                return new Coordinates(
                    gisCentroidLng - SQRT_3 * inradiusLng,
                    gisCentroidLat - inradiusLat);
                    
            default:
                throw new InvalidParameterException("Invalid Hexagonal direction: " + direction);
        }
    };
}