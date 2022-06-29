package com.masterisehomes.geometryapi.hexagon;

import lombok.Getter;
import lombok.ToString;
import com.google.gson.JsonObject;

@ToString
public class HexagonDto {
    @Getter
    private double latitude, longitude, circumradius;
    @Getter
    private Coordinates centroid;
    @Getter
    private Hexagon hexagon;

    public HexagonDto(JsonObject payload) {
        this.latitude = payload.get("latitude").getAsDouble();
        this.longitude = payload.get("longitude").getAsDouble();
        this.circumradius = payload.get("radius").getAsDouble();

        this.centroid = new Coordinates(this.longitude, this.latitude);
        this.hexagon = new Hexagon(this.centroid, this.circumradius);
    }
}
