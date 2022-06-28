package com.masterisehomes.geometryapi.neighbors; 

import com.masterisehomes.geometryapi.geojson.*;
import com.masterisehomes.geometryapi.hexagon.*;
import lombok.Getter;
import lombok.ToString;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


@ToString
public class NeighborsDto extends GeoJsonManager {
    @Getter
    private final Hexagon rootHexagon;
    @Getter
    private final NeighborsGeometry geometry;
    @Getter
    private final Feature feature;
    @Getter
    private int previousHashCode;
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public NeighborsDto(Neighbors neighbors) {
        this.rootHexagon = neighbors.getRootHexagon();
        this.geometry = new NeighborsGeometry(neighbors);
        this.feature = new Feature(this.geometry);
    }

    // Methods
    // TODO: need to update build logic
    public NeighborsDto build() {
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
