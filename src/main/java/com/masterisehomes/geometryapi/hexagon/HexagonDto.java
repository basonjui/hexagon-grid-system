package com.masterisehomes.geometryapi.hexagon;

import com.masterisehomes.geometryapi.geojson.*;
import lombok.Getter;
import lombok.ToString;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.masterisehomes.geometryapi.geojson.HexagonGeometry;

@ToString
public class HexagonDto extends GeoJsonManager {
    @Getter
    private final Hexagon hexagon;
    @Getter
    private final HexagonGeometry geometry;
    @Getter
    private final Feature feature;
    @Getter
    private int previousHashCode;
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public HexagonDto(Hexagon hexagon) {
        this.hexagon = hexagon;
        this.geometry = new HexagonGeometry(hexagon);
        this.feature = new Feature(this.geometry);
    }

    // Methods
    public void addProperty(Object key, Object value) {
        this.feature.addProperty(key, value);
    }

    public HexagonDto build() {
        int currentHashCode = this.getHashCode();

        if (this.previousHashCode != currentHashCode) {
            this.addFeature(this.feature);
            this.previousHashCode = currentHashCode;
        }

        return this;
    }

    // Getter
    public String toGeoJSON() {
        return gson.toJson(this.getFeatureCollection());
    }
}
