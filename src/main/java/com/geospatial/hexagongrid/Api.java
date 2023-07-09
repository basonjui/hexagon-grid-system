package com.geospatial.hexagongrid;

import java.util.Set;

import com.geospatial.hexagongrid.database.PostgresJDBC;
import com.geospatial.hexagongrid.geojson.FeatureCollection;
import com.geospatial.hexagongrid.geojson.GeoJsonManager;
import com.geospatial.hexagongrid.hexagon.*;
import com.geospatial.hexagongrid.neighbors.*;
import com.geospatial.hexagongrid.tessellation.Boundary;
import com.geospatial.hexagongrid.tessellation.CornerEdgeTessellation;
import com.geospatial.hexagongrid.tessellation.CornerEdgeTessellationDto;
import com.geospatial.hexagongrid.utils.JVMUtils;
import com.geospatial.hexagongrid.utils.JsonTransformer;
import com.google.gson.*;
import static spark.Spark.*;

public class Api {
	public final static Gson gson = new Gson();

	// Default port for Spark, you can change this if you want
	public final static int port = 4567;

	public static void main(String[] args) {
		port(port);
		before((request, response) -> response.type("application/json"));

		post("/api/hexagon", "application/json", (request, response) -> {
			try {
				// Parse request payload to a JSONObject with Gson
				JsonObject payload = gson.fromJson(request.body(), JsonObject.class);

				// Initialize a HexagonDto with payload to get all required data
				HexagonDto dto = new HexagonDto(payload);
				GeoJsonManager manager = new GeoJsonManager(dto.getHexagon());
				FeatureCollection collection = manager.getFeatureCollection();
				
				return collection;

			} catch (Exception e) {
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

			} catch (Exception e) {
				return "Invalid JSON data provided: " + e;
			}

		}, new JsonTransformer());

		post("/api/tessellation", "application/json", (request, response) -> {
			try {
				// Parse request payload to a JSONObject with Gson
				JsonObject tessellationPayload = gson.fromJson(request.body(), JsonObject.class);

				CornerEdgeTessellationDto tessellationDto = new CornerEdgeTessellationDto(
						tessellationPayload);
				GeoJsonManager manager = new GeoJsonManager(tessellationDto.getTessellation());

				return manager.getFeatureCollection();

			} catch (Exception e) {
				return "Invalid JSON data provided: " + e;
			}

		}, new JsonTransformer());

		post("/database/tessellation", "application/json", (request, response) -> {
			JsonObject status = new JsonObject();

			try {
				// Parse request payload to a JSONObject with Gson
				JsonObject payload = gson.fromJson(request.body(), JsonObject.class);

				// Check payload for required keys
				boolean validKeys = false;
				boolean validBoundary = false;
				boolean validPayload = false;

				// Required keys for the request payload
				Set<String> requiredKeys = Set.of("administrativeName", "latitude", "longitude", "radius", "boundary");
				if (payload.keySet().equals(requiredKeys)) {
					// If payload has all required keys, then validKeys
					validKeys = true;

					// Continue to check members of payload key `boundary`
					JsonObject boundary = payload.get("boundary").getAsJsonObject();
					Set<String> requiredBoundaryKeys = Set.of("minLatitude", "minLongitude", "maxLatitude", "maxLongitude");
					if (boundary.keySet().equals(requiredBoundaryKeys)) {
						validBoundary = true;
					}
				} else {
					System.out.println("Invalid payload keys: " + payload.keySet());
				}

				// If both keys and boundary's members are valid, then the payload is valid
				validPayload = validKeys && validBoundary;
				if (validPayload) {
					// Start PostgresJDBC connection
					PostgresJDBC pg = new PostgresJDBC.Builder()
							.host("POSTGRES_HOST")
							.port(5432)
							.database("POSTGRES_DATABASE")
							.authentication("POSTGRES_USERNAME", "POSTGRES_PASSWORD")
							.reWriteBatchedInserts(true) // Optional
							.build();

					// Extract Hexagon data from payload
					final double longitude = payload.get("longitude").getAsDouble();
					final double latitude = payload.get("latitude").getAsDouble();
					final int circumradius = payload.get("radius").getAsInt();

					// Create Hexagon
					final Coordinates centroid = new Coordinates(longitude, latitude);
					final Hexagon hexagon = new Hexagon(centroid, circumradius);

					// Extract Boundary data from payload
					JsonObject boundaryJsonObject = payload.get("boundary").getAsJsonObject();
					final double minLat = boundaryJsonObject.get("minLatitude").getAsDouble();
					final double minLng = boundaryJsonObject.get("minLongitude").getAsDouble();
					final double maxLat = boundaryJsonObject.get("maxLatitude").getAsDouble();
					final double maxLng = boundaryJsonObject.get("maxLongitude").getAsDouble();

					// Create Boundary
					final Coordinates minBoundaryCoordinates = new Coordinates(minLng, minLat);
					final Coordinates maxBoundaryCoordinates = new Coordinates(maxLng, maxLat);
					final Boundary boundary = new Boundary(minBoundaryCoordinates,
							maxBoundaryCoordinates);

					// Create Tessellation
					final CornerEdgeTessellation tessellation = new CornerEdgeTessellation(hexagon);
					tessellation.tessellate(boundary);

					// Create table name
					System.out.println("--- Database Configs ---");
					final String TESSELLATION_TABLE_NAME = "%s_tessellation_%sm";
					final String administrativeName = payload.get("administrativeName")
							.getAsString();
					final String tableName = String.format(
							TESSELLATION_TABLE_NAME,
							administrativeName,
							circumradius);
					System.out.println("Table name: " + tableName);

					// Database executions
					JsonObject createTableStatus;
					createTableStatus = pg.createTessellationTable(tableName);
					status.add("createTessellationTable", createTableStatus);

					JsonObject batchInsertStatus;
					batchInsertStatus = pg.batchInsertTessellation(tableName, tessellation);
					status.add("batchInsertTessellation", batchInsertStatus);

					JsonObject addPrimaryKeyStatus;
					addPrimaryKeyStatus = pg.addPrimaryKeyIfNotExists(tableName);
					status.add("addPrimaryKeyIfNotExists", addPrimaryKeyStatus);

					JVMUtils.printMemoryUsages("MB");
				}

			} catch (Exception e) {
				status.addProperty("error", e.toString());
			}

			return status;

		}, new JsonTransformer());

	}
}
