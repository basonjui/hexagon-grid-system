package com.masterisehomes.geometryapi;

import static spark.Spark.*;

import com.google.gson.*;
import com.masterisehomes.geometryapi.hexagon.*;
import com.masterisehomes.geometryapi.neighbors.Neighbors;

public class GeometryApi {
    public final static Gson gson = new GsonBuilder().setPrettyPrinting().create();
    // public final static Gson gson = new Gson();

    public static void main(String[] args) {
        post("/api/hexagon", "application/json", (req, res) -> {
            try {
                // Parse request payload to a JSONObject with Gson
                JsonObject jsonObj = gson.fromJson(req.body(), JsonObject.class);
                // Get GIS data from payload with keys
                double latitude = jsonObj.get("latitude").getAsDouble();
                double longitude = jsonObj.get("longitude").getAsDouble();
                double circumradius = jsonObj.get("radius").getAsDouble();

                // Initialize a hexagon with request's data
                Coordinates centroid = new Coordinates(longitude, latitude);
                Hexagon hexagon = new Hexagon(centroid, circumradius);

                // Get data from DTO
                HexagonDto dto = new HexagonDto(hexagon);

                return dto.build().toGeoJSON();

            } catch (Exception e) {
                return "Invalid JSON data provided: " + e;
            }
        });

        post("/api/neighbors", "application/json", (req, res) -> {
            try {
                // Parse request payload to a JSONObject with Gson
                JsonObject jsonObj = gson.fromJson(req.body(), JsonObject.class);
                // Get GIS data from payload with keys
                double latitude = jsonObj.get("latitude").getAsDouble();
                double longitude = jsonObj.get("longitude").getAsDouble();
                double circumradius = jsonObj.get("radius").getAsDouble();

                // Initialize a hexagon with request's data
                Coordinates centroid = new Coordinates(longitude, latitude);
                Hexagon hexagon = new Hexagon(centroid, circumradius);
                Neighbors neighbors = new Neighbors(hexagon);

                // Get data from DTO
                HexagonDto dto = new HexagonDto(hexagon);

                return dto.build().toGeoJSON();

            } catch (Exception e) {
                return "Invalid JSON data provided: " + e;
            }
        });
    }
}
