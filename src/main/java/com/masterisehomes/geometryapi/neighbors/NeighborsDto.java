package com.masterisehomes.geometryapi.neighbors; 

import com.masterisehomes.geometryapi.geojson.*;
import com.masterisehomes.geometryapi.hexagon.*;
import lombok.Getter;
import lombok.ToString;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


@ToString
public class NeighborsDto extends GeoJSON {
    @Getter
    private final Hexagon rootHexagon;
    @Getter
    private final NeighborsGeometry geometry;
    @Getter
    private final Properties properties = new Properties();
    @Getter
    private final Feature feature;
    @Getter
    private int hashCode;
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public NeighborsDto(Neighbors neighbors) {
        this.rootHexagon = neighbors.getRootHexagon();
        this.geometry = new NeighborsGeometry(neighbors);
        this.feature = new Feature(this.geometry);
    }

    // Methods
    public NeighborsDto build() {
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
