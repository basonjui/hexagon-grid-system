package com.geospatial.hexagongrid.neighbors;

import java.util.Map;

import lombok.Getter;
import lombok.ToString;

import com.geospatial.hexagongrid.hexagon.*;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

@ToString
public class NeighborsDto {
	@Getter
	private final double rootLatitude, rootLongitude, circumradius;
	@Getter
	private final Coordinates rootCentroid;
	@Getter
	private final Hexagon rootHexagon;
	@Getter
	private final Neighbors neighbors;

	final static private Gson gson = new Gson();

	public NeighborsDto(Map<String, Object> lambdaEvent) {
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
		String eventBodyJson = lambdaEvent.get("body").toString(); // lambdaEvent.get("body") returns a
										// JsonPrimitive
		JsonObject eventBody = gson.fromJson(eventBodyJson, JsonObject.class);

		// Get latitude, longitude, and radius data (JsonPrimitive) from eventBody
		this.rootLatitude = eventBody.get("latitude").getAsDouble();
		this.rootLongitude = eventBody.get("longitude").getAsDouble();
		this.circumradius = eventBody.get("radius").getAsDouble();

		// Construct Hexagon
		this.rootCentroid = new Coordinates(this.rootLongitude, this.rootLatitude);
		this.rootHexagon = new Hexagon(this.rootCentroid, this.circumradius);

		// Construct Neighbors
		this.neighbors = new Neighbors(this.rootHexagon);
	}

	public NeighborsDto(Hexagon rootHexagon) {
		this.rootHexagon = rootHexagon;
		this.rootCentroid = rootHexagon.getCentroid();
		this.rootLatitude = this.rootCentroid.getLatitude();
		this.rootLongitude = this.rootCentroid.getLongitude();
		this.circumradius = this.rootHexagon.getCircumradius();
		this.neighbors = new Neighbors(rootHexagon);
	}

	public NeighborsDto(JsonObject payload) {
		this.rootLatitude = payload.get("latitude").getAsDouble();
		this.rootLongitude = payload.get("longitude").getAsDouble();
		this.circumradius = payload.get("radius").getAsDouble();

		this.rootCentroid = new Coordinates(this.rootLongitude, this.rootLatitude);
		this.rootHexagon = new Hexagon(this.rootCentroid, this.circumradius);
		this.neighbors = new Neighbors(this.rootHexagon);
	}
}