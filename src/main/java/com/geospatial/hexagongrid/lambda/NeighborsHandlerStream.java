package com.geospatial.hexagongrid.lambda;

import java.util.Map;
import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.BufferedWriter;
import java.nio.charset.Charset;
import java.lang.IllegalStateException;
import java.lang.reflect.Type;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.JsonSyntaxException;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.geospatial.hexagongrid.geojson.FeatureCollection;
import com.geospatial.hexagongrid.geojson.GeoJsonManager;
import com.geospatial.hexagongrid.neighbors.NeighborsDto;

public class NeighborsHandlerStream implements RequestStreamHandler {
	Gson gson = new Gson();

	@Override
	public void handleRequest(InputStream inputStream, OutputStream outputStream, Context context)
			throws IOException {
		LambdaLogger logger = context.getLogger();
		BufferedReader reader = new BufferedReader(
				new InputStreamReader(inputStream, Charset.forName("US-ASCII")));
		PrintWriter writer = new PrintWriter(
				new BufferedWriter(new OutputStreamWriter(outputStream, Charset.forName("US-ASCII"))));

		try {
			Type stringObjectMap = new TypeToken<Map<String, Object>>() {
			}.getType();
			Map<String, Object> event = gson.fromJson(reader, stringObjectMap);
			logger.log("STREAM TYPE: " + inputStream.getClass().toString());
			logger.log("EVENT TYPE: " + event.getClass().toString());

			// Generate DTO from event Map
			NeighborsDto dto = new NeighborsDto(event);
			GeoJsonManager manager = new GeoJsonManager(dto.getNeighbors());
			FeatureCollection collection = manager.getFeatureCollection();

			// Write JSON result to output stream
			writer.write(gson.toJson(collection));
			if (writer.checkError()) {
				logger.log("WARNING: Writer encountered an error.");
			}
		}

		catch (IllegalStateException | JsonSyntaxException exception) {
			logger.log(exception.toString());
		}

		finally {
			reader.close();
			writer.close();
		}
	}
}