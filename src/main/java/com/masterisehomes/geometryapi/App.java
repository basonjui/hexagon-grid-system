package com.masterisehomes.geometryapi;

import static spark.Spark.*;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.Gson.*;
import com.masterisehomes.geometryapi.hexagon.*;

/**
 * Hello world!
 *
 */
public class App {

    public static void main(String[] args) {
        Gson gson = new Gson();


        String jsonString = "{\"latitude\": 150, \"longitude\": 150, \"radius\": 100}";

        JsonObject jsonObject = gson.fromJson(jsonString, JsonObject.class);


        System.out.println(jsonObject.get("latitude").getAsDouble());

        // post("/api/hexagon", "application/json", (req, res) -> {
        //     Double latitude = Double.parseDouble(req.queryParams("latitude"));
        //     Double longitude = Double.parseDouble(req.queryParams("longitude"));
        //     Integer circumradius = Integer.parseInt(req.queryParams("radius"));

        //     Coordinates hexCoord = new Coordinates(latitude, longitude);
        //     Hexagon hexagon = new Hexagon(hexCoord, circumradius);

            

        //     return gson.toJson(req.body());
        // });

        // get("/api/neighbors", (req, res) -> {
        //     return "Neighbor API Endpoint";
        // });
    }
}
