package com.masterisehomes.geometryapi.geodesy;

import java.lang.Math;

public class Harversine {
    /*
     * Earth's radius at the equator (WGS-84)
     * https://en.wikipedia.org/wiki/World_Geodetic_System#Definition
     */
    public static final double R = 6378137.0; // meters

    /*
     * Calculate the Great-Circle Distance between two coordinates - which is the
     * shortest distance over Earth's surface (fly distance)
     */
    public static double distance(double latitude_1, double longitude_1, double latitude_2, double longitude_2) {
        // Convert latitudes to Radians
        double phi_1 = Math.toRadians(latitude_1);
        double phi_2 = Math.toRadians(latitude_2);

        // Distance between latitudes and longitudes
        double delta_phi = Math.toRadians(latitude_2 - latitude_1);
        double delta_lambda = Math.toRadians(longitude_2 - longitude_1);

        // Apply Harversine formula
        double a = Math.pow(Math.sin(delta_phi / 2), 2)
                + Math.pow(Math.sin(delta_lambda / 2), 2) * Math.cos(phi_1) * Math.cos(phi_2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));

        double distance = R * c; // meters

        return distance;
    }
}
