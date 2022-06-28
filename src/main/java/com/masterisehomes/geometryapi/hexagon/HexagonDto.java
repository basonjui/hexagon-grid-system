package com.masterisehomes.geometryapi.hexagon;

import lombok.Getter;
import lombok.ToString;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.masterisehomes.geometryapi.geojson.GeoJsonManager;

@ToString
public class HexagonDto {
    @Getter
    private double latitude, longitude, circumradius;
    @Getter
    private Coordinates centroid;
    @Getter
    private Hexagon hexagon;

    private GeoJsonManager geojsonManager;
    public final static Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public HexagonDto(JsonObject payload) {
        this.latitude = payload.get("latitude").getAsDouble();
        this.longitude = payload.get("longitude").getAsDouble();
        this.circumradius = payload.get("radius").getAsDouble();

        this.centroid = new Coordinates(this.longitude, this.latitude);
        this.hexagon = new Hexagon(this.centroid, this.circumradius);

        this.geojsonManager = new GeoJsonManager(this.hexagon);
    }

    public String getGeoJson() {
        return gson.toJson(this.geojsonManager.getFeatureCollection());
    }
}
