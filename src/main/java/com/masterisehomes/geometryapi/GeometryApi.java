package com.masterisehomes.geometryapi;

import static spark.Spark.*;

import com.google.gson.*;
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

                // Initialize a HexagonDto with payload to store all required data
                HexagonDto dto = new HexagonDto(payload);

                // Return 
                return dto.getGeoJson();
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

                // Return 
                System.out.print(dto.getGeoJson());

                return dto.getGeoJson();
            } 
            
            catch (Exception e) {
                return "Invalid JSON data provided: " + e;
            }
        });
    }
}
