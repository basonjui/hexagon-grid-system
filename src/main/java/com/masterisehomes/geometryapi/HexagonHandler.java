// package com.masterisehomes.geometryapi;

// // import java.util.Map;
// import com.google.gson.Gson;
// import com.google.gson.GsonBuilder;
// import com.google.gson.JsonObject;
// import com.amazonaws.services.lambda.runtime.Context;
// import com.amazonaws.services.lambda.runtime.RequestHandler;
// import com.amazonaws.services.lambda.runtime.LambdaLogger;
// import com.masterisehomes.geometryapi.hexagon.HexagonDto;
// import com.masterisehomes.geometryapi.geojson.GeoJsonManager;
// import com.masterisehomes.geometryapi.geojson.FeatureCollection;

// public class HexagonHandler implements RequestHandler<Object, String> {
//     Gson gson = new GsonBuilder().setPrettyPrinting().create();

//     @Override
//     public String handleRequest(Object event, Context context) {
//         // Logger
//         LambdaLogger logger = context.getLogger();
//         // log execution details
//         logger.log("ENVIRONMENT VARIABLES: " + gson.toJson(System.getenv()));
//         logger.log("CONTEXT: " + gson.toJson(context));
//         // process event
//         logger.log("EVENT: " + gson.toJson(event));
//         logger.log("EVENT TYPE: " + event.getClass().toString());

//         // Format event
//         String json = gson.toJson(event);
//         JsonObject data = gson.fromJson(json, JsonObject.class);

//         // Initialize a HexagonDto with payload to get all required data
//         HexagonDto dto = new HexagonDto(data);
//         logger.log("DTO: " + gson.toJson(dto));

//         // Instantiate a GeoJsonManager for a Hexagon
//         GeoJsonManager manager = new GeoJsonManager(dto.getHexagon());
//         logger.log("GEOJSONMANAGER: " + gson.toJson(manager));

//         // Use GeoJsonManager to generate FeatureCollection object
//         FeatureCollection collection = manager.getFeatureCollection();
//         logger.log("COLLECTION: " + gson.toJson(collection));

//         // Craft Lambda payload (either version 1.0 or 2.0)
//         JsonObject response = new JsonObject();
//         response.addProperty("statusCode", 200);
//         response.addProperty("body", gson.toJson(collection));

//         return gson.toJson(collection);
//     }
// }