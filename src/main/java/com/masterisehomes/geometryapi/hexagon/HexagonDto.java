package com.masterisehomes.geometryapi.hexagon;

import java.util.Map;

import lombok.Getter;
import lombok.ToString;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

@ToString
public class HexagonDto {
    @Getter
    private double latitude, longitude, circumradius;
    @Getter
    private Coordinates centroid;
    @Getter
    private Hexagon hexagon;
    
    final static private Gson gson = new Gson();

    public HexagonDto(Map<String, Object> lambdaEvent) {
        /*
         * Lambda Event is a Json, which is converted to a Map in order to get values by
         * keys
         * 
         * In general, a LambdaEvent JSON contains all the metadata about the Client
         * that sends a HTTP Request -> API Gateway -> Lambda Function
         * 
         * In this Event (JSON), the "body" key contains actual data sent from the
         * Client, which
         * consists of the following keys (ordered - although does not matter in this
         * case):
         * - latitude
         * - longitude
         * - radius
         */

        // Get body contents as String & convert to a JsonObject (to get inner keys)
        String eventBodyJson = lambdaEvent.get("body").toString(); // lambdaEvent.get("body") returns a JsonPrimitive
        JsonObject eventBody = gson.fromJson(eventBodyJson, JsonObject.class);

        // Get latitude, longitude, and radius data (JsonPrimitive) from eventBody
        this.latitude = eventBody.get("latitude").getAsDouble();
        this.longitude = eventBody.get("longitude").getAsDouble();
        this.circumradius = eventBody.get("radius").getAsDouble();

        // Construct centroid, hexagon and store them in DTO instance
        this.centroid = new Coordinates(this.longitude, this.latitude);
        this.hexagon = new Hexagon(this.centroid, this.circumradius);
    }

    public HexagonDto(Hexagon hexagon) {
        // TODO: need to initialize latitude, longitude
        this.hexagon = hexagon;
        this.centroid = this.hexagon.getCentroid();
        this.circumradius = this.hexagon.getCircumradius();
    }
}
