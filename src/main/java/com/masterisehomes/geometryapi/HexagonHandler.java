package com.masterisehomes.geometryapi;

import java.util.Map;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.masterisehomes.geometryapi.hexagon.HexagonDto;
import com.masterisehomes.geometryapi.geojson.GeoJsonManager;
import com.masterisehomes.geometryapi.geojson.FeatureCollection;

public class HexagonHandler implements RequestHandler<Map<String, String>, String> {
    Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @Override
    public String handleRequest(Map<String, String> event, Context context) {
        // Logger
        LambdaLogger logger = context.getLogger();
        // log execution details
        logger.log("ENVIRONMENT VARIABLES: " + gson.toJson(System.getenv()));
        logger.log("CONTEXT: " + gson.toJson(context));
        // process event
        logger.log("EVENT: " + gson.toJson(event));
        logger.log("EVENT TYPE: " + event.getClass().toString());

        // Initialize a HexagonDto with payload to get all required data
        HexagonDto dto = new HexagonDto(event);
        // Instantiate a GeoJsonManager for a Hexagon
        GeoJsonManager manager = new GeoJsonManager(dto.getHexagon());
        // Use GeoJsonManager to generate FeatureCollection object
        FeatureCollection collection = manager.getFeatureCollection();

        logger.log("COLLECTION: " + gson.toJson(collection));
        String response = gson.toJson(collection);

        return response;

    }
}