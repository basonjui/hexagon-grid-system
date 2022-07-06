package com.masterisehomes.geometryapi;

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

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.JsonSyntaxException;
import com.masterisehomes.geometryapi.geojson.FeatureCollection;
import com.masterisehomes.geometryapi.geojson.GeoJsonManager;
import com.masterisehomes.geometryapi.hexagon.Coordinates;
import com.masterisehomes.geometryapi.hexagon.Hexagon;
import com.masterisehomes.geometryapi.hexagon.HexagonDto;



// Handler value: example.HandlerStream
public class HandlerStream implements RequestStreamHandler {
  Gson gson = new Gson();

  @Override
  public void handleRequest(InputStream inputStream, OutputStream outputStream, Context context) throws IOException {
    LambdaLogger logger = context.getLogger();
    BufferedReader reader = new BufferedReader(
      new InputStreamReader(inputStream, Charset.forName("US-ASCII")));
    PrintWriter writer = new PrintWriter(
        new BufferedWriter(new OutputStreamWriter(outputStream, Charset.forName("US-ASCII")))); // can change to US-ASCII
        
    try {
      // Gson to HashMap<String, Object> - specify Object value will respect the type
      Type stringObjectMap = new TypeToken<Map<String, Object>>(){}.getType();
      Map<String, Object> event = gson.fromJson(reader, stringObjectMap);
      logger.log("STREAM TYPE: " + inputStream.getClass().toString());
      logger.log("EVENT TYPE: " + event.getClass().toString());

      Coordinates centroid = new Coordinates(106.69901892529006, 10.779922217270345);
      Hexagon hex = new Hexagon(centroid, 500);
      // HexagonDto testDto = new HexagonDto(event);
      HexagonDto dto = new HexagonDto(hex);
      GeoJsonManager manager = new GeoJsonManager(dto.getHexagon());
      FeatureCollection collection = manager.getFeatureCollection();

      // Generate DTO from event HashMap
      // HexagonDto dto = new HexagonDto(event);

      // GeoJsonManager
      // GeoJsonManager manager = new GeoJsonManager(dto.getHexagon());
      // FeatureCollection collection = manager.getFeatureCollection();

      writer.write(gson.toJson(event));
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