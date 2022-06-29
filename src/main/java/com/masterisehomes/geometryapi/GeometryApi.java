package com.masterisehomes.geometryapi;

import static spark.Spark.*;

import com.google.gson.*;
import com.masterisehomes.geometryapi.geojson.GeoJsonManager;
import com.masterisehomes.geometryapi.hexagon.*;
import com.masterisehomes.geometryapi.neighbors.*;

public class GeometryApi {
    public final static Gson gson = new GsonBuilder().setPrettyPrinting().create();
    // public final static Gson gson = new Gson();

    public static void main(String[] args) {
        post("/api/hexagon", "application/json", (req, res) -> {
            try {
                // Parse request payload to a JSONObject with Gson
                JsonObject payload = gson.fromJson(req.body(), JsonObject.class);

                // Initialize a HexagonDto with payload to get all required data
                HexagonDto dto = new HexagonDto(payload);
                // From dto, you can get lat, long, centroid, radius, hexagon
                
                // GeoJsonManager handles all GeoJSON operations
                GeoJsonManager manager = new GeoJsonManager(dto.getHexagon());

                // Convert FeatureCollection object to JSON
                String response = gson.toJson(manager.getFeatureCollection());

                // Return JSON in GeoJSON format
                return response;
            } 
            
            catch (Exception e) {
                return "Invalid JSON data provided: " + e;
            }
        });

        post("/api/neighbors", "application/json", (req, res) -> {
            try {
                // Parse request payload to a JSONObject with Gson
                JsonObject payload = gson.fromJson(req.body(), JsonObject.class);

                // Initialize a HexagonDto with payload to store all required data
                NeighborsDto dto = new NeighborsDto(payload);

                GeoJsonManager manager = new GeoJsonManager(dto.getNeighbors());
                String response = gson.toJson(manager.getFeatureCollection());

                return response;
            } 
            
            catch (Exception e) {
                return "Invalid JSON data provided: " + e;
            }
        });
    }
}
