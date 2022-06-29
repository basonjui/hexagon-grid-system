package com.masterisehomes.geometryapi.neighbors;

import lombok.Getter;
import lombok.ToString;
import com.google.gson.JsonObject;
import com.masterisehomes.geometryapi.hexagon.*;
@ToString
public class NeighborsDto {
    @Getter
    private double latitude, longitude, circumradius;
    @Getter
    private Coordinates centroid;
    @Getter
    private Hexagon hexagon;
    @Getter
    private Neighbors neighbors;

    public NeighborsDto(JsonObject payload) {
        this.latitude = payload.get("latitude").getAsDouble();
        this.longitude = payload.get("longitude").getAsDouble();
        this.circumradius = payload.get("radius").getAsDouble();

        this.centroid = new Coordinates(this.longitude, this.latitude);
        this.hexagon = new Hexagon(this.centroid, this.circumradius);
        this.neighbors = new Neighbors(hexagon);
    }
}
