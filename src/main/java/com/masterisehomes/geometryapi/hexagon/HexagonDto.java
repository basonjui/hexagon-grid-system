package com.masterisehomes.geometryapi.hexagon;

import lombok.Getter;
import lombok.ToString;
import java.util.Map;
import java.lang.Double;

@ToString
public class HexagonDto {
    @Getter
    private double latitude, longitude, circumradius;
    @Getter
    private Coordinates centroid;
    @Getter
    private Hexagon hexagon;

    public HexagonDto(Map<String, String> lambdaEvent) {
        // Parse lambdaEvent map to latitude, longitude, circumradius
        this.latitude = Double.parseDouble(lambdaEvent.get("latitude"));
        this.longitude = Double.parseDouble(lambdaEvent.get("longitude"));
        this.circumradius = Double.parseDouble(lambdaEvent.get("radius"));
        
        // Construct Coordinates and Hexagon objects
        this.centroid = new Coordinates(this.longitude, this.latitude);
        this.hexagon = new Hexagon(this.centroid, this.circumradius);
    }
}
