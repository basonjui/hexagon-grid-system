package com.masterisehomes.geometryapi.hexagon;

import lombok.Getter;
import lombok.ToString;
import java.util.Map;

@ToString
public class HexagonDto {
    @Getter
    private double latitude, longitude, circumradius;
    @Getter
    private Coordinates centroid;
    @Getter
    private Hexagon hexagon;

    public HexagonDto(Map<String, Double> lambdaEvent) {
        // Parse lambdaEvent map to latitude, longitude, circumradius
        this.latitude = lambdaEvent.get("latitude");
        this.longitude = lambdaEvent.get("longitude");
        this.circumradius = lambdaEvent.get("radius");
        
        // Construct Coordinates and Hexagon objects
        this.centroid = new Coordinates(this.longitude, this.latitude);
        this.hexagon = new Hexagon(this.centroid, this.circumradius);
    }
}
