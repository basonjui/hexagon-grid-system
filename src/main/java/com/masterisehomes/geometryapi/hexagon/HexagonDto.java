package com.masterisehomes.geometryapi.hexagon;

import java.util.ArrayList;
import java.util.List;

import com.masterisehomes.geometryapi.geojson.*;

import lombok.Setter;
import lombok.Getter;
import lombok.ToString;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

// This class acts as 

@Getter
@ToString
public class HexagonDto {
    private final String type = "Polygon";
    @Setter
    private List<List<Double>> coordinates;

    public HexagonDto() {
    }

    public HexagonDto(Hexagon hexagon) {
        List<Coordinates> verticesCoordinates = hexagon.getVertices();
        this.coordinates = toGeoJsonCoordinates(verticesCoordinates);
    }

    private List<List<Double>> toGeoJsonCoordinates(List<Coordinates> hexVertices) {
        List<List<Double>> geoJsonCoordinates = new ArrayList<List<Double>>();

        hexVertices.forEach((vertex) -> geoJsonCoordinates.add(vertex.toGeoJsonFormat()));
        return geoJsonCoordinates;
    }

    public static void main(String[] args) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Coordinates centroid = new Coordinates(100, 200);
        Hexagon hex = new Hexagon(centroid, 50);
        HexagonDto dto = new HexagonDto(hex);

        // System.out.println(dto.getCoordinates());

        Geometry geom = new Geometry(hex);
        GeoJSON geojson = new GeoJSON.Builder()
                            .geometry(geom)
                            .build();
        
        System.out.println(gson.toJson(geojson));

    }
}
