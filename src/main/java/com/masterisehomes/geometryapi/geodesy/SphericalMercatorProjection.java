package com.masterisehomes.geometryapi.geodesy;

import java.lang.Math;

/* Algorithm by OpenStreetMap (OSM) - Mercator:
 * - https://wiki.openstreetmap.org/wiki/Mercator
 */
public class SphericalMercatorProjection {
    public static final double RADIUS = 6378137.0; /* in meters on the equator */

    /*
     * These functions take their length parameter in meters and return an angle in
     * degrees
     */
    public static double yToLatitude(double y) {
        return Math.toDegrees(Math.atan(Math.exp(y / RADIUS)) * 2 - Math.PI / 2);
    }

    public static double xToLongitude(double x) {
        return Math.toDegrees(x / RADIUS);
    }

    /*
     * These functions take their angle parameter in degrees and return a length in
     * meters
     */
    public static double latitudeToX(double latitude) {
        return Math.log(Math.tan(Math.PI / 4 + Math.toRadians(latitude) / 2)) * RADIUS;
    }

    public static double longitudeToY(double longitude) {
        return Math.toRadians(longitude) * RADIUS;
    }
}
