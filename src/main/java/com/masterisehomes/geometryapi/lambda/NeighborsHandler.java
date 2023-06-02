package com.masterisehomes.geometryapi.lambda;

import java.util.Map;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import com.masterisehomes.geometryapi.neighbors.NeighborsDto;
import com.masterisehomes.geometryapi.geojson.GeoJsonManager;

public class NeighborsHandler implements RequestHandler<Map<String, Object>, String> {
	private static final Logger logger = LoggerFactory.getLogger(NeighborsHandler.class);
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
		NeighborsDto dto = new NeighborsDto(event);
		GeoJsonManager manager = new GeoJsonManager(dto.getNeighbors());

		return gson.toJson(manager.getFeatureCollection());
	}
}