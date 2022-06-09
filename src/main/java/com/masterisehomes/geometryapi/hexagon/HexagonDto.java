package com.masterisehomes.geometryapi.hexagon;

import com.masterisehomes.geometryapi.geojson.*;
import lombok.Setter;
import lombok.Getter;
import lombok.ToString;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.masterisehomes.geometryapi.geojson.Geometry;

// This class acts as 

@Getter
@ToString
public class HexagonDto {
    public Hexagon hexagon;
    public Geometry geometry;
    public GeoJSON geoJson;

    public HexagonDto(Hexagon hexagon) {
        this.hexagon = hexagon;
        this.geometry = new Geometry(hexagon);
    }

    public static void main(String[] args) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Coordinates centroid = new Coordinates(100, 200);
        Hexagon hex = new Hexagon(centroid, 50);

        // System.out.println(dto.getCoordinates());
        
        Geometry geom = new Geometry(hex);
        GeoJSON geojson = new GeoJSON.Builder()
                            .geometry(geom)
                            .build();
        
        System.out.println(gson.toJson(geojson));

    }
}
