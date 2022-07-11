package com.masterisehomes.geometryapi;

import java.util.Map;

import com.google.gson.Gson;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import com.masterisehomes.geometryapi.hexagon.HexagonDto;
import com.masterisehomes.geometryapi.geojson.GeoJsonManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HexagonHandler implements RequestHandler<Map<String, Object>, String> {
    private static final Logger logger = LoggerFactory.getLogger(HexagonHandler.class);
    private static final Gson gson = new Gson();

    @Override
    public String handleRequest(Map<String, Object> event, Context context) {
        // log execution details
        logger.info("ENVIRONMENT VARIABLES: " + gson.toJson(System.getenv()));
        logger.info("CONTEXT: " + gson.toJson(context));

        // process event
        logger.info("EVENT TYPE: " + event.getClass().toString());
        logger.info("EVENT KEYS: " + event.keySet());
        logger.info("EVENT: " + gson.toJson(event));

        // Parse event map to DTO to extract and store data
        HexagonDto dto = new HexagonDto(event);
        GeoJsonManager manager = new GeoJsonManager(dto.getHexagon());

        return gson.toJson(manager.getFeatureCollection());
    }
}