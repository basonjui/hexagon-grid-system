package com.masterisehomes.geometryapi.hexagon;

import com.masterisehomes.geometryapi.geojson.*;
import lombok.Getter;
import lombok.ToString;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.masterisehomes.geometryapi.geojson.Geometry;

@Getter
@ToString
public class HexagonDto extends GeoJSON {
    private final Hexagon hexagon;
    private final Geometry geometry;
    private final Properties properties = new Properties();
    private final Feature feature;
    private int hashCode;
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public HexagonDto(Hexagon hexagon) {
        this.hexagon = hexagon;
        this.geometry = new Geometry(hexagon);
        this.feature = new Feature(geometry);
    }

    // Methods
    public void addProperty(Object key, Object value) {
        this.feature.addProperty(key, value);
    }

    public HexagonDto build() {
        // Need to add logic to make sure that feature will only add once,
        // even if .build() is called multiple times
        int currentHashCode = this.getFeatureCollection().hashCode();

        if (this.hashCode != currentHashCode) {
            this.addFeature(this.feature);
            this.hashCode = currentHashCode;
        }

        return this;
    }

    // Getter
    public String toGeoJSON() {
        return gson.toJson(this.getFeatureCollection());
    }
}
