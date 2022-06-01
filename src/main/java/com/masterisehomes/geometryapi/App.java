package com.masterisehomes.geometryapi;

import static spark.Spark.*;

import com.google.gson.*;
import com.masterisehomes.geometryapi.hexagon.*;

/**
 * Hello world!
 *
 */
public class App {

    public static void main(String[] args) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        get("/api/hexagon", (req, res) -> {
            Double latitude = Double.parseDouble(req.queryParams("latitude"));
            Double longitude = Double.parseDouble(req.queryParams("longitude"));
            Integer circumradius = Integer.parseInt(req.queryParams("radius"));

            Coordinates hexCoord = new Coordinates(latitude, longitude);
            Hexagon hexagon = new Hexagon(hexCoord, circumradius);

            return gson.toJson(hexagon.getVertices());
        });

        get("/api/neighbors", (req, res) -> {
            return "Neighbor API Endpoint";
        });
    }
}
