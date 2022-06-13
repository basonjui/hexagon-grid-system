package com.masterisehomes.geometryapi.hexagon;

import com.masterisehomes.geometryapi.geojson.*;
import lombok.Getter;
import lombok.ToString;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.masterisehomes.geometryapi.geojson.Geometry;

@ToString
public class HexagonDto extends GeoJSON {
    @Getter private final Hexagon hexagon;
    @Getter private final Geometry geometry;
    @Getter private final Properties properties = new Properties();
    @Getter private final Feature feature;
    @Getter private int hashCode;
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
