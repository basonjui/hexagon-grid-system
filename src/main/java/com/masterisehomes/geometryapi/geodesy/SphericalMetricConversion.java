package com.masterisehomes.geometryapi.geodesy;

import java.lang.Math;

public class SphericalMetricConversion {
    private static final double metersPerLatitude = 111111.0;

    public static double meterToLatitude(double meter) {
        double latitudeDegree = meter / metersPerLatitude;
        return latitudeDegree;
    }
 
    public static double meterToLongitude(double meter, double latitude) {
        double longitudeDegree = meter / metersPerLatitude * Math.cos(Math.toRadians(latitude));
        return longitudeDegree;
    }
}