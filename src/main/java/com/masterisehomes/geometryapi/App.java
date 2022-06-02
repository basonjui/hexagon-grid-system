package com.masterisehomes.geometryapi;

import static spark.Spark.*;

import com.google.gson.*;
import com.masterisehomes.geometryapi.hexagon.*;

/**
 * Hello world!
 *
 */
public class App {
    public final static Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static void main(String[] args) {

        // String jsonString = "{\"latitude\": 150, \"longitude\": 150, \"radius\":
        // 100}";

        // System.out.println(jsonObject.get("latitude").getAsDouble());

        post("/api/hexagon", "application/json", (req, res) -> {
            try {
                // Parse request payload to a JSONObject with Gson
                JsonObject jsonObj = gson.fromJson(req.body(), JsonObject.class);
                // Get GIS data from payload with keys
                Double latitude = jsonObj.get("latitude").getAsDouble();
                Double longitude = jsonObj.get("longitude").getAsDouble();
                Integer circumradius = jsonObj.get("radius").getAsInt();

                // Initialize a hexagon with client's data
                Coordinates clientCentroid = new Coordinates(latitude, longitude);
                Hexagon clientHexagon = new Hexagon(clientCentroid, circumradius);

                // Get data from DTO
                HexagonDto hexDto = new HexagonDto(clientHexagon);

                return gson.toJson(hexDto);

            } catch (Exception e) {
                return "Invalid JSON data provided: " + e;
            }
        });

        get("/api/neighbors", (req, res) -> {
            return "Neighbor API Endpoint";
        });
    }
}
