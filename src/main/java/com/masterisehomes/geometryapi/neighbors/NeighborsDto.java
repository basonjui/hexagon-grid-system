package com.masterisehomes.geometryapi.neighbors;

import lombok.Getter;
import lombok.ToString;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.masterisehomes.geometryapi.hexagon.*;
import com.masterisehomes.geometryapi.geojson.GeoJsonManager;

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

    private GeoJsonManager geojsonManager;
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public NeighborsDto(JsonObject payload) {
        this.latitude = payload.get("latitude").getAsDouble();
        this.longitude = payload.get("longitude").getAsDouble();
        this.circumradius = payload.get("radius").getAsDouble();

        this.centroid = new Coordinates(this.longitude, this.latitude);
        this.hexagon = new Hexagon(this.centroid, this.circumradius);
        this.neighbors = new Neighbors(hexagon);
        
        this.geojsonManager = new GeoJsonManager(this.neighbors);
    }

    public String getGeoJson() {
        return gson.toJson(this.geojsonManager.getFeatureCollection());
    }
}
