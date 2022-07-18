package com.masterisehomes.geometryapi;

import static spark.Spark.*;

import com.google.gson.*;

import com.masterisehomes.geometryapi.geojson.FeatureCollection;
import com.masterisehomes.geometryapi.geojson.GeoJsonManager;
import com.masterisehomes.geometryapi.hexagon.*;
import com.masterisehomes.geometryapi.neighbors.*;
import com.masterisehomes.geometryapi.utils.JsonTransformer;

public class GeometryApi {
    public final static Gson gson = new GsonBuilder().setPrettyPrinting().create();
    // public final static Gson gson = new Gson();

    public static void main(String[] args) {
        before((request, response) -> response.type("application/json"));

        post("/api/hexagon", "application/json", (request, response) -> {
            try {
                // Parse request payload to a JSONObject with Gson
                JsonObject payload = gson.fromJson(request.body(), JsonObject.class);

                // Initialize a HexagonDto with payload to get all required data
                HexagonDto dto = new HexagonDto(payload);
                // From dto, you can get lat, long, centroid, radius, hexagon

                // GeoJsonManager handles all GeoJSON operations
                GeoJsonManager manager = new GeoJsonManager(dto.getHexagon());
                FeatureCollection collection = manager.getFeatureCollection();

                // Return GeoJSON response
                return collection;
            }

            catch (Exception e) {
                return "Invalid JSON data provided: " + e;
            }
        }, new JsonTransformer());

        post("/api/neighbors", "application/json", (request, response) -> {
            try {
                // Parse request payload to a JSONObject with Gson
                JsonObject payload = gson.fromJson(request.body(), JsonObject.class);

                // Initialize a HexagonDto with payload to store all required data
                NeighborsDto dto = new NeighborsDto(payload);

                GeoJsonManager manager = new GeoJsonManager(dto.getNeighbors());
                FeatureCollection collection = manager.getFeatureCollection();

                return collection;
            }

            catch (Exception e) {
                return "Invalid JSON data provided: " + e;
            }
        }, new JsonTransformer());
    }
}
